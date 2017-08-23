package com.google.android.apps.miyagi.development.ui.register.email.create_account;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.CreateAccountFullLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.ui.register.email.check_account.EmailCheckAccountFragment;
import com.google.android.apps.miyagi.development.utils.InputValidator;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.rengwuxian.materialedittext.MaterialEditText;
import retrofit2.HttpException;
import rx.Subscription;

import javax.inject.Inject;

public class EmailCreateAccountFragment extends RegistrationFragment<EmailCreateAccountContract.Presenter> implements EmailCreateAccountContract.View {

    public static final String WEAK_PASSWORD_TEXT = "WEAK_PASSWORD";

    @Inject EmailCreateAccountPresenter mPresenter;

    @Inject AnalyticsHelper mAnalyticsHelper;
    @Inject ConfigStorage mConfigStorage;

    @BindView(R.id.email_address_input) MaterialEditText mEmailInput;
    @BindView(R.id.first_name_input) MaterialEditText mFirstNameInput;
    @BindView(R.id.last_name_input) MaterialEditText mLastNameInput;
    @BindView(R.id.choose_password_input) MaterialEditText mChoosePasswordInput;
    @BindView(R.id.confirm_password_input) MaterialEditText mConfirmPasswordInput;
    @BindView(R.id.scroll_view) ScrollView mScrollView;

    @BindView(R.id.email_notifications_text) TextView mEmailNotificationText;
    @BindView(R.id.email_notifications) Switch mEmailNotification;
    @BindView(R.id.push_notifications_text) TextView mPushNotificationText;
    @BindView(R.id.push_notifications) Switch mPushSwitch;

    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.button_next) NavigationButton mButtonNext;
    @BindView(R.id.button_prev) NavigationButton mButtonPrev;

    private CreateAccountFullLabels mLabels;
    private Unbinder mUnbinder;
    private Subscription mSubscription;
    private boolean mIsRegistered;

    public static EmailCreateAccountFragment newInstance() {
        return new EmailCreateAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_email_create_account_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_registration_sign_up));

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
        if (mIsRegistered) {
            onCreateAccountProcessSuccessful();
        } else {
            mNavigationCallback.showLoaderScreen(false);
        }
    }


    private void bindData() {
        mLabels = mConfigStorage.getLabels().getCreateAccountFullLabels();

        ((RegisterActivity) getActivity()).setTitle(mLabels.getToolbarTitle());
        mButtonNext.setText(mLabels.getButtonNext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            String email = bundle.getString(EmailCheckAccountFragment.BUNDLE_EMAIL);
            mEmailInput.setText(email);
            mEmailInput.setEnabled(false);
            mEmailInput.setHint(mLabels.getEmailHint());
            mEmailInput.setFloatingLabelText(mLabels.getEmailHint());
        }

        mFirstNameInput.setHint(mLabels.getFirstName());
        mLastNameInput.setHint(mLabels.getLastName());
        mChoosePasswordInput.setHint(mLabels.getChoosePassword());
        mConfirmPasswordInput.setHint(mLabels.getConfirmPassword());

        mFirstNameInput.setFloatingLabelText(mLabels.getFirstName());
        mLastNameInput.setFloatingLabelText(mLabels.getLastName());
        mChoosePasswordInput.setFloatingLabelText(mLabels.getChoosePassword());
        mConfirmPasswordInput.setFloatingLabelText(mLabels.getConfirmPassword());

        mEmailNotificationText.setText(mLabels.getEmailNotifications());
        mPushNotificationText.setText(mLabels.getPushNotifications());
    }

    private void setupUi() {
        setupActionBar();

        mNavigationCallback.setErrorScreenOnActionClickListener(this::onButtonErrorClick);

        mBottomNav.setBackgroundColor(mConfigStorage.getLabels().getColors().getMainBackgroundColor());
        mButtonNext.setOnClickListener(v -> onNextButtonClicked());
        mButtonPrev.setVisibility(View.VISIBLE);
        mButtonPrev.setOnClickListener(v -> mNavigationCallback.goBack());
        mPushSwitch.setChecked(mConfigStorage.readPushToken() != null);
        mPushSwitch.jumpDrawablesToCurrentState();
        mPushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pushSwitchChanged(isChecked);
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
        createAccount();
    }

    private void createAccount() {
        if (validateInputs()) {
            String email = mEmailInput.getText().toString();
            String password = mChoosePasswordInput.getText().toString();
            String firstName = mFirstNameInput.getText().toString();
            String lastName = mLastNameInput.getText().toString();

            getPresenter().createAccount(email, password, firstName, lastName, mEmailNotification.isChecked());
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String firstName = mFirstNameInput.getText().toString();
        if (!InputValidator.isValidName(firstName)) {
            mFirstNameInput.setError(mLabels.getValidationEmptyFirstName());
            isValid = false;
        } else {
            mFirstNameInput.setError(null);
        }

        String lastName = mLastNameInput.getText().toString();
        if (!InputValidator.isValidName(lastName)) {
            mLastNameInput.setError(mLabels.getValidationEmptyLastName());
            isValid = false;
        } else {
            mLastNameInput.setError(null);
        }

        String chosenPassword = mChoosePasswordInput.getText().toString();
        if (!InputValidator.isValidPassword(chosenPassword)) {
            mChoosePasswordInput.setError(mLabels.getValidationEmptyPassword());
            isValid = false;
        } else {
            mChoosePasswordInput.setError(null);
        }

        String confirmedPassword = mConfirmPasswordInput.getText().toString();
        if (!confirmedPassword.equals(chosenPassword)) {
            mConfirmPasswordInput.setError(mLabels.getValidationPasswordMismatch());
            isValid = false;
        } else {
            mConfirmPasswordInput.setError(null);
        }
        KeyboardHelper.hideKeyboard(getActivity());
        return isValid;
    }

    @Override
    public void onCreateAccountProcessSuccessful(RegisterUserResponse registerUserResponse) {
        mIsRegistered = true;
        onCreateAccountProcessSuccessful();
    }

    private void onCreateAccountProcessSuccessful() {
        mAnalyticsHelper.trackEvent(getString(R.string.screen_registration_sign_up), getString(R.string.event_category_registration), getString(R.string.event_action_registered), getString(R.string.event_action_registered));
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.DIAGNOSTICS);
    }

    @Override
    public void onCreateAccountProcessFailure(Throwable throwable) {
        mNavigationCallback.showLoaderScreen(false);
        mNavigationCallback.setErrorScreenText(throwable);

        // https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.html#createUserWithEmailAndPassword(java.lang.String, java.lang.String)
        if (throwable instanceof FirebaseException) {
            FirebaseMessagesLabels labels = mConfigStorage.getLabels().getFirebaseMessagesLabels();
            if (throwable instanceof FirebaseAuthWeakPasswordException) {
                // thrown if the password is not strong enough
                mChoosePasswordInput.setError(labels.getWeakPassword());
                KeyboardHelper.hideKeyboard(getActivity());
            } else if (throwable instanceof FirebaseAuthInvalidCredentialsException) {
                // thrown if the email address is malformed
                mChoosePasswordInput.setError(labels.getInvalidEmail());
                KeyboardHelper.hideKeyboard(getActivity());
            } else if (throwable instanceof FirebaseAuthUserCollisionException) {
                // thrown if there already exists an account with the given email address
                mEmailInput.setError(labels.getEmailExists());
                KeyboardHelper.hideKeyboard(getActivity());
                mScrollView.scrollTo(0, 0);
            } else if (throwable instanceof FirebaseNetworkException) {
                mNavigationCallback.setErrorScreenOnActionClickListener(this::createAccount);
                mNavigationCallback.setErrorScreenText(throwable);
                mNavigationCallback.showErrorScreen(true);
            } else if (throwable.getMessage().contains(WEAK_PASSWORD_TEXT)) {
                //fix for Firebase Auth with weak password bug
                mChoosePasswordInput.setError(labels.getWeakPassword());
                KeyboardHelper.hideKeyboard(getActivity());
            } else {
                mNavigationCallback.setErrorScreenText(throwable);
                mNavigationCallback.showErrorScreen(true);
            }
        } else {
            if (throwable instanceof RetrofitException) {
                RetrofitException exception = (RetrofitException) throwable;
                if (exception.getKind() == RetrofitException.Kind.INVALID_TOKEN) {
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
                } else if (exception.getKind() == RetrofitException.Kind.NETWORK) {
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::createAccount);
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

    private void pushSwitchChanged(boolean isChecked) {
        if (isChecked) {
            String pushToken = FirebaseInstanceId.getInstance().getToken();
            mConfigStorage.savePushToken(pushToken);
            // token will be sent after showing dashboard
            mConfigStorage.saveShouldUpdatePushToken(true);
        } else {
            mConfigStorage.saveShouldUpdatePushToken(false);
            mConfigStorage.savePushToken(null);
        }
    }

    @Override
    public void onSubscribe() {
        mNavigationCallback.showLoaderScreen(true);
        mNavigationCallback.showErrorScreen(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = null;
        mUnbinder.unbind();
        mUnbinder = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }

    @Override
    public EmailCreateAccountContract.Presenter getPresenter() {
        return mPresenter;
    }
}
