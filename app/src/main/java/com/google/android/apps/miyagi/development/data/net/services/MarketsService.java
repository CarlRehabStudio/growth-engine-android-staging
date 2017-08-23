package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponse;

import rx.Observable;

/**
 * Created by jerzyw on 13.10.2016.
 */

public interface MarketsService extends BaseService {

    Observable<MarketsResponse> getMarkets();
}
