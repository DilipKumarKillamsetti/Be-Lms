package com.mahindra.be_lms.util;

import junit.framework.TestCase;

/**
 * Created by Pravin on 11/16/16.
 */

public class QuickTest extends TestCase {

    public static boolean isAlphanumeric(String str) {
        return isLetter(str);
    }

    private static boolean isDigit(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c))
                return true;
        }

        return false;
    }

    private static boolean isLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isLetter(c))
                return true;
        }

        return false;
    }

    public boolean isAlphanumeric2(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }

}
