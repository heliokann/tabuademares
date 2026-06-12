package com.novoideal.tabuademares.util;

import java.text.Normalizer;

public class TextNormalizer {
    public static String normalize(String input) {
        String nfd = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD);
        return nfd.replaceAll("[^\\p{ASCII}]", "");
    }
}
