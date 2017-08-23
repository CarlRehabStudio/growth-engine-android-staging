package com.google.android.apps.miyagi.development.ui.register.email.enter_password;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.EnterPasswordLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponseData;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.KeyboardHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.components.widget.NavigationButton;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegisterBundleKey;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.ui.register.email.check_account.EmailCheckAccountFragment;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;
import com.google.android.apps.miyagi.development.utils.InputValidator;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.rengwuxian.materialedittext.MaterialEditText;
import retrofit2.HttpException;

import javax.inject.Inject;

public class EmailEnterPasswordFragment extends RegistrationFragment<EmailEnterPasswordContract.Presenter> implements EmailEnterPasswordContract.View {

    @Inject ConfigStorage mConfigStorage;
    @Inject EmailEnterPasswordPresenter mPresenter;

    @BindView(R.id.email_address_input) MaterialEditText mEmailInput;
    @BindView(R.id.email_password_input) MaterialEditText mPasswordInput;
    @BindView(R.id.link_problem_signing_in) TextView mProblemLink;

    @BindView(R.id.bottom_navigation_container) View mBottomNav;
    @BindView(R.id.button_prev) NavigationButton mButtonPrev;
    @BindView(R.id.button_next) NavigationButton mButtonNext;

    private EnterPasswordLabels mLabels;
    private String mEmail;

    private Unbinder mUnbinder;

    public static EmailEnterPasswordFragment newInstance() {
        return new EmailEnterPasswordFragment();
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_email_enter_password_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUi();
        bindData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!ViewUtils.isTablet(getContext())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setupUi() {
        setupActionBar();

        mNavigationCallback.setErrorScreenOnActionClickListener(this::onButtonErrorClick);

        mBottomNav.setBackgroundColor(mConfigStorage.getLabels().getColors().getMainBackgroundColor());
        mButtonNext.setOnClickListener(v -> onNextButtonClicked());
        mButtonPrev.setVisibility(View.VISIBLE);
        mButtonPrev.setOnClickListener(v -> mNavigationCallback.goBack());

        mEmailInput.setEnabled(false);

        mPasswordInput.setOnEditorActionListener(new DoneClicked());
        mPasswordInput.setVisibility(View.VISIBLE);

        mPasswordInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onNextButtonClicked();
            }
            return false;
        });
    }

    private void bindData() {
        String toolbarTitle = mConfigStorage.getLabels().getCheckAccountLabels().getToolbarTitle();
        ((RegisterActivity) getActivity()).setTitle(toolbarTitle);

        mLabels = mConfigStorage.getLabels().getEnterPasswordLabels();
        mButtonNext.setText(mLabels.getButtonNext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            mEmail = bundle.getString(EmailCheckAccountFragment.BUNDLE_EMAIL);
            mEmailInput.setHint(mLabels.getEmailHint());
            mEmailInput.setFloatingLabelText(mLabels.getEmailHint());
            mEmailInput.setText(mEmail);
        }

        mPasswordInput.setHint(mLabels.getPassword());
        mPasswordInput.setFloatingLabelText(mLabels.getPassword());

        mProblemLink.setText(HtmlHelper.fromHtml(mLabels.getProblemLinkText()));
        mProblemLink.setOnClickListener(view -> {
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.EMAIL_RESET_PASSWORD, RegisterBundleKey.EMAIL, mEmail);
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
        signInWithEmail();
    }

    private void signInWithEmail() {
        KeyboardHelper.hideKeyboard(getActivity());
        String password = mPasswordInput.getText().toString();
        String email = mEmailInput.getText().toString();
        if (validateInputs(email, password)) {
            getPresenter().signInWithEmail(email, password);
        }
    }

    private boolean validateInputs(String email, String password) {
        if (!InputValidator.isValidPassword(password)) {
            mPasswordInput.setError(mLabels.getValidationEmptyPassword());
        } else {
            mPasswordInput.setError(null);
        }

        return InputValidator.isValidPassword(password) && !TextUtils.isEmpty(email);
    }

    @Override
    public void onLoginProcessCompletedSuccessful(NextStepResponse nextStepResponse) {
        Lh.v("onLoginProcessCompletedSuccessful");
        NextStepResponseData responseData = nextStepResponse.getResponseData();
        switch (responseData.getNextStep()) {
            case NextStepResponseData.NextStep.DASHBOARD:
                mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.DASHBOARD);
                break;
            case NextStepResponseData.NextStep.DIAGNOSTICS:
                mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.DIAGNOSTICS);
                break;
            case NextStepResponseData.NextStep.COMPLETE_PROFILE:
                mNavigationCallback.showLoaderScreen(false);
                mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.COMPLETE_PROFILE);
                return;
            default:
                Lh.e(this, "onLoginProcessCompletedSuccessful - Unknown next_status");
        }
    }

    @Override
    public void onLoginProcessFailure(Throwable throwable) {
        mNavigationCallback.showLoaderScreen(false);
        mNavigationCallback.setErrorScreenText(throwable);

        // https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.html#signInWithEmailAndPassword(java.lang.String, java.lang.String)
        if (throwable instanceof FirebaseException) {
            FirebaseMessagesLabels labels = mConfigStorage.getLabels().getFirebaseMessagesLabels();
            if (throwable instanceof FirebaseAuthInvalidCredentialsException) {
                //  thrown if the user account corresponding to email does not exist or has been disabled
                mPasswordInput.setError(labels.getInvalidPassword());
            } else if (throwable instanceof FirebaseAuthInvalidUserException) {
                // thrown if the password is wrong
                mPasswordInput.setError(labels.getInvalidPassword());
            } else if (throwable instanceof FirebaseTooManyRequestsException) {
                mPasswordInput.setError(labels.getTooManyAttempts());
            } else if (throwable instanceof FirebaseNetworkException) {
                mNavigationCallback.setErrorScreenOnActionClickListener(this::signInWithEmail);
                mNavigationCallback.setErrorScreenText(throwable);
            } else {
                mNavigationCallback.setErrorScreenMessage(labels.getInternalError());
                mNavigationCallback.showErrorScreen(true);
            }
        } else {
            if (throwable instanceof RetrofitException) {
                RetrofitException exception = (RetrofitException) throwable;
                if (exception.getKind() == RetrofitException.Kind.INVALID_TOKEN) {
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
                } else if (exception.getKind() == RetrofitException.Kind.NETWORK) {
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::signInWithEmail);
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
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }

    @Override
    public EmailEnterPasswordPresenter getPresenter() {
        return mPresenter;
    }
}
