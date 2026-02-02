package com.shop.common.util;

public final class TextNormalizer {

    private TextNormalizer() {}

    public static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
