package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserRequestData;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public interface RegisterUserServiceApi {

    @POST("api/v1/register")
    Observable<RegisterUserResponse> getRegisterUserData(@Body RegisterUserRequestData registerUserRequestData);
}
