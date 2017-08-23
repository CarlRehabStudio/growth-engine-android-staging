package com.google.android.apps.miyagi.development.data.net.responses.commondata.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponse;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class CommonDataResponseValidator extends ResponseStatusValidator<CommonDataResponse> {

    /**
     * Validates API response with common data.
     *
     * @param response - response to validate.
     * @return - response object.
     * @throws ResponseStatusException - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static CommonDataResponse validate(CommonDataResponse response) throws ResponseStatusException, FieldValidationException {
        CommonDataResponseValidator validator = new CommonDataResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(CommonDataResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
        CommonDataResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");

        // TODO: more detailed validation

    }
}
