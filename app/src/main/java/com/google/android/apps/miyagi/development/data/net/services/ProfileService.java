package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponse;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileUpdateRequestData;

import retrofit2.http.Body;
import rx.Observable;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

public interface ProfileService extends BaseService {
    Observable<ProfileResponse> getProfile();

    Observable<ResponseStatus> updateProfile(@Body ProfileUpdateRequestData profileUpdateRequestData);
}
