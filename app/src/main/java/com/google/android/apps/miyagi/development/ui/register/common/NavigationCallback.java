package com.google.android.apps.miyagi.development.ui.register.common;

import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by jerzyw on 10.10.2016.
 */

public interface NavigationCallback {

    NavigationCallback NULL = new NavigationCallback() {
        @Override
        public void goToSignInSelection(boolean singleMarket) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void goToStepRqst(RegistrationSteps step) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change to other fragment.");
        }

        @Override
        public void goToStepRqst(RegistrationSteps step, String key, String data) {
            Lh.e(this, "Fragment not attached to Activity. Can't change fragment.");
        }

        @Override
        public void goBack() {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener actionClickListener) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void setErrorScreenText(Throwable throwable) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void setErrorScreenMessage(String text) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void setErrorScreenButton(String text) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void showLoaderScreen(boolean show) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void showErrorScreen(boolean show) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }

        @Override
        public void showErrorScreenWithoutNavigationButton(boolean show) {
            Lh.e(this, "Fragment is not attached to Activity. Can't change fragment.");
        }
    };

    void goToSignInSelection(boolean singleMarket);

    void goToStepRqst(RegistrationSteps step);

    void goToStepRqst(RegistrationSteps step, String key, String data);

    void goBack();

    void setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener actionClickListener);

    void setErrorScreenText(Throwable throwable);

    void setErrorScreenMessage(String text);

    void setErrorScreenButton(String text);

    void showLoaderScreen(boolean show);

    void showErrorScreen(boolean show);

    void showErrorScreenWithoutNavigationButton(boolean show);

    /**
     * Available registration steps.
     */
    enum RegistrationSteps {
        MARKET_SELECTION,
        EMAIL_CHECK_ACCOUNT_EXISTS,
        EMAIL_ENTER_PASSWORD,
        EMAIL_RESET_PASSWORD,
        EMAIL_CREATE_ACCOUNT,
        COMPLETE_PROFILE,
        DASHBOARD,
        DIAGNOSTICS,
        SIGN_IN_WITH_EMAIL,
        SIGN_IN_WITH_GOOGLE
    }
}
