package com.google.android.apps.miyagi.development.data.net.responses.next.step.validator;

import com.google.android.apps.miyagi.development.data.error.ApiContractException;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class NextStepResponseValidator extends ResponseStatusValidator<NextStepResponse> {

    /**
     *
     * @param response - response to validate.
     * @return - response object.
     * @throws RuntimeException - exception will be thrown when response is not valid.
     */
    public static NextStepResponse validate(NextStepResponse response) throws RuntimeException {
        NextStepResponseValidator validator = new NextStepResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(NextStepResponse response) throws RuntimeException {
        super.validateResponse(response);
        NextStepResponseData responseData = response.getResponseData();

        FieldValidator.notNull(responseData, "ResponseData");

        int nextStep = responseData.getNextStep();
        switch (nextStep) {
            case NextStepResponseData.NextStep.DASHBOARD:
            case NextStepResponseData.NextStep.DIAGNOSTICS:
            case NextStepResponseData.NextStep.COMPLETE_PROFILE:
                break;
            case NextStepResponseData.NextStep.ACCOUNT_IS_BEING_DELETED:
                throw new DeletedAccountException();
            default:
                throw new ApiContractException("next_step", responseData.getNextStep());
        }
    }


}
