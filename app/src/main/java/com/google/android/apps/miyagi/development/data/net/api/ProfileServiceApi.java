package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponse;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileUpdateRequestData;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

public interface ProfileServiceApi {

    @GET("api/v1/pages/profile")
    Observable<ProfileResponse> getProfile();

    @POST("api/v1/student/profile")
    Observable<ResponseStatus> updateProfile(@Body ProfileUpdateRequestData profileUpdateRequestData);
}
