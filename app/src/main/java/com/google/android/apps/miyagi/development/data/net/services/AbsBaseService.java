package com.google.android.apps.miyagi.development.data.net.services;

import android.text.TextUtils;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import retrofit2.Retrofit;

/**
 * Created by jerzyw on 13.10.2016.
 */

public abstract class AbsBaseService<T> implements BaseService {

    private final RetrofitProvider mRetrofitProvider;
    private final Class<T> mApiClass;
    protected T mApi;
    private String mCurrBaseUrl;


    /**
     * @param retrofitProvider - provides configured retrofit instance.
     * @param apiClass         - Retrofit api class.
     */
    public AbsBaseService(RetrofitProvider retrofitProvider, ConfigStorage configStorage, Class<T> apiClass) {
        mRetrofitProvider = retrofitProvider;
        mApiClass = apiClass;

        changeServiceUrl("https://gweb-miyagi-testing.appspot.com/");

        if (configStorage.getSelectedMarket() != null) {
            String endPointUrl = configStorage.getSelectedMarket().getEndpointUrl();
            if (!TextUtils.isEmpty(endPointUrl)) {
                changeServiceUrl(endPointUrl);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Recreate Retrofit and api instance.
     */
    public void changeServiceUrl(String baseUrl) {
        if (mCurrBaseUrl == null || !mCurrBaseUrl.equals(baseUrl)) {
            //recreate retrofit
            Retrofit retrofit = mRetrofitProvider.createNewRetrofitInstance(baseUrl);
            //create new API instance
            mApi = retrofit.create(mApiClass);
            mCurrBaseUrl = baseUrl;
        }
    }

}
