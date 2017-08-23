package com.google.android.apps.miyagi.development.data.net.config;

/**
 * Created by jerzyw on 05.10.2016.
 */

public interface ServiceConfig {

    String getApiDateFormat();

    int getReadTimeout();

    int getConnectTimeout();

    String getBaseApiUrl();
}
