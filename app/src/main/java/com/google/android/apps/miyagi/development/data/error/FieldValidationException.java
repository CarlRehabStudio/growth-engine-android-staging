package com.google.android.apps.miyagi.development.data.error;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class FieldValidationException extends RuntimeException {

    public FieldValidationException(String fieldName, String message) {
        super(fieldName + " : " + message);
    }

    public FieldValidationException(String message) {
        super(message);
    }
}
