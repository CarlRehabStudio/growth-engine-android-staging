package com.google.android.apps.miyagi.development.ui.register.email.enter_password;

import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;

/**
 * Created by lukaszweglinski on 10.04.2017.
 */

public interface EmailEnterPasswordContract {
    interface View extends BaseView<EmailEnterPasswordContract.Presenter> {

        void onSubscribe();

        void onLoginProcessCompletedSuccessful(NextStepResponse nextStepResponse);

        void onLoginProcessFailure(Throwable throwable);
    }

    interface Presenter extends BasePresenter<EmailEnterPasswordContract.View> {

        void signInWithEmail(String email, String password);

        void onLoginProcessCompletedSuccessful(NextStepResponse nextStepResponse);

        void onLoginProcessFailure(Throwable throwable);
    }
}