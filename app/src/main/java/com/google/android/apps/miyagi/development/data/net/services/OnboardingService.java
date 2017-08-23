package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponse;

import rx.Observable;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

public interface OnboardingService extends BaseService {

    String EXISTING_USER = "existing";
    String NEW_USER = "new";

    Observable<OnboardingResponse> getOnboarding(boolean newUser);
}
