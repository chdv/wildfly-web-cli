package com.dch.utils;

/**
 * Created by dcherdyntsev on 29.07.2015.
 */
public class TextUtils {

    public static String trim2null(String value) {
        if(value == null)
            return null;
        if(value.trim().length() == 0) {
            return null;
        }
        return value.trim();
    }

    public static boolean isEmpty(String value) {
        return trim2null(value) == null;
    }

    public static String trim2Empty(String value) {
        if(value == null) {
            return "";
        }
        return value.trim();
    }

}
