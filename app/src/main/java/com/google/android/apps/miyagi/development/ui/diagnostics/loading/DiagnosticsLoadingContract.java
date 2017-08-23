package com.google.android.apps.miyagi.development.ui.diagnostics.loading;

import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;

import java.util.List;

/**
 * Created by lukaszweglinski on 08.03.2017.
 */

public interface DiagnosticsLoadingContract {
    interface View extends BaseView<DiagnosticsLoadingContract.Presenter> {

        void onSuccess(BaseResponse r);

        void startOnboarding();

        /**
         * Callback on async request start.
         */
        void onSubscribe();

        /**
         * Callback on async request fail.
         *
         * @param throwable Async request error.
         */
        void onDataError(Throwable throwable);
    }

    interface Presenter extends BasePresenter<DiagnosticsLoadingContract.View> {
        void submitDiagnostics(String mPersona, List<Integer> mGoals, String mXsrfToken);

        void skipDiagnostics(String mXsrfToken);

        void submitCertification(String mXsrfToken);
    }
}
