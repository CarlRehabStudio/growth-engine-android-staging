package com.google.android.apps.miyagi.development.data.net.responses.markets.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class MarketResponseValidator extends ResponseStatusValidator<MarketsResponse> {

    /**
     * Validates API response with market data.
     *
     * @param response - response to validate.
     * @return - response object.
     * @throws ResponseStatusException - exception will be thrown when response is not valid.
     * @throws FieldValidationException - exception will be thrown when field value is not valid.
     */
    public static MarketsResponse validate(MarketsResponse response) throws ResponseStatusException, FieldValidationException {
        MarketResponseValidator validator = new MarketResponseValidator();
        validator.validateResponse(response);
        return response;
    }

    @Override
    protected void validateResponse(MarketsResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);
        MarketsResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");
        FieldValidator.notNull(responseData.getMarkets(), "markets");
    }


}
