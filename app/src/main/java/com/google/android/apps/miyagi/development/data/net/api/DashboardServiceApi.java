package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by jerzyw on 13.10.2016.
 */

public interface DashboardServiceApi {

    @GET("api/v1/pages/dash")
    Observable<DashboardResponse> getDashboardData();
}
