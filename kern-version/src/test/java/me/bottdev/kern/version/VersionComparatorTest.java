package me.bottdev.kern.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionComparatorTest {

    @Nested
    @DisplayName("Constructor and getter properties")
    class ConstructorAndGetters {

        @Test
        @DisplayName("constructor: defaults null operator to equals sign")
        void constructor_nullOperatorDefaults() {
            SemVersion version = new SemVersion(1, 0, 0, null, null);
            VersionComparator comparator = new VersionComparator(null, version);
            assertThat(comparator.getOperator()).isEqualTo("=");
            assertThat(comparator.getVersion()).isEqualTo(version);
        }

        @Test
        @DisplayName("constructor: null version throws NullPointerException")
        void constructor_nullVersionThrows() {
            assertThatThrownBy(() -> new VersionComparator("=", null))
                    .isInstanceOf(NullPointerException.class);
        }

    }

    @Nested
    @DisplayName("Satisfaction checks")
    class Satisfaction {

        private final SemVersion v1 = new SemVersion(1, 0, 0, null, null);
        private final SemVersion v2 = new SemVersion(2, 0, 0, null, null);

        @Test
        @DisplayName("isSatisfiedBy: equals operator (=)")
        void isSatisfiedBy_equal() {
            VersionComparator comp = new VersionComparator("=", v1);
            assertThat(comp.isSatisfiedBy(v1)).isTrue();
            assertThat(comp.isSatisfiedBy(v2)).isFalse();
        }

        @Test
        @DisplayName("isSatisfiedBy: greater than operator (>)")
        void isSatisfiedBy_greaterThan() {
            VersionComparator comp = new VersionComparator(">", v1);
            assertThat(comp.isSatisfiedBy(v2)).isTrue();
            assertThat(comp.isSatisfiedBy(v1)).isFalse();
        }

        @Test
        @DisplayName("isSatisfiedBy: greater than or equal operator (>=)")
        void isSatisfiedBy_greaterThanOrEqual() {
            VersionComparator comp = new VersionComparator(">=", v1);
            assertThat(comp.isSatisfiedBy(v2)).isTrue();
            assertThat(comp.isSatisfiedBy(v1)).isTrue();
            
            SemVersion v0 = new SemVersion(0, 9, 0, null, null);
            assertThat(comp.isSatisfiedBy(v0)).isFalse();
        }

        @Test
        @DisplayName("isSatisfiedBy: less than operator (<)")
        void isSatisfiedBy_lessThan() {
            VersionComparator comp = new VersionComparator("<", v2);
            assertThat(comp.isSatisfiedBy(v1)).isTrue();
            assertThat(comp.isSatisfiedBy(v2)).isFalse();
        }

        @Test
        @DisplayName("isSatisfiedBy: less than or equal operator (<=)")
        void isSatisfiedBy_lessThanOrEqual() {
            VersionComparator comp = new VersionComparator("<=", v2);
            assertThat(comp.isSatisfiedBy(v1)).isTrue();
            assertThat(comp.isSatisfiedBy(v2)).isTrue();

            SemVersion v3 = new SemVersion(3, 0, 0, null, null);
            assertThat(comp.isSatisfiedBy(v3)).isFalse();
        }

        @Test
        @DisplayName("isSatisfiedBy: invalid operator throws IllegalArgumentException")
        void isSatisfiedBy_invalidOperator() {
            VersionComparator comp = new VersionComparator("~", v1);
            assertThatThrownBy(() -> comp.isSatisfiedBy(v1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown operator: ~");
        }

        @Test
        @DisplayName("isSatisfiedBy: null target version throws NullPointerException")
        void isSatisfiedBy_nullThrows() {
            VersionComparator comp = new VersionComparator("=", v1);
            assertThatThrownBy(() -> comp.isSatisfiedBy(null))
                    .isInstanceOf(NullPointerException.class);
        }

    }

}
