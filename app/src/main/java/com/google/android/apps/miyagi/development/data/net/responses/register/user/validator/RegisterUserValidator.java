package com.google.android.apps.miyagi.development.data.net.responses.register.user.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by jerzyw on 14.11.2016.
 */

public class RegisterUserValidator extends ResponseStatusValidator<RegisterUserResponse> {

    /**
     * @param response - response to validate.
     * @return - response object.
     * @throws RuntimeException - exception will be thrown when response is not valid.
     */
    public static RegisterUserResponse validate(RegisterUserResponse response) throws RuntimeException {
        RegisterUserValidator validator = new RegisterUserValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(RegisterUserResponse response) throws ResponseStatusException, FieldValidationException {
        //response object
        FieldValidator.notNull(response, "Response");
        //status field
        ResponseStatus status = response.getStatus();
        FieldValidator.notNull(status, "Status");
        //status code
        switch (status.getCode()) {
            case RegisterUserResponse.StatusCodes.OK:
                //ok
                break;
            case RegisterUserResponse.StatusCodes.ERROR_ACCOUNT_IS_CURRENTLY_BEING_DELETED:
            case RegisterUserResponse.StatusCodes.ERROR_MISSING_FIREBASE_TOKEN:
            case RegisterUserResponse.StatusCodes.ERROR_TOKEN_NOT_VALID:
            case RegisterUserResponse.StatusCodes.ERROR_USER_HAS_ALREADY_ACCOUNT:
            default:
                throw new ResponseStatusException(status.getCode(), status.getMessage());
        }
    }
}
