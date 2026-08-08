package me.bottdev.kern.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SemVersionTest {

    @Nested
    @DisplayName("toString representation")
    class ToString {

        @Test
        @DisplayName("toString: returns standard version format when no prerelease or build metadata")
        void toString_simple() {
            SemVersion version = new SemVersion(1, 2, 3, null, null);
            assertThat(version.toString()).isEqualTo("1.2.3");
        }

        @Test
        @DisplayName("toString: includes pre-release version if present")
        void toString_withPreRelease() {
            SemVersion version = new SemVersion(1, 2, 3, "alpha.1", null);
            assertThat(version.toString()).isEqualTo("1.2.3-alpha.1");
        }

        @Test
        @DisplayName("toString: includes build metadata if present")
        void toString_withBuildMetadata() {
            SemVersion version = new SemVersion(1, 2, 3, null, "build.123");
            assertThat(version.toString()).isEqualTo("1.2.3+build.123");
        }

        @Test
        @DisplayName("toString: includes both pre-release and build metadata if present")
        void toString_withBoth() {
            SemVersion version = new SemVersion(1, 2, 3, "beta.2", "exp.sha.5114f85");
            assertThat(version.toString()).isEqualTo("1.2.3-beta.2+exp.sha.5114f85");
        }

    }

    @Nested
    @DisplayName("Comparison operations")
    class Comparison {

        @Test
        @DisplayName("compareTo: major versions comparison")
        void compareTo_major() {
            SemVersion v1 = new SemVersion(2, 0, 0, null, null);
            SemVersion v2 = new SemVersion(1, 0, 0, null, null);
            assertThat(v1.compareTo(v2)).isPositive();
            assertThat(v2.compareTo(v1)).isNegative();
        }

        @Test
        @DisplayName("compareTo: minor versions comparison")
        void compareTo_minor() {
            SemVersion v1 = new SemVersion(1, 3, 0, null, null);
            SemVersion v2 = new SemVersion(1, 2, 0, null, null);
            assertThat(v1.compareTo(v2)).isPositive();
            assertThat(v2.compareTo(v1)).isNegative();
        }

        @Test
        @DisplayName("compareTo: patch versions comparison")
        void compareTo_patch() {
            SemVersion v1 = new SemVersion(1, 2, 4, null, null);
            SemVersion v2 = new SemVersion(1, 2, 3, null, null);
            assertThat(v1.compareTo(v2)).isPositive();
            assertThat(v2.compareTo(v1)).isNegative();
        }

        @Test
        @DisplayName("compareTo: release version is higher than pre-release version")
        void compareTo_releaseVsPreRelease() {
            SemVersion v1 = new SemVersion(1, 2, 3, null, null);
            SemVersion v2 = new SemVersion(1, 2, 3, "alpha.1", null);
            assertThat(v1.compareTo(v2)).isPositive();
            assertThat(v2.compareTo(v1)).isNegative();
        }

        @Test
        @DisplayName("compareTo: pre-release comparison lexicographically")
        void compareTo_preReleaseLexicographical() {
            SemVersion v1 = new SemVersion(1, 2, 3, "beta", null);
            SemVersion v2 = new SemVersion(1, 2, 3, "alpha", null);
            assertThat(v1.compareTo(v2)).isPositive();
            assertThat(v2.compareTo(v1)).isNegative();
        }

        @Test
        @DisplayName("compareTo: identical versions return zero")
        void compareTo_identical() {
            SemVersion v1 = new SemVersion(1, 2, 3, "alpha", "build");
            SemVersion v2 = new SemVersion(1, 2, 3, "alpha", "other-build");
            assertThat(v1.compareTo(v2)).isZero();
        }

        @Test
        @DisplayName("compareTo: null comparison throws NullPointerException")
        void compareTo_nullThrows() {
            SemVersion version = new SemVersion(1, 2, 3, null, null);
            assertThatThrownBy(() -> version.compareTo(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("isGreaterThan: returns true if greater")
        void isGreaterThan() {
            SemVersion v1 = new SemVersion(2, 0, 0, null, null);
            SemVersion v2 = new SemVersion(1, 0, 0, null, null);
            assertThat(v1.isGreaterThan(v2)).isTrue();
            assertThat(v2.isGreaterThan(v1)).isFalse();
        }

        @Test
        @DisplayName("isGreaterThanOrEqual: returns true if greater or equal")
        void isGreaterThanOrEqual() {
            SemVersion v1 = new SemVersion(2, 0, 0, null, null);
            SemVersion v2 = new SemVersion(1, 0, 0, null, null);
            assertThat(v1.isGreaterThanOrEqual(v2)).isTrue();
            assertThat(v1.isGreaterThanOrEqual(v1)).isTrue();
            assertThat(v2.isGreaterThanOrEqual(v1)).isFalse();
        }

        @Test
        @DisplayName("isLessThan: returns true if less")
        void isLessThan() {
            SemVersion v1 = new SemVersion(1, 0, 0, null, null);
            SemVersion v2 = new SemVersion(2, 0, 0, null, null);
            assertThat(v1.isLessThan(v2)).isTrue();
            assertThat(v2.isLessThan(v1)).isFalse();
        }

        @Test
        @DisplayName("isLessThanOrEqual: returns true if less or equal")
        void isLessThanOrEqual() {
            SemVersion v1 = new SemVersion(1, 0, 0, null, null);
            SemVersion v2 = new SemVersion(2, 0, 0, null, null);
            assertThat(v1.isLessThanOrEqual(v2)).isTrue();
            assertThat(v1.isLessThanOrEqual(v1)).isTrue();
            assertThat(v2.isLessThanOrEqual(v1)).isFalse();
        }

        @Test
        @DisplayName("isEqual: returns true if equal")
        void isEqual() {
            SemVersion v1 = new SemVersion(1, 2, 3, "rc1", null);
            SemVersion v2 = new SemVersion(1, 2, 3, "rc1", "build");
            assertThat(v1.isEqual(v2)).isTrue();
        }

    }

}
