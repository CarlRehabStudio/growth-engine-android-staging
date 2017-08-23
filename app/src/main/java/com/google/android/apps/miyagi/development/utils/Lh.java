package com.google.android.apps.miyagi.development.utils;

import android.support.annotation.NonNull;
import android.util.Log;

public class Lh {

    private static String TAG = "Miyagi";
    private static boolean shouldLog = true;

    /**
     * Log to debug stream.
     * @param tag - custom tag.
     * @param msg - massage to log.
     */
    public static void d(String tag, String msg) {
        if (shouldLog) {
            Log.d(formatTag(tag), formatContent(msg));
        }
    }

    /**
     * Log to debug stream.
     *
     * @param loggingObject - logging object. Simple name of its class will be used as TAG.
     * @param msg - massage to log.
     */
    public static void d(Object loggingObject, String msg) {
        if (shouldLog) {
            Log.d(formatTag(loggingObject.getClass().getSimpleName()), formatContent(msg));
        }
    }

    /**
     * Log to error stream.
     * @param tag - custom tag.
     * @param msg - massage to log.
     */
    public static void e(String tag, String msg) {
        if (shouldLog) {
            Log.e(formatTag(tag), formatContent(msg));
        }
    }

    /**
     * Log to error stream.
     *
     * @param loggingObject - logging object. Simple name of its class will be used as TAG.
     * @param msg - massage to log.
     */
    public static void e(Object loggingObject, String msg) {
        if (shouldLog) {
            Log.e(formatTag(loggingObject.getClass().getSimpleName()), formatContent(msg));
        }
    }

    /**
     * Log to verbose stream.
     * @param msg - massage to log.
     */
    public static void v(String msg) {
        if (shouldLog) {
            Log.v(TAG, formatContent(msg));
        }
    }

    /**
     * Log to info stream.
     * @param msg - massage to log.
     */
    public static void i(String msg) {
        if (shouldLog) {
            Log.i(TAG, formatTag(msg));
        }
    }

    private static String formatTag(String tag) {
        return TAG + "~" + tag;
    }

    /**
     * Prevents null exception when logging.
     * @param content - msg string. Can be null.
     * @return - not null string
     */
    private static @NonNull String formatContent(String content) {
        return " " + content;
    }
}
