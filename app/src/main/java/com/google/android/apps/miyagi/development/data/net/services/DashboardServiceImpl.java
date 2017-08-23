package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.DashboardServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by jerzyw on 13.10.2016.
 */

public class DashboardServiceImpl extends AbsBaseService<DashboardServiceApi> implements DashboardService {

    public DashboardServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, DashboardServiceApi.class);
    }

    @Override
    public Observable<DashboardResponse> getDashboardData() {
        return mApi.getDashboardData().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
