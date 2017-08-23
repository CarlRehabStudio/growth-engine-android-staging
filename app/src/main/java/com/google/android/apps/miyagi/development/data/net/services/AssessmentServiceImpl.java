package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.AssessmentServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.assessment.AssessmentResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.gson.JsonObject;

import retrofit2.http.Body;
import rx.Observable;

public class AssessmentServiceImpl extends AbsBaseService<AssessmentServiceApi> implements AssessmentService {

    public AssessmentServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, AssessmentServiceApi.class);
    }

    @Override
    public Observable<AssessmentResponse> getTopicData(int topicId) {
        return mApi.getTopicData(topicId).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<AssessmentResponse> sendTopicAnswers(int topicId, JsonObject answers) {
        return mApi.sendTopicAnswers(topicId, answers).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<AssessmentResponse> getCertificationData() {
        return mApi.getCertificationData().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<AssessmentResponse> sendCertificationAnswers(@Body JsonObject answers) {
        return mApi.sendCertificationAnswers(answers).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
