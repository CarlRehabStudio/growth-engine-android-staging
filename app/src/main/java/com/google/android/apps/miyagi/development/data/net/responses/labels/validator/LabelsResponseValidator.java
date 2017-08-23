package com.google.android.apps.miyagi.development.data.net.responses.labels.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by marcin on 23.12.2016.
 */

public class LabelsResponseValidator extends ResponseStatusValidator<LabelsResponse> {

    /**
     * Validates API response with labels data.
     *
     * @param response - response to validate.
     * @return - response object.
     * @throws ResponseStatusException - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static LabelsResponse validate(LabelsResponse response) throws ResponseStatusException, FieldValidationException {
        LabelsResponseValidator validator = new LabelsResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(LabelsResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
        LabelsResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");

        FieldValidator.notNull(responseData.getSignInSelectionLabels(), "sign_in_method_selection");
        FieldValidator.notNull(responseData.getCheckAccountLabels(), "check_account");
        FieldValidator.notNull(responseData.getCreateAccountFullLabels(), "create_account_full");
        FieldValidator.notNull(responseData.getCreateAccountMissingInfoLabels(), "create_account_missing_info");
        FieldValidator.notNull(responseData.getEnterPasswordLabels(), "enter_password");
        FieldValidator.notNull(responseData.getFirebaseMessagesLabels(), "firebase_messages");
        FieldValidator.notNull(responseData.getErrorsLabels(), "errors");

        // TODO: more detailed validation

    }
}
