package com.google.android.apps.miyagi.development.data.net.responses.diagnostics.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSubmitResponse;

/**
 * Created by lukaszweglinski on 22.12.2016.
 */

public class DiagnosticsSubmitValidator extends ResponseStatusValidator<DiagnosticsSubmitResponse> {

    /**
     * Validates API submit response with status code.
     *
     * @param response - response to validate.
     * @return response object.
     * @throws ResponseStatusException  - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static DiagnosticsSubmitResponse validate(DiagnosticsSubmitResponse response) throws ResponseStatusException, FieldValidationException {
        DiagnosticsSubmitValidator validator = new DiagnosticsSubmitValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(DiagnosticsSubmitResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
    }
}
