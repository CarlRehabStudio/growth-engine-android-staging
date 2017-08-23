package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by marcin on 22.12.2016.
 */

public interface LabelsServiceApi {

    @GET("api/v1/pages/register")
    Observable<LabelsResponse> getLabels();
}
