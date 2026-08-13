package me.bottdev.kern.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRangeParserTest {

    @Nested
    @DisplayName("Valid range parsing")
    class ValidParsing {

        @Test
        @DisplayName("parse: parses single comparator")
        void parse_single() {
            VersionRange range = VersionRangeParser.parse(">=1.2.3");
            assertThat(range.comparatorSets()).hasSize(1);
            assertThat(range.comparatorSets().get(0)).hasSize(1);
            
            VersionComparator comp = range.comparatorSets().get(0).get(0);
            assertThat(comp.getOperator()).isEqualTo(">=");
            assertThat(comp.getVersion()).isEqualTo(new SemVersion(1, 2, 3, null, null));
        }

        @Test
        @DisplayName("parse: parses multiple AND comparators")
        void parse_multipleAnd() {
            VersionRange range = VersionRangeParser.parse(">=1.0.0 <2.0.0");
            assertThat(range.comparatorSets()).hasSize(1);
            assertThat(range.comparatorSets().get(0)).hasSize(2);
        }

        @Test
        @DisplayName("parse: parses multiple OR sets")
        void parse_multipleOr() {
            VersionRange range = VersionRangeParser.parse("<1.0.0 || >=2.0.0");
            assertThat(range.comparatorSets()).hasSize(2);
            assertThat(range.comparatorSets().get(0)).hasSize(1);
            assertThat(range.comparatorSets().get(1)).hasSize(1);
        }

    }

    @Nested
    @DisplayName("Invalid range parsing")
    class InvalidParsing {

        @Test
        @DisplayName("parse: null range string throws NullPointerException")
        void parse_nullThrows() {
            assertThatThrownBy(() -> VersionRangeParser.parse(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("parse: empty range string throws IllegalArgumentException")
        void parse_emptyThrows() {
            assertThatThrownBy(() -> VersionRangeParser.parse(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Range string cannot be empty");

            assertThatThrownBy(() -> VersionRangeParser.parse("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Range string cannot be empty");
        }

        @Test
        @DisplayName("parse: malformed comparator operator throws IllegalArgumentException")
        void parse_malformedThrows() {
            assertThatThrownBy(() -> VersionRangeParser.parse("~1.2.3"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

}
