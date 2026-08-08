package me.bottdev.kern.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SemVersionParserTest {

    @Nested
    @DisplayName("Valid versions parsing")
    class ValidParsing {

        @Test
        @DisplayName("parse: parses standard semantic version")
        void parse_standard() {
            SemVersion version = SemVersionParser.parse("1.2.3");
            assertThat(version.major()).isEqualTo(1);
            assertThat(version.minor()).isEqualTo(2);
            assertThat(version.patch()).isEqualTo(3);
            assertThat(version.preRelease()).isNull();
            assertThat(version.buildMetadata()).isNull();
        }

        @Test
        @DisplayName("parse: parses version with leading 'v' or 'V' prefix")
        void parse_withPrefix() {
            SemVersion versionLower = SemVersionParser.parse("v2.4.6");
            assertThat(versionLower.major()).isEqualTo(2);
            
            SemVersion versionUpper = SemVersionParser.parse("V3.5.7");
            assertThat(versionUpper.major()).isEqualTo(3);
        }

        @Test
        @DisplayName("parse: parses version with pre-release")
        void parse_withPreRelease() {
            SemVersion version = SemVersionParser.parse("1.2.3-alpha.beta.1");
            assertThat(version.preRelease()).isEqualTo("alpha.beta.1");
            assertThat(version.buildMetadata()).isNull();
        }

        @Test
        @DisplayName("parse: parses version with build metadata")
        void parse_withBuildMetadata() {
            SemVersion version = SemVersionParser.parse("1.2.3+build.123");
            assertThat(version.buildMetadata()).isEqualTo("build.123");
            assertThat(version.preRelease()).isNull();
        }

        @Test
        @DisplayName("parse: parses version with both pre-release and build metadata")
        void parse_withBoth() {
            SemVersion version = SemVersionParser.parse("1.2.3-rc.1+20150311T010101");
            assertThat(version.preRelease()).isEqualTo("rc.1");
            assertThat(version.buildMetadata()).isEqualTo("20150311T010101");
        }

    }

    @Nested
    @DisplayName("Invalid versions parsing")
    class InvalidParsing {

        @Test
        @DisplayName("parse: null version string throws NullPointerException")
        void parse_nullThrows() {
            assertThatThrownBy(() -> SemVersionParser.parse(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("parse: empty version string throws IllegalArgumentException")
        void parse_emptyThrows() {
            assertThatThrownBy(() -> SemVersionParser.parse(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Version string cannot be null or empty");

            assertThatThrownBy(() -> SemVersionParser.parse("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Version string cannot be null or empty");
        }

        @Test
        @DisplayName("parse: malformed version strings throw IllegalArgumentException")
        void parse_malformedThrows() {
            String[] invalidVersions = {
                    "1",
                    "1.2",
                    "1.2.3.4",
                    "a.b.c",
                    "1.2.3-",
                    "1.2.3+",
                    "1.2.3-alpha+",
                    "01.2.3",
                    "1.02.3"
            };

            for (String invalid : invalidVersions) {
                assertThatThrownBy(() -> SemVersionParser.parse(invalid))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Invalid semantic version format");
            }
        }

    }

}
