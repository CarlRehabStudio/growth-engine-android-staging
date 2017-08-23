package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.CommonDataServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;


/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class CommonDataServiceImpl extends AbsBaseService<CommonDataServiceApi> implements CommonDataService {

    public CommonDataServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, CommonDataServiceApi.class);
    }

    @Override
    public Observable<CommonDataResponse> getCommonData() {
        return mApi.getCommonData().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
