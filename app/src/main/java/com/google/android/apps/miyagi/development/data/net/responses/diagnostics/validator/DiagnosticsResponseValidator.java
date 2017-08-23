package com.google.android.apps.miyagi.development.data.net.responses.diagnostics.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by Lukasz on 17.12.2016.
 */

public class DiagnosticsResponseValidator extends ResponseStatusValidator<DiagnosticsResponse> {

    /**
     * Validates API response with diagnostics data.
     *
     * @param response - response to validate.
     * @return response object.
     * @throws ResponseStatusException  - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static DiagnosticsResponse validate(DiagnosticsResponse response) throws ResponseStatusException, FieldValidationException {
        DiagnosticsResponseValidator validator = new DiagnosticsResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(DiagnosticsResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
        DiagnosticsResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");

        //TODO validate field
        FieldValidator.notNull(responseData.getCommon(), "common");
        FieldValidator.notNull(responseData.getLoading(), "loading");
        FieldValidator.notNull(responseData.getStepOne(), "step_one");
        FieldValidator.notNull(responseData.getStepTwo(), "step_two");
        FieldValidator.notNull(responseData.getStepThree(), "step_three");
    }
}
