package me.bottdev.kern.version;

import lombok.NonNull;

public record SemVersion(
        int major,
        int minor,
        int patch,
        String preRelease,
        String buildMetadata
) implements Comparable<SemVersion> {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(major).append(".").append(minor).append(".").append(patch);
        if (preRelease != null && !preRelease.isEmpty()) {
            builder.append("-").append(preRelease);
        }
        if (buildMetadata != null && !buildMetadata.isEmpty()) {
            builder.append("+").append(buildMetadata);
        }
        return builder.toString();
    }

    public boolean isGreaterThan(SemVersion other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(SemVersion other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(SemVersion other) {
        return this.compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(SemVersion other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isEqual(SemVersion other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public int compareTo(@NonNull SemVersion other) {

        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        if (this.patch != other.patch) {
            return Integer.compare(this.patch, other.patch);
        }

        if (this.preRelease == null && other.preRelease != null) return 1;
        if (this.preRelease != null && other.preRelease == null) return -1;
        if (this.preRelease != null) {
            return this.preRelease.compareTo(other.preRelease);
        }

        return 0;
    }

}
