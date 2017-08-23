package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.LabelsServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by marcin on 22.12.2016.
 */

public class LabelsServiceImpl extends AbsBaseService<LabelsServiceApi> implements LabelsService {

    public LabelsServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, LabelsServiceApi.class);
    }

    @Override
    public Observable<LabelsResponse> getLabels() {
        return mApi.getLabels().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
