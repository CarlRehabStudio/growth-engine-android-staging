package com.google.android.apps.miyagi.development.ui.register.email.check_account;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.CheckAccountLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.FirebaseProviders;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.utils.InputValidator;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.ProviderQueryResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.rengwuxian.materialedittext.MaterialEditText;
import retrofit2.HttpException;

import java.util.List;

import javax.inject.Inject;

/**
 * A screen that offers to sign in - asks for email.
 */
public class EmailCheckAccountFragment extends RegistrationFragment<EmailCheckAccountContract.Presenter> implements EmailCheckAccountContract.View {

    public static final String BUNDLE_EMAIL = "EMAIL";

    @Inject EmailCheckAccountPresenter mPresenter;
    @Inject ConfigStorage mConfigStorage;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.email_address_input) MaterialEditText mEmailInput;

    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.button_prev) NavigationButton mButtonPrev;
    @BindView(R.id.button_next) NavigationButton mButtonNext;

    private Unbinder mUnbinder;
    private CheckAccountLabels mLabels;
    private boolean mIsFirstTime = true;


    /**
     * Creates new EmailCheckAccountFragment.
     */
    public static EmailCheckAccountFragment newInstance() {
        return new EmailCheckAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_email_check_account_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_registration_sign_in));

        setupUi();
        bindData();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!ViewUtils.isTablet(getContext())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void bindData() {
        mLabels = mConfigStorage.getLabels().getCheckAccountLabels();

        ((RegisterActivity) getActivity()).setTitle(mLabels.getToolbarTitle());
        mButtonNext.setText(mLabels.getButtonNext());

        if (mIsFirstTime) {
            mEmailInput.setHint(mLabels.getEmailInputHint());
            mEmailInput.setFloatingLabelText(mLabels.getEmailInputHint());
        } else {
            mEmailInput.setHint(mLabels.getEmail());
            mEmailInput.setFloatingLabelText(mLabels.getEmail());
        }
    }

    private void setupUi() {
        mNavigationCallback.setErrorScreenOnActionClickListener(this::onButtonErrorClick);

        setupActionBar();

        mBottomNav.setBackgroundColor(mConfigStorage.getLabels().getColors().getMainBackgroundColor());
        mButtonNext.setOnClickListener(v -> onNextButtonClicked());
        mButtonPrev.setVisibility(View.VISIBLE);
        mButtonPrev.setOnClickListener(v -> mNavigationCallback.goBack());

        mEmailInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onNextButtonClicked();
            }
            return false;
        });
    }

    private void onButtonErrorClick() {
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onNextButtonClicked() {
        mIsFirstTime = false;
        checkAccount();
    }

    private void checkAccount() {
        final String email = mEmailInput.getText().toString();

        if (!InputValidator.notNullAndNotEmpty(email)) {
            mEmailInput.setError(mLabels.getEmailRequiredError());
        } else if (!InputValidator.isValidEmail(email)) {
            FirebaseMessagesLabels labels = mConfigStorage.getLabels().getFirebaseMessagesLabels();
            mEmailInput.setError(labels.getInvalidEmail());
        } else {
            mEmailInput.setError(null);
            KeyboardHelper.hideKeyboard(getActivity());
            getPresenter().checkAccount(email);
        }
    }

    @Override
    public void onCheckAccountReceived(String email, ProviderQueryResult providerQueryResult) {
        List<String> providers = providerQueryResult.getProviders();
        boolean hasEmailProvider = false;
        boolean hasGoogleProvider = false;

        if (providers != null && providers.size() > 0) {
            for (String provider : providers) {
                if (FirebaseProviders.EMAIL.equals(provider)) {
                    hasEmailProvider = true;
                    break;
                }
            }
            for (String provider : providers) {
                if (FirebaseProviders.GOOGLE.equals(provider)) {
                    hasGoogleProvider = true;
                    break;
                }
            }
        }

        if (hasEmailProvider) {
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.EMAIL_ENTER_PASSWORD, BUNDLE_EMAIL, email);
        } else if (hasGoogleProvider) {
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.SIGN_IN_WITH_GOOGLE, BUNDLE_EMAIL, email);
        } else {
            // create local account
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.EMAIL_CREATE_ACCOUNT, BUNDLE_EMAIL, email);
        }
    }

    @Override
    public void onCheckAccountFailure(Throwable throwable) {
        mNavigationCallback.showLoaderScreen(false);
        mNavigationCallback.setErrorScreenText(throwable);

        if (throwable instanceof FirebaseException) {
            FirebaseMessagesLabels labels = mConfigStorage.getLabels().getFirebaseMessagesLabels();
            // https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.html#signInWithCredential(com.google.firebase.auth.AuthCredential)
            if (throwable instanceof FirebaseAuthInvalidUserException) {
                //  thrown if the user account you are trying to sign in to has been disabled. Also thrown if credential is an EmailAuthCredential with an email address that does not correspond to an existing user.
                mNavigationCallback.setErrorScreenMessage(labels.getInvalidEmail());
            } else if (throwable instanceof FirebaseAuthInvalidCredentialsException) {
                // thrown if the credential is malformed or has expired. If credential instanceof EmailAuthCredential it will be thrown if the password is incorrect.
                mNavigationCallback.setErrorScreenMessage(labels.getInvalidAuth());
            } else if (throwable instanceof FirebaseAuthUserCollisionException) {
                // thrown if there already exists an account with the email address asserted by the credential.
                mNavigationCallback.setErrorScreenMessage(labels.getEmailExists());
            } else if (throwable instanceof FirebaseNetworkException) {
                mNavigationCallback.setErrorScreenOnActionClickListener(this::checkAccount);
                mNavigationCallback.setErrorScreenText(throwable);
            } else {
                mNavigationCallback.setErrorScreenMessage(labels.getInternalError());
            }
            mNavigationCallback.showErrorScreen(true);
        } else {
            if (throwable instanceof RetrofitException) {
                RetrofitException exception = (RetrofitException) throwable;
                if (exception.getKind() == RetrofitException.Kind.INVALID_TOKEN) {
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
                } else if (exception.getKind() == RetrofitException.Kind.NETWORK) {
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::checkAccount);
                    mNavigationCallback.setErrorScreenText(throwable);
                    mNavigationCallback.showErrorScreen(true);
                } else {
                    mNavigationCallback.setErrorScreenText(throwable);
                    mNavigationCallback.showErrorScreen(true);
                }
            } else if (throwable instanceof HttpException) {
                HttpException exception = (HttpException) throwable;
                if (exception.code() == HttpStatusCode.INVALID_TOKEN) {
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
                }
            } else if (throwable instanceof DeletedAccountException) {
                ErrorsLabels errorsLabels = mConfigStorage.getLabels().getErrorsLabels();
                mNavigationCallback.setErrorScreenButton(errorsLabels.getDeletedAccountButton());
                mNavigationCallback.setErrorScreenMessage(errorsLabels.getDeletedAccount());
                mNavigationCallback.setErrorScreenOnActionClickListener(this::onDeletedAccountErrorButtonClick);
                mNavigationCallback.showErrorScreen(true);
            }
        }
    }

    private void onDeletedAccountErrorButtonClick() {
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
    }

    public void onSubscribe() {
        mNavigationCallback.showLoaderScreen(true);
        mNavigationCallback.showErrorScreen(false);
    }

    @Override
    public void onTerminate() {
        mNavigationCallback.showLoaderScreen(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }


    @Override
    public EmailCheckAccountPresenter getPresenter() {
        return mPresenter;
    }
}
