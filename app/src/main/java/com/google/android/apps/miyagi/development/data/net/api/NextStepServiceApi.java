package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public interface NextStepServiceApi {
    @GET("api/v1/student/next-step")
    Observable<NextStepResponse> getNextStepData();
}
