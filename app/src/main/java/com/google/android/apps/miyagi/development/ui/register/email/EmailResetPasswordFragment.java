package com.google.android.apps.miyagi.development.ui.register.email;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ResetPasswordLabels;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegisterBundleKey;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.utils.RxFirebaseAuth;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.google.android.apps.miyagi.development.ui.register.email.EmailResetPasswordFragment.BundleKeys.IS_MESSAG_VISIBLE;
import com.rengwuxian.materialedittext.MaterialEditText;
import retrofit2.HttpException;
import rx.Subscription;

import javax.inject.Inject;

/**
 * Created by marcinarciszew on 21.03.2017.
 */

public class EmailResetPasswordFragment extends RegistrationFragment {

    @Inject ConfigStorage mConfigStorage;

    @BindView(R.id.email_address_input) MaterialEditText mEmailInput;
    @BindView(R.id.label_message) TextView mLabelMessage;

    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.button_prev) NavigationButton mButtonPrev;
    @BindView(R.id.button_next) NavigationButton mButtonNext;

    private Unbinder mUnbinder;

    private Subscription mSubscription;
    private String mEmail;
    private boolean mIsMessageVisible;

    public static EmailResetPasswordFragment newInstance() {
        return new EmailResetPasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_email_reset_password_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mEmail = bundle.getString(RegisterBundleKey.EMAIL);
        }

        if (savedInstanceState != null) {
            mIsMessageVisible = savedInstanceState.getBoolean(IS_MESSAG_VISIBLE, false);
        }

        setupUi();
        bindData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!ViewUtils.isTablet(getContext())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = null;
    }

    private void bindData() {
        ResetPasswordLabels labels = mConfigStorage.getLabels().getResetPasswordLabels();
        getActivity().setTitle(labels.getResetHeader());
        mButtonNext.setText(labels.getCtaText());
        mLabelMessage.setText(labels.getFeedbackText().replace("$1", mEmail));

        mEmailInput.setText(mEmail);
    }

    private void setupUi() {
        setupActionBar();

        mNavigationCallback.setErrorScreenOnActionClickListener(this::onButtonErrorClick);

        mBottomNav.setBackgroundColor(mConfigStorage.getLabels().getColors().getMainBackgroundColor());
        mButtonNext.setOnClickListener(v -> onNextButtonClicked());
        mButtonNext.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);


        mButtonPrev.setVisibility(View.GONE);

        mEmailInput.setEnabled(false);

        if (mIsMessageVisible) {
            mLabelMessage.setVisibility(View.VISIBLE);
        } else {
            mLabelMessage.setVisibility(View.GONE);
        }
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void onButtonErrorClick() {
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
    }

    @Override
    public void onNextButtonClicked() {
        resetPassword();
    }

    private void resetPassword() {
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = RxFirebaseAuth.resetPassword(FirebaseAuth.getInstance(), mEmail)
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(this::onResetSuccess, this::onResetFailure);
    }

    private void onSubscribe() {
        mNavigationCallback.showLoaderScreen(true);
        mNavigationCallback.showErrorScreen(false);
    }

    private void onTerminate() {
        mNavigationCallback.showLoaderScreen(false);
    }

    private void onResetSuccess(Void aVoid) {
        mIsMessageVisible = true;
        mLabelMessage.setVisibility(View.VISIBLE);
    }

    private void onResetFailure(Throwable throwable) {
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
                mNavigationCallback.setErrorScreenOnActionClickListener(this::resetPassword);
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
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::resetPassword);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_MESSAG_VISIBLE, mIsMessageVisible);
        super.onSaveInstanceState(outState);
    }

    public interface BundleKeys {
        String IS_MESSAG_VISIBLE = "IS_MESSAG_VISIBLE";
    }
}
