package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.MarketsServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.markets.MarketsResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by jerzyw on 05.10.2016.
 */
public class MarketsServiceImpl extends AbsBaseService<MarketsServiceApi> implements MarketsService {

    public MarketsServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, MarketsServiceApi.class);
    }

    @Override
    public Observable<MarketsResponse> getMarkets() {
        return mApi.getMarkets().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

}
