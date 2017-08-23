package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public interface CommonDataServiceApi {

    @GET("api/v1/pages/common-data")
    Observable<CommonDataResponse> getCommonData();
}
