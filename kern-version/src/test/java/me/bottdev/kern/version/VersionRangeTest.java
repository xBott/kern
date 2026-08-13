package me.bottdev.kern.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRangeTest {

    @Nested
    @DisplayName("Constructor and basic properties")
    class Constructor {

        @Test
        @DisplayName("constructor: null list throws NullPointerException")
        void constructor_nullThrows() {
            assertThatThrownBy(() -> new VersionRange(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("constructor: constructs correctly with valid comparator sets")
        void constructor_valid() {
            SemVersion version = new SemVersion(1, 0, 0, null, null);
            VersionComparator comp = new VersionComparator("=", version);
            List<List<VersionComparator>> sets = List.of(List.of(comp));

            VersionRange range = new VersionRange(sets);
            assertThat(range.comparatorSets()).containsExactlyElementsOf(sets);
        }

    }

    @Nested
    @DisplayName("Range satisfying tests")
    class Satisfies {

        @Test
        @DisplayName("satisfies: matching single exact version")
        void satisfies_exact() {
            VersionRange range = VersionRangeParser.parse("=1.2.3");
            assertThat(range.satisfies(SemVersionParser.parse("1.2.3"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.2.4"))).isFalse();
        }

        @Test
        @DisplayName("satisfies: matching single version without operator defaults to equal")
        void satisfies_defaultOperator() {
            VersionRange range = VersionRangeParser.parse("1.2.3");
            assertThat(range.satisfies(SemVersionParser.parse("1.2.3"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.2.4"))).isFalse();
        }

        @Test
        @DisplayName("satisfies: matching AND ranges (space separated)")
        void satisfies_andRange() {
            VersionRange range = VersionRangeParser.parse(">=1.0.0 <2.0.0");
            assertThat(range.satisfies(SemVersionParser.parse("1.0.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.5.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("2.0.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("0.9.9"))).isFalse();
        }

        @Test
        @DisplayName("satisfies: matching OR ranges (|| separated)")
        void satisfies_orRange() {
            VersionRange range = VersionRangeParser.parse("<1.0.0 || >=2.0.0");
            assertThat(range.satisfies(SemVersionParser.parse("0.5.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.0.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("1.5.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("2.0.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("3.0.0"))).isTrue();
        }

        @Test
        @DisplayName("satisfies: complex combination range")
        void satisfies_complex() {
            VersionRange range = VersionRangeParser.parse(">=1.0.0 <2.0.0 || >=3.0.0");
            assertThat(range.satisfies(SemVersionParser.parse("1.5.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("2.5.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("3.0.0"))).isTrue();
        }

        @Test
        @DisplayName("satisfies: null argument throws NullPointerException")
        void satisfies_nullThrows() {
            VersionRange range = VersionRangeParser.parse(">=1.0.0");
            assertThatThrownBy(() -> range.satisfies(null))
                    .isInstanceOf(NullPointerException.class);
        }

    }

}
