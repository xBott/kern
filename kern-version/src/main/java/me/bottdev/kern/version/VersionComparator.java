package me.bottdev.kern.version;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@EqualsAndHashCode
public class VersionComparator {

    private final String operator;
    private final SemVersion version;

    public VersionComparator(String operator, @NonNull SemVersion version) {
        this.operator = operator != null ? operator : "=";
        this.version = version;
    }

    public boolean isSatisfiedBy(@NonNull SemVersion targetVersion) {
        return switch (operator) {
            case "=" -> targetVersion.isEqual(version);
            case ">" -> targetVersion.isGreaterThan(version);
            case ">=" -> targetVersion.isGreaterThanOrEqual(version);
            case "<" -> targetVersion.isLessThan(version);
            case "<=" -> targetVersion.isLessThanOrEqual(version);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    @Override
    public String toString() {
        return operator + version;
    }
}