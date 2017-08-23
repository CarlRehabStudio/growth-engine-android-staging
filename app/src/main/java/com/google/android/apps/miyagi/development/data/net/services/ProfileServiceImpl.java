package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.ProfileServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponse;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileUpdateRequestData;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import retrofit2.http.Body;
import rx.Observable;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

public class ProfileServiceImpl extends AbsBaseService<ProfileServiceApi> implements ProfileService {

    /**
     * @param retrofitProvider - provides configured retrofit instance.
     */
    public ProfileServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, ProfileServiceApi.class);
    }

    @Override
    public Observable<ProfileResponse> getProfile() {
        return mApi.getProfile().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<ResponseStatus> updateProfile(@Body ProfileUpdateRequestData profileUpdateRequestData) {
        return mApi.updateProfile(profileUpdateRequestData).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
