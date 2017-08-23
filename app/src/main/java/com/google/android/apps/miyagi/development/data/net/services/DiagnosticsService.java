package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticSubmitData;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSkipResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSubmitResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;

import rx.Observable;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

public interface DiagnosticsService extends BaseService {

    Observable<DiagnosticsResponse> getDiagnostics();

    Observable<DiagnosticsSubmitResponse> submitDiagnostics(DiagnosticSubmitData diagnosticSubmitData);

    Observable<DiagnosticsSkipResponse> skipDiagnostics(XsrfToken token);

    Observable<DiagnosticsSkipResponse> submitCertification(XsrfToken token);
}
