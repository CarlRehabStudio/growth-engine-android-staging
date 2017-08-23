package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticSubmitData;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSkipResponse;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsSubmitResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by lukaszweglinski on 17.12.2016.
 */

public interface DiagnosticsServiceApi {

    @GET("api/v1/pages/diagnostics")
    Observable<DiagnosticsResponse> getDiagnostics();

    @POST("api/v1/diagnostics")
    Observable<DiagnosticsSubmitResponse> submitDiagnostics(@Body DiagnosticSubmitData diagnosticSubmitData);

    @POST("api/v1/diagnostics/skip")
    Observable<DiagnosticsSkipResponse> skipDiagnostics(@Body XsrfToken token);

    @POST("api/v1/diagnostics/certification")
    Observable<DiagnosticsSkipResponse> submitCertification(@Body XsrfToken token);
}
