package com.google.android.apps.miyagi.development.dagger;

import android.content.Context;
import com.google.android.apps.miyagi.development.BuildConfig;
import com.google.android.apps.miyagi.development.dagger.providers.MainRxRetrofitProvider;
import com.google.android.apps.miyagi.development.data.net.config.ServiceConfig;
import com.google.android.apps.miyagi.development.data.net.services.HeaderInterceptor;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

/**
 * Created by jerzyw on 05.10.2016.
 */
@Module
public class NetModule {
    private static final String TAG = "API CALL";
    private final Context mContext;

    public NetModule(Context context) {
        mContext = context;
    }

    /**
     * Inject are by type. We would need to inject many Retrofits with different configurations that
     * why we use Providers object - we inject specifics type of provider who gave as specially
     * configured Retrofit instance.
     *
     * @param serviceConfig - configuration object
     * @return - RetrofitProvider object
     */
    @Singleton
    @Provides
    public MainRxRetrofitProvider provideRetrofitProvider(ServiceConfig serviceConfig, Gson gson, ConfigStorage configStorage) {

        List<Interceptor> interceptors = new ArrayList<>();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Lh.d(TAG, message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            interceptors.add(loggingInterceptor);
        }
        interceptors.add(new HeaderInterceptor(configStorage));

        return new MainRxRetrofitProvider(serviceConfig, gson, interceptors, mContext);
    }

    /**
     * Return configured Gson instance.
     *
     * @param serviceConfig - configuration object.
     * @return gson instance.
     */
    @Singleton
    @Provides
    public Gson provideGson(ServiceConfig serviceConfig) {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(serviceConfig.getApiDateFormat())
                .create();
    }
}
