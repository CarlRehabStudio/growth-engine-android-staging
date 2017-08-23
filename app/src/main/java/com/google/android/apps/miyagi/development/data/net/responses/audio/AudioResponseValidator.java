package com.google.android.apps.miyagi.development.data.net.responses.audio;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by jerzyw on 22.12.2016.
 */

public class AudioResponseValidator extends ResponseStatusValidator<AudioResponse> {

    /**
     * Validates API response with dashboard data.
     *
     * @param response - response to validate.
     * @return - response object.
     * @throws ResponseStatusException  - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static AudioResponse validate(AudioResponse response) throws ResponseStatusException, FieldValidationException {
        AudioResponseValidator validator = new AudioResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(AudioResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);

        AudioResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");
        // TODO: add validation when API finished
    }
}
