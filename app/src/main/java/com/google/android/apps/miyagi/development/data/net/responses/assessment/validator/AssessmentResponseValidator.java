package com.google.android.apps.miyagi.development.data.net.responses.assessment.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponse;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by marcinarciszew on 14.12.2016.
 */

public class AssessmentResponseValidator extends ResponseStatusValidator<AssessmentResponse> {

    /**
     * Validates API response with assessment data.
     *
     * @param response - response to validate.
     * @return response object.
     * @throws ResponseStatusException - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static AssessmentResponse validate(AssessmentResponse response) throws ResponseStatusException, FieldValidationException {
        AssessmentResponseValidator validator = new AssessmentResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(AssessmentResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);

        AssessmentResponseData responseData = response.getResponseData();
        // TODO: validate assessment fields
        //FieldValidator.notNull(responseData, "ResponseData");

    }
}
