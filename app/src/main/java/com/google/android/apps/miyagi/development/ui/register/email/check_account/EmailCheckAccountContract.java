package com.google.android.apps.miyagi.development.ui.register.email.check_account;

import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;
import com.google.firebase.auth.ProviderQueryResult;

/**
 * Created by lukaszweglinski on 10.04.2017.
 */

public interface EmailCheckAccountContract {
    interface View extends BaseView<EmailCheckAccountContract.Presenter> {

        void onSubscribe();

        void onCheckAccountReceived(String email, ProviderQueryResult providerQueryResult);

        void onCheckAccountFailure(Throwable throwable);

        void onTerminate();
    }

    interface Presenter extends BasePresenter<EmailCheckAccountContract.View> {

        void checkAccount(String email);
    }
}