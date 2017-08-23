package com.google.android.apps.miyagi.development.ui.register.signin;

import com.google.android.apps.miyagi.development.data.models.labels.SignInSelectionLabels;
import com.google.android.apps.miyagi.development.ui.BasePresenter;
import com.google.android.apps.miyagi.development.ui.BaseView;

/**
 * Created by marcin on 15.01.2017.
 */

public interface SignInContract {

    interface View extends BaseView<SignInContract.Presenter> {

        /**
         * Initialize UI widgets (buttons).
         * @param googleColor Google button color.
         * @param emailColor Email button color.
         */
        void setUpUi(int googleColor, int emailColor);

        /**
         * Initialize ActionBar.
         * @param displayHomeAsUpEnabled Whether homeAsUp is enabled or not.
         */
        void setUpActionBar(boolean displayHomeAsUpEnabled);

        /**
         * Initialize UI with common texts.
         * @param labels Common texts for UI widgets.
         */
        void bindData(SignInSelectionLabels labels);

        /**
         * Request Google Sign-In method.
         */
        void signInWithGoogle();

    }

    interface Presenter extends BasePresenter<SignInContract.View> {

        /**
         * Set if SignIn is for single market.
         * @param isSingleMarket Whether it is single market.
         */
        void setSingleMarket(boolean isSingleMarket);

        /**
         * Callback on Google login method select.
         */
        void onGoogleLoginSelected();

        /**
         * Callback on email login method select.
         */
        void onEmailLoginSelected();

        /**
         * Callback on Google login success.
         * @param token Token retrieved from Google login.
         * @param authCode Authentication code retrieved from Google login.
         * @param email Email used with Google login.
         * @param isOnline True if device is connected to the Internet.
         */
        void onGoogleLoggedIn(String token, String authCode, String email, boolean isOnline);

    }
}
