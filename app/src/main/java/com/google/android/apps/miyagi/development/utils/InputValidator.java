package com.google.android.apps.miyagi.development.utils;

import android.text.TextUtils;

/**
 * A simple class that validates the specified input data.
 */
public class InputValidator {
    /**
     * Validates the specified input with email pattern and returns validation result.
     *
     * @param input - specified input
     * @return true if the specified input is a valid email, otherwise false.
     */
    public static boolean isValidEmail(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
        }
    }

    /**
     * Validates the specified input with password restrictions and returns validation result.
     *
     * @param input - specified input
     * @return true if the specified input is a valid password, otherwise false.
     */
    public static boolean isValidPassword(String input) {
        return !android.text.TextUtils.isEmpty(input);
    }

    /**
     * Validates the specified input with name restrictions and returns validation result.
     *
     * @param input - specified input
     * @return true if the specified input is a valid name, otherwise false.
     */
    public static boolean isValidName(String input) {
        return !android.text.TextUtils.isEmpty(input);
    }

    /**
     * Validates the specified input with null and empty restrictions and returns validation result.
     *
     * @param input - specified input
     * @return true if the specified input is not null or empty, otherwise false.
     */
    public static boolean notNullAndNotEmpty(String input) {
        return !android.text.TextUtils.isEmpty(input);
    }
}
