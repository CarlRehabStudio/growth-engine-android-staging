package com.google.android.apps.miyagi.development.data.net.services;

import retrofit2.Retrofit;

/**
 * Created by jerzyw on 13.10.2016.
 * Object creates new Retrofit with custom configuration
 */

public interface RetrofitProvider {

    /**
     * Creates new Retrofit instance with given baseUrl.
     */
    Retrofit createNewRetrofitInstance(String baseUrl);

}
