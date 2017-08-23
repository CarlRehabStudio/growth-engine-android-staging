package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.NextStepServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public class NextStepServiceImpl extends AbsBaseService<NextStepServiceApi> implements NextStepService {

    public NextStepServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, NextStepServiceApi.class);
    }

    @Override
    public Observable<NextStepResponse> getNextStepData() {
        return mApi.getNextStepData().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
