package com.google.android.apps.miyagi.development.ui.register.google;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.CreateAccountMissingInfoLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.validator.NextStepResponseValidator;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.validator.RegisterUserValidator;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.net.services.RegisterUserService;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.utils.InputValidator;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.rengwuxian.materialedittext.MaterialEditText;
import retrofit2.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

/**
 * Created by marcin on 23.12.2016.
 */

public class CompleteAccountInfoFragment extends RegistrationFragment {

    @Inject ConfigStorage mConfigStorage;
    @Inject RegisterUserService mRegisterUserService;
    @Inject NextStepService mNextStepService;

    @BindView(R.id.first_name_input) MaterialEditText mFirstNameInput;
    @BindView(R.id.last_name_input) MaterialEditText mLastNameInput;

    @BindView(R.id.email_notifications_text) TextView mEmailNotificationText;
    @BindView(R.id.email_notifications) Switch mEmailNotification;
    @BindView(R.id.push_notifications_text) TextView mPushNotificationText;
    @BindView(R.id.push_notifications) Switch mPushSwitch;

    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.button_next) NavigationButton mButtonNext;
    @BindView(R.id.button_prev) NavigationButton mButtonPrev;

    @BindView(R.id.root_container) FrameLayout mRootContainer;

    private CreateAccountMissingInfoLabels mLabels;

    private Unbinder mUnbinder;
    private Subscription mSubscription;

    public static CompleteAccountInfoFragment newInstance() {
        return new CompleteAccountInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_complete_account_info_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

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
        mLabels = mConfigStorage.getLabels().getCreateAccountMissingInfoLabels();

        ((RegisterActivity) getActivity()).setTitle(mLabels.getToolbarTitle());
        mButtonNext.setText(mLabels.getButtonNext());

        mFirstNameInput.setHint(mLabels.getFirstName());
        mFirstNameInput.setFloatingLabelText(mLabels.getFirstName());

        mLastNameInput.setHint(mLabels.getLastName());
        mLastNameInput.setFloatingLabelText(mLabels.getLastName());

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

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onNextButtonClicked() {
        registerUser();
    }

    private void registerUser() {
        if (validateInputs()) {
            KeyboardHelper.hideKeyboard(getActivity());

            String firstName = mFirstNameInput.getText().toString();
            String lastName = mLastNameInput.getText().toString();

            SubscriptionHelper.unsubscribe(mSubscription);
            mSubscription = mNextStepService.getNextStepData()
                    .map(NextStepResponseValidator::validate)
                    .flatMap(new Func1<NextStepResponse, Observable<RegisterUserResponse>>() {
                        @Override
                        public Observable<RegisterUserResponse> call(NextStepResponse nextStepResponse) {
                            return mRegisterUserService.registerUser(firstName, lastName, mEmailNotification.isChecked(), nextStepResponse.getResponseData().getXsrfToken())
                                    .map(RegisterUserValidator::validate)
                                    .subscribeOn(Schedulers.io());
                        }
                    })
                    .doOnSubscribe(this::onSubscribe)
                    .subscribe(this::onRegisterUserSuccessful, this::onRegisterUserFailure);
        }
    }

    private void onRegisterUserSuccessful(RegisterUserResponse registerUserResponse) {
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.DIAGNOSTICS);
    }

    private void onButtonErrorClick() {
        // TODO: sign out the user
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
    }

    private void onRegisterUserFailure(Throwable throwable) {
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
                mNavigationCallback.setErrorScreenOnActionClickListener(this::registerUser);
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
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::registerUser);
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

    private void onSubscribe() {
        mNavigationCallback.showLoaderScreen(true);
        mNavigationCallback.showErrorScreen(false);
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
        return isValid;
    }

    private void pushSwitchChanged(boolean isChecked) {
        if (isChecked) {
            String pushToken = FirebaseInstanceId.getInstance().getToken();
            mConfigStorage.savePushToken(pushToken);
            // token will be sent after showing dashboard
            mConfigStorage.saveShouldUpdatePushToken(true);
        } else {
            mConfigStorage.savePushToken(null);
            mConfigStorage.saveShouldUpdatePushToken(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = null;
    }

}

