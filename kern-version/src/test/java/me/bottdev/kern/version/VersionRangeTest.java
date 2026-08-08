package me.bottdev.kern.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRangeTest {

    @Nested
    @DisplayName("Range satisfying tests")
    class Satisfies {

        @Test
        @DisplayName("satisfies: matching single exact version")
        void satisfies_exact() {
            VersionRange range = new VersionRange("=1.2.3");
            assertThat(range.satisfies(SemVersionParser.parse("1.2.3"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.2.4"))).isFalse();
        }

        @Test
        @DisplayName("satisfies: matching single version without operator defaults to equal")
        void satisfies_defaultOperator() {
            VersionRange range = new VersionRange("1.2.3");
            assertThat(range.satisfies(SemVersionParser.parse("1.2.3"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.2.4"))).isFalse();
        }

        @Test
        @DisplayName("satisfies: matching AND ranges (space separated)")
        void satisfies_andRange() {
            VersionRange range = new VersionRange(">=1.0.0 <2.0.0");
            assertThat(range.satisfies(SemVersionParser.parse("1.0.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.5.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("2.0.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("0.9.9"))).isFalse();
        }

        @Test
        @DisplayName("satisfies: matching OR ranges (|| separated)")
        void satisfies_orRange() {
            VersionRange range = new VersionRange("<1.0.0 || >=2.0.0");
            assertThat(range.satisfies(SemVersionParser.parse("0.5.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("1.0.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("1.5.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("2.0.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("3.0.0"))).isTrue();
        }

        @Test
        @DisplayName("satisfies: complex combination range")
        void satisfies_complex() {
            VersionRange range = new VersionRange(">=1.0.0 <2.0.0 || >=3.0.0");
            assertThat(range.satisfies(SemVersionParser.parse("1.5.0"))).isTrue();
            assertThat(range.satisfies(SemVersionParser.parse("2.5.0"))).isFalse();
            assertThat(range.satisfies(SemVersionParser.parse("3.0.0"))).isTrue();
        }

        @Test
        @DisplayName("satisfies: null argument throws NullPointerException")
        void satisfies_nullThrows() {
            VersionRange range = new VersionRange(">=1.0.0");
            assertThatThrownBy(() -> range.satisfies(null))
                    .isInstanceOf(NullPointerException.class);
        }

    }

    @Nested
    @DisplayName("Invalid Range Construction")
    class InvalidConstruction {

        @Test
        @DisplayName("constructor: null range string throws NullPointerException")
        void constructor_nullThrows() {
            assertThatThrownBy(() -> new VersionRange(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("constructor: malformed operator throws IllegalArgumentException")
        void constructor_malformedOperatorThrows() {
            assertThatThrownBy(() -> new VersionRange("~1.2.3"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

}
