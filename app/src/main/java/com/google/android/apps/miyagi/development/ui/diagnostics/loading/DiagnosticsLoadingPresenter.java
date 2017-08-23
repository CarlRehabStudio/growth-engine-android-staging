package com.google.android.apps.miyagi.development.ui.diagnostics.loading;

import android.content.Context;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;
import com.google.android.apps.miyagi.development.data.net.responses.dashbord.DashboardResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticSubmitData;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSkipResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSubmitResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.validator.DiagnosticsSubmitValidator;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;
import com.google.android.apps.miyagi.development.data.net.services.DashboardService;
import com.google.android.apps.miyagi.development.data.net.services.DiagnosticsService;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import rx.Observable;
import rx.Subscription;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 08.03.2017.
 */

public class DiagnosticsLoadingPresenter extends BasePresenterImpl<DiagnosticsLoadingContract.View> implements DiagnosticsLoadingContract.Presenter {

    @Inject DiagnosticsService mDiagnosticsService;
    @Inject ConfigStorage mConfigStorage;
    @Inject DashboardService mDashboardService;
    @Inject CurrentSessionCache mCurrentSessionCache;

    private Observable<DiagnosticsSubmitResponse> mDiagnosticsSubmitObservable;
    private Observable<DiagnosticsSkipResponse> mDiagnosticsSkipObservable;
    private Observable<DiagnosticsSkipResponse> mDiagnosticsSubmitCertificateObservable;
    private Subscription mApiSubscription;

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void submitDiagnostics(String mPersona, List<Integer> mGoals, String mXsrfToken) {
        SubscriptionHelper.unsubscribe(mApiSubscription);

        if (mDiagnosticsSubmitObservable == null) {
            String stepTwoId = mPersona != null ? mPersona : null;
            DiagnosticSubmitData diagnosticSubmitData = new DiagnosticSubmitData(stepTwoId, mGoals, mXsrfToken);
            mDiagnosticsSubmitObservable = mDiagnosticsService.submitDiagnostics(diagnosticSubmitData)
                    .zipWith(mDashboardService.getDashboardData(), (diagnosticsSubmitResponse, dashboardResponse) -> {
                        onDashboardDataReceived(dashboardResponse);
                        return diagnosticsSubmitResponse;
                    })
                    .map(DiagnosticsSubmitValidator::validate)
                    .doOnSubscribe(this::onSubscribe)
                    .cache();
        }

        mApiSubscription = mDiagnosticsSubmitObservable
                .subscribe(this::onSuccess, this::onDataError);
    }

    @Override
    public void skipDiagnostics(String mXsrfToken) {
        SubscriptionHelper.unsubscribe(mApiSubscription);

        if (mDiagnosticsSkipObservable == null) {
            mDiagnosticsSkipObservable = mDiagnosticsService.skipDiagnostics(new XsrfToken(mXsrfToken))
                    .zipWith(mDashboardService.getDashboardData(), (diagnosticsSubmitResponse, dashboardResponse) -> {
                        onDashboardDataReceived(dashboardResponse);
                        return diagnosticsSubmitResponse;
                    })
                    .doOnSubscribe(this::onSubscribe)
                    .cache();
        }

        mApiSubscription = mDiagnosticsSkipObservable
                .subscribe(this::onSuccess, this::onDataError);
    }

    @Override
    public void submitCertification(String mXsrfToken) {
        SubscriptionHelper.unsubscribe(mApiSubscription);

        if (mDiagnosticsSubmitCertificateObservable == null) {
            mDiagnosticsSubmitCertificateObservable = mDiagnosticsService.submitCertification(new XsrfToken(mXsrfToken))
                    .zipWith(mDashboardService.getDashboardData(), (diagnosticsSubmitResponse, dashboardResponse) -> {
                        onDashboardDataReceived(dashboardResponse);
                        return diagnosticsSubmitResponse;
                    })
                    .doOnSubscribe(this::onSubscribe)
                    .cache();
        }

        mApiSubscription = mDiagnosticsSubmitCertificateObservable
                .subscribe(this::onSuccess, this::onDataError);
    }

    private void onSuccess(BaseResponse r) {
        if (getView() != null) {
            getView().startOnboarding();
        }
    }

    private void onSubscribe() {
        if (getView() != null) {
            getView().onSubscribe();
        }
    }

    private void onDataError(Throwable throwable) {
        mDiagnosticsSubmitObservable = null;
        mDiagnosticsSubmitCertificateObservable = null;
        mDiagnosticsSkipObservable = null;

        if (getView() != null) {
            getView().onDataError(throwable);
        }
    }

    private void onDashboardDataReceived(DashboardResponse response) {
        mCurrentSessionCache.setDashboardResponse(response);
        mConfigStorage.saveShouldUpdateDashboard(false);
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
    }

    @Override
    public void onDestroy() {

    }
}