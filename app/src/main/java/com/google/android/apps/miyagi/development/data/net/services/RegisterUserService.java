package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;

import rx.Observable;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public interface RegisterUserService extends BaseService {

    Observable<RegisterUserResponse> registerUser(String firstName, String lastName, boolean emailNotifications, String xsrfToken);
}
