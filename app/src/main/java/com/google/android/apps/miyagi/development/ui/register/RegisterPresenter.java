package com.google.android.apps.miyagi.development.ui.register;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponse;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.validator.CommonDataResponseValidator;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.validator.OnboardingResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataService;
import com.google.android.apps.miyagi.development.data.net.services.DashboardService;
import com.google.android.apps.miyagi.development.data.net.services.OnboardingService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import rx.Observable;
import rx.Subscription;

import javax.inject.Inject;

/**
 * Created by Paweł on 2017-02-27.
 */

public class RegisterPresenter extends BasePresenterImpl<RegisterContract.View> implements RegisterContract.Presenter {

    @Inject CommonDataService mCommonDataService;
    @Inject OnboardingService mOnboardingService;
    @Inject DashboardService mDashboardService;
    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject ConfigStorage mConfigStorage;

    private Subscription mSubscription;
    private boolean mIsOnboardingRequired;
    private NavigationCallback.RegistrationSteps mNextSteps;
    private OnboardingResponseData mOnboardingResponse;

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void onAttachView(@NonNull RegisterContract.View view) {
        super.onAttachView(view);
        if (mSubscription != null) {
            onSubscribe();
        } else if (mNextSteps != null) {
            afterCommonDataStep();
        }
    }

    @Override
    public void getCommonData(NavigationCallback.RegistrationSteps step, boolean isOnboardingRequired) {
        SubscriptionHelper.unsubscribe(mSubscription);
        mIsOnboardingRequired = isOnboardingRequired;
        boolean newUser = mCurrentSessionCache.getOnboardingUserType() == CurrentSessionCache.OnboardingUserType.NEW_USER;

        mSubscription = Observable.zip(
                mCommonDataService.getCommonData()
                        .map(CommonDataResponseValidator::validate),
                isOnboardingRequired
                        ? mOnboardingService.getOnboarding(newUser).map(OnboardingResponseValidator::validate) : Observable.just(null),
                newUser
                        ? Observable.just(null) : mDashboardService.getDashboardData(),
                Observable.just(step),
                (commonDataResponse, onboardingResponseData, dashboardResponse, registrationSteps) -> {
                    onOnboardingDataReceived(onboardingResponseData);
                    onCommonDataReceived(commonDataResponse);
                    onDashboardDataReceived(dashboardResponse);
                    return registrationSteps;
                })
                .doOnSubscribe(this::onSubscribe)
                .subscribe(this::afterCommonDataStep, this::onDataError);
    }

    private void afterCommonDataStep(NavigationCallback.RegistrationSteps registrationSteps) {
        mNextSteps = registrationSteps;
        afterCommonDataStep();
    }

    private void afterCommonDataStep() {
        SubscriptionHelper.unsubscribe(mSubscription);
        RegisterContract.View view = getView();
        if (view != null) {
            switch (mNextSteps) {
                case DIAGNOSTICS:
                    view.goToDiagnostics();
                    view.onScreenExit();
                    break;
                case DASHBOARD:
                    if (mIsOnboardingRequired) {
                        view.goToOnboarding(mOnboardingResponse);
                    } else {
                        view.goToDashboard();
                    }
                    view.onScreenExit();
                    break;
                default:
                    break;
            }
        }
    }

    private void onCommonDataReceived(CommonDataResponse commonDataResponse) {
        mConfigStorage.saveCommonData(commonDataResponse.getResponseData());
        if ((commonDataResponse.getResponseData() != null)
                && (commonDataResponse.getResponseData().getMenu() != null)
                && commonDataResponse.getResponseData().getMenu().isUsesSystemFont()) {
            GoogleApplication.getInstance().setupSystemFonts();
        }
    }

    private void onDashboardDataReceived(DashboardResponse response) {
        if (response != null) {
            mCurrentSessionCache.setDashboardResponse(response);
            mConfigStorage.saveShouldUpdateDashboard(false);
        }
    }

    private void onOnboardingDataReceived(OnboardingResponseData onboardingResponseData) {
        mOnboardingResponse = onboardingResponseData;
    }

    private void onDataError(Throwable throwable) {
        SubscriptionHelper.unsubscribe(mSubscription);
        if (getView() != null) {
            getView().onDataError(throwable);
        }
    }

    private void onSubscribe() {
        if (getView() != null) {
            getView().onSubscribe();
        }
    }

    @Override
    public void onDestroy() {

    }
}
