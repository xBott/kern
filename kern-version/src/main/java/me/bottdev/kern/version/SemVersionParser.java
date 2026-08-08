package me.bottdev.kern.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;

public class SemVersionParser {
    
    private static final Pattern SEMVER_PATTERN = Pattern.compile(
        "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
                "(?:-([0-9A-Za-z-.]+))?" +
                "(?:\\+([0-9A-Za-z-.]+))?$"
    );

    public static SemVersion parse(@NonNull String versionStr) {

        if (versionStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty");
        }

        if (versionStr.startsWith("v") || versionStr.startsWith("V")) {
            versionStr = versionStr.substring(1);
        }

        Matcher matcher = SEMVER_PATTERN.matcher(versionStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid semantic version format: " + versionStr);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        String preRelease = matcher.group(4);
        String buildMetadata = matcher.group(5);

        return new SemVersion(major, minor, patch, preRelease, buildMetadata);

    }

}