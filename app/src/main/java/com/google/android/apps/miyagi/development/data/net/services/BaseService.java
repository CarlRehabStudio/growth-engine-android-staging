package com.google.android.apps.miyagi.development.data.net.services;

/**
 * Created by jerzyw on 03.11.2016.
 */

public interface BaseService {

    /**
     * @param baseUrl - base url. Must be ended by "/" http://127.0.0.1/.
     */
    void changeServiceUrl(String baseUrl);

}
