package me.bottdev.kern.version;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;

public class VersionRange {

    private final List<List<VersionComparator>> comparatorSets = new ArrayList<>();

    public VersionRange(@NonNull String rangeStr) {
        parseRange(rangeStr);
    }

    private void parseRange(String rangeStr) {

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

    }

    private VersionComparator parseComparator(String token) {
        
        Matcher matcher = Pattern.compile("^(>=|<=|>|<|=)?\\s*(.+)").matcher(token);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid comparator: " + token);
        }
        
        String op = matcher.group(1);
        SemVersion ver = SemVersionParser.parse(matcher.group(2));

        return new VersionComparator(op, ver);
    }

    public boolean satisfies(@NonNull SemVersion version) {

        for (List<VersionComparator> andSet : comparatorSets) {

            boolean matchesAll = true;
            for (VersionComparator comp : andSet) {
                if (!comp.isSatisfiedBy(version)) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) return true;
        }

        return false;

    }

}