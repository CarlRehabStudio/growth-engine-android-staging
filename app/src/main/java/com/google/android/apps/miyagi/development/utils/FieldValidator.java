package com.google.android.apps.miyagi.development.utils;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;

import java.util.List;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class FieldValidator {

    /**
     * @param obj  - object to validate.
     * @param name - model field name used in exception message.
     * @throws FieldValidationException - throws exception when validation fails.
     */
    public static void notNull(Object obj, String name) throws FieldValidationException {
        if (obj == null) {
            throw new FieldValidationException(name, "Can't be null");
        }
    }

    /**
     * @param str  - string to validate.
     * @param name - model field name used in exception message.
     * @throws FieldValidationException - throws exception when validation fails.
     */
    public static void notNullAndNotEmpty(String str, String name) throws FieldValidationException {
        notNull(str, name);
        if (str.isEmpty()) {
            throw new FieldValidationException(name, "Can't be empty string");
        }
    }

    /**
     * @param list - list to validate.
     * @param name - model field name used in exception message.
     * @throws FieldValidationException - throws exception when validation fails.
     */
    public static void notNullAndNotEmpty(List list, String name) throws FieldValidationException {
        notNull(list, name);
        if (list.isEmpty()) {
            throw new FieldValidationException(name, "Can't be empty collection");
        }
    }

}
