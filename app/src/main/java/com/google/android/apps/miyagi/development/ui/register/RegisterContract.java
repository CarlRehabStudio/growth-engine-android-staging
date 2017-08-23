package com.google.android.apps.miyagi.development.ui.register;

import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;

/**
 * Created by marcin on 15.01.2017.
 */

public interface RegisterContract {

    interface View extends BaseView<RegisterContract.Presenter> {

        /**
         * Go to Diagnostics screen.
         */
        void goToDiagnostics();

        /**
         * Go to OnBoarding screen.
         * @param onboardingResponse OnBoardingData from API.
         */
        void goToOnboarding(OnboardingResponseData onboardingResponse);

        /**
         * Go to Dashboard screen.
         */
        void goToDashboard();

        /**
         * Callback on async request start.
         */
        void onSubscribe();

        /**
         * Callback on async request fail.
         * @param throwable Async request error.
         */
        void onDataError(Throwable throwable);
    }

    interface Presenter extends BasePresenter<RegisterContract.View> {

        /**
         * Get Common data information.
         * @param step Next step.
         * @param isOnboardingRequired Whether OnBoarding is required for next step.
         */
        void getCommonData(NavigationCallback.RegistrationSteps step, boolean isOnboardingRequired);

    }
}
