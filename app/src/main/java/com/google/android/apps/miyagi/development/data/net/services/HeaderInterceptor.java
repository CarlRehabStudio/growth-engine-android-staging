package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by jerzyw on 14.11.2016.
 */

public class HeaderInterceptor implements Interceptor {
    private static final String AUTH_TOKEN = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json; charset=utf-8";

    private final ConfigStorage mConfigStorage;

    public HeaderInterceptor(ConfigStorage configStorage) {
        mConfigStorage = configStorage;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        String loginToken = mConfigStorage.readLoginToken();
        if (loginToken != null) {
            builder.addHeader(AUTH_TOKEN, "Bearer " + loginToken);
        }
        return chain.proceed(builder.build());
    }
}
