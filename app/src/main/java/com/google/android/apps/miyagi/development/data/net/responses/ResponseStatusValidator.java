package com.google.android.apps.miyagi.development.data.net.responses;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class ResponseStatusValidator<T extends BaseResponse> {

    protected void validateResponse(T response) throws ResponseStatusException, FieldValidationException {
        //validate status and throw exception
        FieldValidator.notNull(response, "Response");
        ResponseStatus status = response.getStatus();
        FieldValidator.notNull(status, "Status");

        switch (status.getCode()) {
            case BaseResponse.BaseStatusCodes.OK:
                break;
            default:
                throw new ResponseStatusException(status.getCode(), status.getMessage());

        }
    }
}
