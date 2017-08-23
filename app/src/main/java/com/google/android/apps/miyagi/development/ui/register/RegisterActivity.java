package com.google.android.apps.miyagi.development.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.net.responses.onboarding.OnboardingResponseData;
import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.helpers.PreloaderHelper;
import com.google.android.apps.miyagi.development.helpers.SignOutUserHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;
import com.google.android.apps.miyagi.development.ui.diagnostics.DiagnosticsActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingPrefs;
import com.google.android.apps.miyagi.development.ui.onboarding.common.OnboardingType;
import com.google.android.apps.miyagi.development.ui.register.common.FirebaseProviders;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegisterBundleKey;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.ui.register.email.EmailResetPasswordFragment;
import com.google.android.apps.miyagi.development.ui.register.email.check_account.EmailCheckAccountFragment;
import com.google.android.apps.miyagi.development.ui.register.email.create_account.EmailCreateAccountFragment;
import com.google.android.apps.miyagi.development.ui.register.email.enter_password.EmailEnterPasswordFragment;
import com.google.android.apps.miyagi.development.ui.register.google.CompleteAccountInfoFragment;
import com.google.android.apps.miyagi.development.ui.register.market.MarketSelectionFragment;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInSelectionFragment;
import com.google.android.apps.miyagi.development.ui.register.signin.SignInWithProviderFragment;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.ToolbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;

import static com.google.android.apps.miyagi.development.ui.onboarding.OnboardingActivity.Arg.FROM_DIAGNOSTICS;

import javax.inject.Inject;

/**
 * A register screen that offers to sign in / sign up to app service.
 */
public class RegisterActivity extends BaseActivity<RegisterContract.Presenter> implements RegisterContract.View, NavigationCallback {

    @Inject CurrentSessionCache mCurrentSessionCache;
    @Inject RegisterPresenter mPresenter;
    @Inject Lazy<SignOutUserHelper> mSignOutUserHelper;
    @Inject Lazy<OnboardingPrefs> mOnboardingPrefs;

    @Nullable
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private PreloaderHelper mPreloaderHelper;
    private ErrorScreenHelper mErrorHelper;
    private boolean mSingleMarket = false;

    public static Intent createIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);

        ToolbarHelper.setUpChildActivityToolbar(this);

        mPreloaderHelper = new PreloaderHelper(findViewById(R.id.preloader_screen));
        mErrorHelper = new ErrorScreenHelper(findViewById(R.id.error_screen));
        mErrorHelper.setOnNavigationClickListener(() -> mErrorHelper.hide());

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            goToStepRqst(RegistrationSteps.MARKET_SELECTION);
        }
    }

    /**
     * Set both Toolbar and CollapsingToolbar title (collapsed and expadned title)
     */
    public void setTitle(String title) {
        super.setTitle(title);
        mToolbar.setTitle(title);
        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setTitle(title);
        }
    }

    private void getCommonData(RegistrationSteps step) {
        mErrorHelper.setOnActionClickListener(() -> getCommonData(step));
        mPresenter.getCommonData(step, mOnboardingPrefs.get().isOnboardingRequired(OnboardingType.DASHBOARD));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Changes current registration step into provided one.
     *
     * @param step - expected registration step.
     */
    public void goToStep(NavigationCallback.RegistrationSteps step, String key, String data) {
        Bundle bundle = new Bundle();
        if (key != null && data != null) {
            bundle.putString(key, data);
        }
        RegistrationFragment fragment = null;
        switch (step) {
            case MARKET_SELECTION:
                // when back to market selection, clean up fragment back stack.
                cleanUpFragmentBackStack();
                mSignOutUserHelper.get().signOut();
                fragment = MarketSelectionFragment.newInstance();
                break;
            case EMAIL_CHECK_ACCOUNT_EXISTS:
                fragment = EmailCheckAccountFragment.newInstance();
                break;
            case EMAIL_ENTER_PASSWORD:
                fragment = EmailEnterPasswordFragment.newInstance();
                break;
            case EMAIL_RESET_PASSWORD:
                fragment = EmailResetPasswordFragment.newInstance();
                break;
            case EMAIL_CREATE_ACCOUNT:
                fragment = EmailCreateAccountFragment.newInstance();
                break;
            case COMPLETE_PROFILE:
                fragment = CompleteAccountInfoFragment.newInstance();
                break;
            case DASHBOARD:
                mCurrentSessionCache.setOnboardingUserType(CurrentSessionCache.OnboardingUserType.RETURNING_USER);
                getCommonData(step);
                return;
            case DIAGNOSTICS:
                mCurrentSessionCache.setOnboardingUserType(CurrentSessionCache.OnboardingUserType.NEW_USER);
                getCommonData(step);
                return;
            case SIGN_IN_WITH_EMAIL:
                fragment = SignInWithProviderFragment.newInstance();
                bundle.putString(RegisterBundleKey.PROVIDER, FirebaseProviders.EMAIL);
                break;
            case SIGN_IN_WITH_GOOGLE:
                fragment = SignInWithProviderFragment.newInstance();
                bundle.putString(RegisterBundleKey.PROVIDER, FirebaseProviders.GOOGLE);
                break;
            default:
                Lh.e(this, "Unknown screen Id!" + step);
                return;
        }
        fragment.setArguments(bundle);
        transitionFragment(fragment);
    }

    private void cleanUpFragmentBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int fragmentCount = fragmentManager.getBackStackEntryCount();
        while (fragmentCount > 0) {
            fragmentManager.popBackStackImmediate();
            --fragmentCount;
        }
    }

    private void transitionFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getCanonicalName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void goToSignInSelection(boolean singleMarket) {
        SignInSelectionFragment fragment = SignInSelectionFragment.newInstance(mSingleMarket);
        transitionFragment(fragment);
    }

    /**
     * Callback method from Fragments.
     *
     * @param step - step id
     */
    @Override
    public void goToStepRqst(RegistrationSteps step) {
        goToStep(step, null, null);
    }

    /**
     * Callback method from Fragments.
     *
     * @param step - step id
     * @param key  - key identifying step's data
     * @param data - step's data
     */
    @Override
    public void goToStepRqst(RegistrationSteps step, String key, String data) {
        goToStep(step, key, data);
    }

    @Override
    public void goBack() {
        onBackPressed();
        KeyboardHelper.hideKeyboard(this);
    }

    @Override
    public void setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener actionClickListener) {
        mErrorHelper.setOnActionClickListener(actionClickListener);
    }

    @Override
    public void setErrorScreenText(Throwable throwable) {
        mErrorHelper.setErrorForLoggedOut(throwable);
    }

    @Override
    public void setErrorScreenMessage(String text) {
        mErrorHelper.setMessage(text);
    }

    @Override
    public void setErrorScreenButton(String text) {
        mErrorHelper.setButton(text);
    }

    @Override
    public void showLoaderScreen(boolean show) {
        if (show) {
            mPreloaderHelper.show();
        } else {
            mPreloaderHelper.hide();
        }
    }

    @Override
    public void showErrorScreen(boolean show) {
        mErrorHelper.showNavigationButton(true);
        if (show) {
            mErrorHelper.show();
        } else {
            mErrorHelper.hide();
        }
    }

    @Override
    public void showErrorScreenWithoutNavigationButton(boolean show) {
        mErrorHelper.showNavigationButton(false);
        if (show) {
            mErrorHelper.show();
        } else {
            mErrorHelper.hide();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1 && mSingleMarket) {
            finish();
        } else if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void onSubscribe() {
        mPreloaderHelper.show();
        mErrorHelper.hide();
    }

    @Override
    public void onDataError(Throwable throwable) {
        showLoaderScreen(false);
        mErrorHelper.showNavigationButton(true);
        mErrorHelper.setErrorForLoggedOut(throwable);
        mErrorHelper.show();
    }

    @Override
    public void goToDiagnostics() {
        startActivity(DiagnosticsActivity.createIntent(this));
        finish();
    }

    @Override
    public void goToOnboarding(OnboardingResponseData onboardingResponse) {
        boolean fromDiagnostics = getIntent().getBooleanExtra(FROM_DIAGNOSTICS, false);
        startActivity(OnboardingActivity.createIntent(this, OnboardingType.DASHBOARD, fromDiagnostics, onboardingResponse));
        finish();
    }

    @Override
    public void goToDashboard() {
        startActivity(DashboardActivity.createIntent(this, false));
        finish();
    }

    @Override
    public void onScreenEnter() {
        ((GoogleApplication) getApplication()).createRegisterComponent();
    }

    @Override
    public void onScreenExit() {
        ((GoogleApplication) getApplication()).releaseRegisterComponent();
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) getApplication()).getRegisterComponent().inject(this);
    }

    @Override
    public RegisterPresenter getPresenter() {
        return mPresenter;
    }
}