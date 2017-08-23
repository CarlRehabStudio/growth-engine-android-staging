package com.google.android.apps.miyagi.development.dagger;

import android.content.Context;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.net.config.ServiceConfig;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jerzyw on 05.10.2016.
 * Dagger 2 module. Provides configuration.
 */
@Module
public class ConfigModule {

    @Singleton
    @Provides
    ServiceConfig providesDataServiceConfig(final Context context) {
        return new ServiceConfig() {
            @Override
            public String getApiDateFormat() {
                return context.getString(R.string.api_date_format);
            }

            @Override
            public int getReadTimeout() {
                return context.getResources().getInteger(R.integer.api_read_timeout_sek);
            }

            @Override
            public int getConnectTimeout() {
                return context.getResources().getInteger(R.integer.api_connect_timeout_sek);
            }

            @Override
            public String getBaseApiUrl() {
                return context.getString(R.string.base_service_url);
            }
        };
    }
}
