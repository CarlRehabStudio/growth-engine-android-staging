package com.google.android.apps.miyagi.development.ui.register.email.create_account;

import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;

/**
 * Created by lukaszweglinski on 10.04.2017.
 */

public interface EmailCreateAccountContract {
    interface View extends BaseView<EmailCreateAccountContract.Presenter> {

        void onSubscribe();

        void onCreateAccountProcessSuccessful(RegisterUserResponse registerUserResponse);

        void onCreateAccountProcessFailure(Throwable throwable);
    }

    interface Presenter extends BasePresenter<EmailCreateAccountContract.View> {

        void createAccount(String email, String password, String firstName, String lastName, boolean isEmailNotification);

        void onCreateAccountProcessSuccessful(RegisterUserResponse registerUserResponse);

        void onCreateAccountProcessFailure(Throwable throwable);
    }
}