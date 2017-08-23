package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.DiagnosticsServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticSubmitData;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSkipResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSubmitResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Created by lukaszweglinski on 17.12.2016.
 */

public class DiagnosticsServiceImpl extends AbsBaseService<DiagnosticsServiceApi> implements DiagnosticsService {

    private static long DELAY_TIME_SEC = 2;

    /**
     * @param retrofitProvider - provides configured retrofit instance.
     */
    public DiagnosticsServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, DiagnosticsServiceApi.class);
    }

    @Override
    public Observable<DiagnosticsResponse> getDiagnostics() {
        return mApi.getDiagnostics().compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<DiagnosticsSubmitResponse> submitDiagnostics(DiagnosticSubmitData diagnosticSubmitData) {
        return observableWithDelay(mApi.submitDiagnostics(diagnosticSubmitData)).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<DiagnosticsSkipResponse> skipDiagnostics(XsrfToken token) {
        return observableWithDelay(mApi.skipDiagnostics(token)).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<DiagnosticsSkipResponse> submitCertification(XsrfToken token) {
        return observableWithDelay(mApi.submitCertification(token)).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    private <T> Observable<T> observableWithDelay(Observable<T> observable) {
        return Observable.zip(observable, Observable.timer(DELAY_TIME_SEC, TimeUnit.SECONDS), (diagnosticsResponse, aLong) -> diagnosticsResponse);
    }
}
