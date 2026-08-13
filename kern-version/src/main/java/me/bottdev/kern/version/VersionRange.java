package me.bottdev.kern.version;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class VersionRange {

    private final List<List<VersionComparator>> comparatorSets;

    public VersionRange(@NonNull List<List<VersionComparator>> comparatorSets) {
        this.comparatorSets = List.copyOf(comparatorSets);
    }

    public boolean satisfies(@NonNull SemVersion version) {

        for (List<VersionComparator> andSet : comparatorSets) {

            boolean matchesAll = true;
            for (VersionComparator comp : andSet) {
                if (!comp.isSatisfiedBy(version)) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) return true;
        }

        return false;

    }

    /// True if this range can never be satisfied by any version (e.g. the result of
    /// intersecting two ranges with no overlap). Mirrors {@link #satisfies}: an empty
    /// OR-set of comparator groups means nothing ever matches.
    public boolean isEmpty() {
        return comparatorSets.isEmpty();
    }

    /// Intersects this range with `other`, returning a new range that is satisfied only
    /// by versions both ranges would accept. Each AND-group (interval) of this range is
    /// intersected against each AND-group of `other`; non-empty results are kept as the
    /// new range's OR-groups. If nothing overlaps, the result {@link #isEmpty()}.
    public VersionRange intersect(@NonNull VersionRange other) {

        List<List<VersionComparator>> resultSets = new ArrayList<>();

        for (List<VersionComparator> setA : this.comparatorSets) {
            Interval intervalA = Interval.fold(setA);
            if (intervalA == null) continue;

            for (List<VersionComparator> setB : other.comparatorSets) {
                Interval intervalB = Interval.fold(setB);
                if (intervalB == null) continue;

                Interval intersected = intervalA.intersect(intervalB);
                if (intersected != null) {
                    resultSets.add(intersected.toComparators());
                }
            }
        }

        return new VersionRange(resultSets);
    }

    /// A canonical half-open/closed interval `[lower, upper]` folded from one AND-group
    /// of comparators. `null` bound means unbounded on that side (-infinity / +infinity).
    private record Interval(
            SemVersion lower,
            boolean lowerInclusive,
            SemVersion upper,
            boolean upperInclusive
    ) {

        private static final Interval UNBOUNDED = new Interval(null, true, null, true);

        /// Folds an AND-group of comparators into a single interval by successively
        /// tightening the lower/upper bound. Returns null if the group is internally
        /// unsatisfiable (e.g. `>2.0.0` combined with `<1.0.0`).
        static Interval fold(List<VersionComparator> andSet) {

            Interval current = UNBOUNDED;

            for (VersionComparator comp : andSet) {
                SemVersion v = comp.getVersion();

                Interval candidate = switch (comp.getOperator()) {
                    case "=" -> new Interval(v, true, v, true);
                    case ">" -> new Interval(v, false, null, true);
                    case ">=" -> new Interval(v, true, null, true);
                    case "<" -> new Interval(null, true, v, false);
                    case "<=" -> new Interval(null, true, v, true);
                    default -> throw new IllegalArgumentException("Unknown operator: " + comp.getOperator());
                };

                current = current.intersect(candidate);
                if (current == null) return null;
            }

            return current;
        }

        /// Intersects two intervals, returning null if they don't overlap.
        Interval intersect(Interval other) {

            SemVersion newLower;
            boolean newLowerInclusive;
            if (this.lower == null) {
                newLower = other.lower;
                newLowerInclusive = other.lowerInclusive;
            } else if (other.lower == null) {
                newLower = this.lower;
                newLowerInclusive = this.lowerInclusive;
            } else {
                int cmp = compare(this.lower, other.lower);
                if (cmp > 0) {
                    newLower = this.lower;
                    newLowerInclusive = this.lowerInclusive;
                } else if (cmp < 0) {
                    newLower = other.lower;
                    newLowerInclusive = other.lowerInclusive;
                } else {
                    newLower = this.lower;
                    // same boundary version: exclusive is stricter, wins if either side excludes it
                    newLowerInclusive = this.lowerInclusive && other.lowerInclusive;
                }
            }

            SemVersion newUpper;
            boolean newUpperInclusive;
            if (this.upper == null) {
                newUpper = other.upper;
                newUpperInclusive = other.upperInclusive;
            } else if (other.upper == null) {
                newUpper = this.upper;
                newUpperInclusive = this.upperInclusive;
            } else {
                int cmp = compare(this.upper, other.upper);
                if (cmp < 0) {
                    newUpper = this.upper;
                    newUpperInclusive = this.upperInclusive;
                } else if (cmp > 0) {
                    newUpper = other.upper;
                    newUpperInclusive = other.upperInclusive;
                } else {
                    newUpper = this.upper;
                    newUpperInclusive = this.upperInclusive && other.upperInclusive;
                }
            }

            if (newLower != null && newUpper != null) {
                int cmp = compare(newLower, newUpper);
                if (cmp > 0) return null; // lower above upper: empty
                if (cmp == 0 && !(newLowerInclusive && newUpperInclusive)) return null; // single point excluded
            }

            return new Interval(newLower, newLowerInclusive, newUpper, newUpperInclusive);
        }

        /// Converts this interval back into an AND-group of comparators. An unbounded
        /// interval (-infinity, +infinity) becomes an empty list, i.e. "no constraint" —
        /// consistent with how {@link VersionRange#satisfies} treats an empty AND-group.
        List<VersionComparator> toComparators() {
            List<VersionComparator> result = new ArrayList<>();
            if (lower != null) {
                result.add(new VersionComparator(lowerInclusive ? ">=" : ">", lower));
            }
            if (upper != null) {
                result.add(new VersionComparator(upperInclusive ? "<=" : "<", upper));
            }
            return result;
        }

        private static int compare(SemVersion a, SemVersion b) {
            if (a.isEqual(b)) return 0;
            return a.isGreaterThan(b) ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        if (comparatorSets.isEmpty()) {
            return "*";
        }

        return comparatorSets.stream()
                .filter(andSet -> !andSet.isEmpty())
                .map(andSet -> andSet.stream()
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.joining(" ")))
                .filter(str -> !str.isEmpty())
                .collect(java.util.stream.Collectors.joining(" || "));
    }

}