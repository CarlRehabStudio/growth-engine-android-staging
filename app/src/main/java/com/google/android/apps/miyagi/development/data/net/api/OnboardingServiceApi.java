package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

public interface OnboardingServiceApi {
    @GET("api/v1/pages/onboarding")
    Observable<OnboardingResponse> getOnboarding(@Query("user_type") String source);
}
