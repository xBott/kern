package me.bottdev.kern.version;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public class VersionRangeParser {

    private static final Pattern COMPARATOR_PATTERN = Pattern.compile("^(>=|<=|>|<|=)?\\s*(.+)");

    public static VersionRange parse(@NonNull String rangeStr) {

        if (rangeStr.equalsIgnoreCase("*")) {
            return VersionRange.any();
        }

        if (rangeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Range string cannot be empty");
        }

        List<List<VersionComparator>> comparatorSets = new ArrayList<>();
        String[] orParts = rangeStr.split("\\|\\|");

        for (String orPart : orParts) {

            List<VersionComparator> andVersionComparators = new ArrayList<>();
            String[] tokens = orPart.trim().split("\\s+");
            
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                andVersionComparators.add(parseComparator(token));
            }

            comparatorSets.add(andVersionComparators);

        }

        return new VersionRange(comparatorSets);

    }

    private static VersionComparator parseComparator(String token) {
        
        Matcher matcher = COMPARATOR_PATTERN.matcher(token);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid comparator: " + token);
        }
        
        String op = matcher.group(1);
        SemVersion ver = SemVersionParser.parse(matcher.group(2));

        return new VersionComparator(op, ver);
    }

}
