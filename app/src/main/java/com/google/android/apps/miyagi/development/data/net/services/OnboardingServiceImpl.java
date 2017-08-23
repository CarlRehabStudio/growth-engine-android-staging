package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.OnboardingServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

public class OnboardingServiceImpl extends AbsBaseService<OnboardingServiceApi> implements OnboardingService {

    /**
     * @param retrofitProvider - provides configured retrofit instance.
     */
    public OnboardingServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, OnboardingServiceApi.class);
    }

    @Override
    public Observable<OnboardingResponse> getOnboarding(boolean newUser) {
        return mApi.getOnboarding(newUser ? NEW_USER : EXISTING_USER).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
