package com.google.android.apps.miyagi.development.data.net.responses.onboarding.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponse;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

public class OnboardingResponseValidator extends ResponseStatusValidator<OnboardingResponse> {
    /**
     * @param response - response to validate.
     * @return - response object.
     * @throws RuntimeException - exception will be thrown when response is not valid.
     */
    public static OnboardingResponseData validate(OnboardingResponse response) throws RuntimeException {
        OnboardingResponseValidator validator = new OnboardingResponseValidator();
        validator.validateResponse(response);
        return response.getResponseData();
    }

    @Override
    protected void validateResponse(OnboardingResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);

        OnboardingResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");

        FieldValidator.notNull(responseData.getSkipText(), "skip_text");

        //TODO validate step data field
    }
}
