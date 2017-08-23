package com.google.android.apps.miyagi.development.data.net.responses.markets;

import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class MarketsResponseData {

    @SerializedName("markets")
    protected List<Market> mMarkets;

    public List<Market> getMarkets() {
        return mMarkets;
    }
}
