package com.google.android.apps.miyagi.development.dagger.providers;

import android.content.Context;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.config.ServiceConfig;
import com.google.android.apps.miyagi.development.data.net.services.RetrofitProvider;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RxErrorHandlingCallAdapterFactory;
import com.google.gson.Gson;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jerzy Wierzchowski on 2016-05-01.
 * e-mail: PDSmaniac@gmail.com
 * Encapsulates retrofit creation
 */
public class MainRxRetrofitProvider implements RetrofitProvider {

    private static final int DEFAULT_DISK_CACHE_SIZE = 10 * 1024 * 1024;

    private Context mContext;
    private ServiceConfig mConfig;
    private Gson mGson;
    private List<Interceptor> mInterceptors;

    /**
     * @param config       - configuration.
     * @param gson         - pre-configured Gson instance for serialization/deserialization data.
     * @param interceptors - list network of interceptors.
     */
    public MainRxRetrofitProvider(ServiceConfig config, Gson gson, List<Interceptor> interceptors, Context context) {
        GoogleApplication.getInstance().getAppComponent().inject(this);
        mConfig = config;
        mGson = gson;
        mInterceptors = interceptors;
        mContext = context;
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        for (Interceptor interceptor : mInterceptors) {
            builder.addInterceptor(interceptor);
        }

        builder.connectTimeout(mConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(mConfig.getReadTimeout(), TimeUnit.SECONDS);

        File cacheDirectory = mContext.getCacheDir();
        Cache cache = new Cache(cacheDirectory, DEFAULT_DISK_CACHE_SIZE);

        builder.cache(cache);

        return builder.build();
    }

    @Override
    public Retrofit createNewRetrofitInstance(String baseUrl) {
        OkHttpClient okHttpClient = createOkHttpClient();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
    }
}