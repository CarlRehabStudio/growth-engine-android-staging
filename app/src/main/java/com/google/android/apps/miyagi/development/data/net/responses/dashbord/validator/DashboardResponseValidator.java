package com.google.android.apps.miyagi.development.data.net.responses.dashbord.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by jerzyw on 15.11.2016.
 */

public class DashboardResponseValidator extends ResponseStatusValidator<DashboardResponse> {

    /**
     * Validates API response with dashboard data.
     *
     * @param response - response to validate.
     * @return - response object.
     * @throws ResponseStatusException  - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static DashboardResponse validate(DashboardResponse response) throws ResponseStatusException, FieldValidationException {
        DashboardResponseValidator validator = new DashboardResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(DashboardResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
        DashboardResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");


        //TODO add validator
        /*FieldValidator.notNull(responseData.getHeader(), "header");
        FieldValidator.notNull(responseData.getTopicGroups(), "topic_groups");
        FieldValidator.notNull(responseData.getSmallMenu(), "small_menu");*/
    }
}
