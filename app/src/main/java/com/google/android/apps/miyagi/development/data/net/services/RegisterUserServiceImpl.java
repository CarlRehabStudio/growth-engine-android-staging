package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.RegisterUserServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserRequestData;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public class RegisterUserServiceImpl extends AbsBaseService<RegisterUserServiceApi> implements RegisterUserService {

    public RegisterUserServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, RegisterUserServiceApi.class);
    }

    public Observable<RegisterUserResponse> registerUser(String firstName, String lastName, boolean emailNotifications, String xsrfToken) {
        return mApi.getRegisterUserData(new RegisterUserRequestData(firstName, lastName, emailNotifications, xsrfToken)).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
