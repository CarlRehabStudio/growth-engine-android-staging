package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by jerzyw on 05.10.2016.
 */

public interface MarketsServiceApi {

    @GET("/api/v1/markets")
    Observable<MarketsResponse> getMarkets();
}
