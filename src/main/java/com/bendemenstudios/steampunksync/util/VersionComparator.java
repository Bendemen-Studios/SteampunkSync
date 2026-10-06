package com.bendemenstudios.steampunksync.util;

import java.util.ArrayList;
import java.util.List;

public final class VersionComparator {
    private VersionComparator() {}

    public static int compare(String a, String b) {
        List<Integer> left = parts(a), right = parts(b);
        int n = Math.max(left.size(), right.size());
        for (int i = 0; i < n; i++) {
            int l = i < left.size() ? left.get(i) : 0;
            int r = i < right.size() ? right.get(i) : 0;
            if (l != r) return Integer.compare(l, r);
        }
        return 0;
    }

    private static List<Integer> parts(String value) {
        String cleaned = value == null ? "" : value.trim().replaceFirst("^[vV]", "");
        String[] tokens = cleaned.split("[^0-9]+");
        List<Integer> out = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty()) out.add(Integer.parseInt(token));
        }
        if (out.isEmpty()) out.add(0);
        return out;
    }
}
