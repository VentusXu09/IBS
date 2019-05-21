package com.ventus.ibs.util;

import android.support.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ventus0905 on 04/16/2019
 */

public class StringUtils {

    /**
     * Check if a string null, "" or with value of "null".
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().equals("") || str.trim().equalsIgnoreCase("null");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        } else {
            return str1.equals(str2);
        }
    }

    /**
     * ellipsize a string
     *
     * @param str
     * @param maxLen
     * @return ellipsized string
     */
    public static String ellipsize(String str, int maxLen) {
        if (str == null)
            return null;
        str = str.trim();
        if ((str.length() < maxLen) || (maxLen < 3))
            return str;
        return str.substring(0, maxLen - 3) + "...";
    }


    /**
     * Counts how many times the substring appears in the larger string.
     *
     * @param s   the larger string
     * @param sub the substring
     * @return count
     */
    public static int countMatches(@NonNull String s, @NonNull String sub) {
        if (sub.length() == 0 || sub.length() > s.length()) {
            return 0;
        } else {
            int cutStringLength = s.length() - s.replace(sub, "").length();
            return cutStringLength == 0 ? 0 : cutStringLength / sub.length();
        }
    }

    /**
     * @param throwable
     * @return string representation of a throwable's stack trace.
     */
    public static String toString(Throwable throwable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

    public static String upperCaseFirstLetters(String str) {
        boolean prevWasWhiteSp = true;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (prevWasWhiteSp) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                prevWasWhiteSp = false;
            } else {
                prevWasWhiteSp = Character.isWhitespace(chars[i]);
            }
        }
        return new String(chars);
    }

    /**
     * this method will search for substring within a base string.
     *
     * @param baseString the string in which we want to search
     * @param subString  the string we want to find in base string
     * @return true if it can find the exact substring otherwise returns false.
     */
    public static boolean containExactString(String baseString, String subString) {
        if (StringUtils.isNotBlank(baseString) && StringUtils.isNotBlank(subString)) {
            String matchString = "\\b" + subString + "\\b";
            Pattern pattern = Pattern.compile(matchString);
            Matcher matcher = pattern.matcher(baseString);
            return matcher.find();
        }
        return false;
    }

    /**
     * Generate the md5 for the string.
     */
    public static String md5(String s) throws NoSuchAlgorithmException {
        if (isEmpty(s)) {
            return s;
        }

        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }

        return hexString.toString();
    }

    /**
     * Add sqlite escape string in case sql injection
     *
     * @param tempSQL
     * @return
     */
    public static String sqliteEscapeString(String tempSQL) {
        if (null == tempSQL) {
            return null;
        }
        tempSQL = tempSQL.replaceAll("'", "''");
        tempSQL = tempSQL.replaceAll("&", "/&");
        tempSQL = tempSQL.replaceAll("%", "/%");
        tempSQL = tempSQL.replaceAll("_", "/_");
        return tempSQL;
    }
}

