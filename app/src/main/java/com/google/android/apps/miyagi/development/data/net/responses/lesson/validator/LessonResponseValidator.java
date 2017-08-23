package com.google.android.apps.miyagi.development.data.net.responses.lesson.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

public class LessonResponseValidator extends ResponseStatusValidator<LessonResponse> {

    /**
     * Validates API response with lesson data.
     *
     * @param response - response to validate.
     * @return response object.
     * @throws ResponseStatusException - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static LessonResponse validate(LessonResponse response) throws ResponseStatusException, FieldValidationException {
        LessonResponseValidator validator = new LessonResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(LessonResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
        LessonResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");

        // TODO: validate lesson fields
        FieldValidator.notNull(responseData.getLesson(), "lesson");

        // TODO: validate practice fields
        FieldValidator.notNull(responseData.getPractice(), "activity");
    }
}
