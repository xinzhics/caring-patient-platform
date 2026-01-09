package com.caring.sass.common.constant;

import java.util.ArrayList;
import java.util.List;

public class LetterUtil {

    public final List<String> ENGLISH_ALPHABET = new ArrayList<>();

    private static LetterUtil letterUtil;

    public static synchronized LetterUtil getInstance() {
        if (letterUtil == null) {
            letterUtil = new LetterUtil();
        }
        return letterUtil;
    }


    public LetterUtil() {
        ENGLISH_ALPHABET.add("A");
        ENGLISH_ALPHABET.add("B");
        ENGLISH_ALPHABET.add("C");
        ENGLISH_ALPHABET.add("D");
        ENGLISH_ALPHABET.add("E");
        ENGLISH_ALPHABET.add("F");
        ENGLISH_ALPHABET.add("G");
        ENGLISH_ALPHABET.add("H");
        ENGLISH_ALPHABET.add("I");
        ENGLISH_ALPHABET.add("J");
        ENGLISH_ALPHABET.add("K");
        ENGLISH_ALPHABET.add("L");
        ENGLISH_ALPHABET.add("M");
        ENGLISH_ALPHABET.add("N");
        ENGLISH_ALPHABET.add("O");
        ENGLISH_ALPHABET.add("P");
        ENGLISH_ALPHABET.add("Q");
        ENGLISH_ALPHABET.add("R");
        ENGLISH_ALPHABET.add("S");
        ENGLISH_ALPHABET.add("T");
        ENGLISH_ALPHABET.add("U");
        ENGLISH_ALPHABET.add("V");
        ENGLISH_ALPHABET.add("W");
        ENGLISH_ALPHABET.add("X");
        ENGLISH_ALPHABET.add("Y");
        ENGLISH_ALPHABET.add("Z");
        ENGLISH_ALPHABET.add("Z#");
    }

}
