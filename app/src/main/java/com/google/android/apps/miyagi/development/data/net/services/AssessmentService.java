package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponse;
import com.google.gson.JsonObject;

import retrofit2.http.Body;
import rx.Observable;

public interface AssessmentService extends BaseService {

    Observable<AssessmentResponse> getTopicData(int topicId);

    Observable<AssessmentResponse> sendTopicAnswers(int topicId, JsonObject answers);

    Observable<AssessmentResponse> getCertificationData();

    Observable<AssessmentResponse> sendCertificationAnswers(@Body JsonObject answers);
}
