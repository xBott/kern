package me.bottdev.kern.version;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
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

}