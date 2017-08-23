package com.google.android.apps.miyagi.development.data.error;

/**
 * Created by jerzyw on 10.11.2016.
 */

public class ApiContractException extends FieldValidationException {

    public ApiContractException(String fieldName, String value) {
        super("Unexpected value: " + value + " in field " + fieldName);
    }

    public ApiContractException(String fieldName, int value) {
        super("Unexpected value: " + value + " in field " + fieldName);
    }
}
