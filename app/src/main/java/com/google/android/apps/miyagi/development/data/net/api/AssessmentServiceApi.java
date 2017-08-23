package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponse;
import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by marcinarciszew on 14.12.2016.
 */

public interface AssessmentServiceApi {

    @GET("api/v1/pages/topic/{id}/assessment")
    Observable<AssessmentResponse> getTopicData(@Path("id") int topicId);

    @POST("api/v1/topic/{id}/assessment")
    Observable<AssessmentResponse> sendTopicAnswers(@Path("id") int topicId, @Body JsonObject answers);

    @GET("api/v1/pages/certification/assessment")
    Observable<AssessmentResponse> getCertificationData();

    @POST("api/v1/certification/assessment")
    Observable<AssessmentResponse> sendCertificationAnswers(@Body JsonObject answers);
}
