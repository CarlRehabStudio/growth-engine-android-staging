package com.google.android.apps.miyagi.development.ui.register.signin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.models.labels.SignInSelectionLabels;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.validator.NextStepResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.helpers.ScreenAnimationHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.ui.register.common.FirebaseProviders;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.ui.register.common.RegisterBundleKey;
import com.google.android.apps.miyagi.development.ui.register.common.RegistrationFragment;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.RxFirebaseAuth;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

/**
 * Created by marcinarciszew on 20.03.2017.
 */

public class SignInWithProviderFragment extends RegistrationFragment {

    private static final int SIGN_IN_REQUEST = 1;

    @Inject ConfigStorage mConfigStorage;
    @Inject ScreenAnimationHelper mScreenAnimationHelper;
    @Inject NextStepService mNextStepService;
    @Inject AnalyticsHelper mAnalyticsHelper;

    @BindView(R.id.image_logo) ImageView mImageLogo;
    @BindView(R.id.label_message) TextView mLabelMessage;
    @BindView(R.id.button_sign_in_with_provider) AppCompatButton mButtonSignInWithProvider;

    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private Subscription mSubscription;
    private FirebaseAuth mAuth;
    private String mGoogleToken;
    private String mGoogleAuthCode;
    private String mEmail;
    private String mFirebaseProvider;
    private NextStepResponseData mFirebaseResponse;

    public static SignInWithProviderFragment newInstance() {
        SignInWithProviderFragment fragment = new SignInWithProviderFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_sign_in_with_provider, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        createGoogleApiClient();

        setupUi();
        bindData();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ViewUtils.isSmallDevice(getContext()) && !ViewUtils.isTablet(getContext())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, wait for onResume and authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                onGoogleLoggedIn(account.getIdToken(), account.getServerAuthCode(), account.getEmail());
            } else {
                // TODO Google Sign In failed, update UI appropriately
                Lh.e(this, "Google sign in failed.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onGoogleLoggedIn(String token, String authCode, String email) {
        mGoogleToken = token;
        mGoogleAuthCode = authCode;
        mEmail = email;

        firebaseAuthWithGoogle();
    }

    private void setupUi() {
        setupActionBar();
    }

    private void bindData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mEmail = bundle.getString(RegisterBundleKey.EMAIL);
            mFirebaseProvider = bundle.getString(RegisterBundleKey.PROVIDER);
        }

        SignInSelectionLabels labels = mConfigStorage.getLabels().getSignInSelectionLabels();

        if (mFirebaseProvider.equals(FirebaseProviders.GOOGLE)) {
            int googleColor = mConfigStorage.getLabels().getColors().getMainCtaColor();
            ColorHelper.tintButtonBackground(mButtonSignInWithProvider, googleColor);
            mButtonSignInWithProvider.setText(labels.getSignInGoogle());
            mButtonSignInWithProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_g, 0, 0, 0);
            mButtonSignInWithProvider.setOnClickListener(this::onLoginButtonClick);

            mLabelMessage.setText(formatMessage(labels.getSignInGoogleUsed(), mEmail));

            mNavigationCallback.setErrorScreenOnActionClickListener(this::signInWithGoogle);
        } else if (mFirebaseProvider.equals(FirebaseProviders.EMAIL)) {
            int emailColor = mConfigStorage.getLabels().getColors().getMainBackgroundColor();
            ColorHelper.tintButtonBackground(mButtonSignInWithProvider, emailColor);
            mButtonSignInWithProvider.setText(labels.getSignInEmail());
            mButtonSignInWithProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_mail, 0, 0, 0);
            mButtonSignInWithProvider.setOnClickListener(this::onLoginButtonClick);

            mLabelMessage.setText(formatMessage(labels.getSignInEmailUsed(), mEmail));
        }

        animateScreen();
    }

    private String formatMessage(String pattern, String email) {
        return pattern.replace("$1", email);
    }

    private void animateScreen() {
        mScreenAnimationHelper.addFlatView(mImageLogo);
        mScreenAnimationHelper.addFlatView(mLabelMessage);

        mScreenAnimationHelper.addCtaView(mButtonSignInWithProvider);

        mScreenAnimationHelper.animateScreen();
    }

    private void onLoginButtonClick(View v) {
        if (FirebaseProviders.GOOGLE.equals(mFirebaseProvider)) {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_registration_account), getString(R.string.event_category_registration), getString(R.string.event_action_select_option), mButtonSignInWithProvider.getText().toString());
            signInWithGoogle();
        } else if (FirebaseProviders.EMAIL.equals(mFirebaseProvider)) {
            mAnalyticsHelper.trackEvent(getString(R.string.screen_registration_account), getString(R.string.event_category_registration), getString(R.string.event_action_select_option), mButtonSignInWithProvider.getText().toString());
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.EMAIL_ENTER_PASSWORD, RegisterBundleKey.EMAIL, mEmail);
            onScreenExit();
        }
    }

    private void signInWithGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_REQUEST);
    }

    private void setupActionBar() {
        RegisterActivity activity = (RegisterActivity) getActivity();
        activity.setTitle(null);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void createGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        //FIXME
                        Lh.e(this, "Google API onConnectionFailed.");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle() {
        // http://stackoverflow.com/questions/34685928/token-null-sign-in-google-account
        AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleToken, mGoogleAuthCode);
        // sign in to firebase
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = RxFirebaseAuth.signInWithCredential(mAuth, credential)
                .flatMap(new Func1<AuthResult, Observable<GetTokenResult>>() {
                    @Override
                    public Observable<GetTokenResult> call(AuthResult authResult) {
                        return RxFirebaseAuth.getToken(authResult.getUser(), true);
                    }
                })
                .map(tokenResult -> {
                    mConfigStorage.saveLoginToken(tokenResult.getToken());
                    return tokenResult;
                })
                // get next step
                .flatMap(new Func1<GetTokenResult, Observable<NextStepResponse>>() {
                    @Override
                    public Observable<NextStepResponse> call(GetTokenResult getTokenResult) {
                        // getNextStep api call
                        return mNextStepService.getNextStepData()
                                .map(NextStepResponseValidator::validate);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::onSubscribe)
                .subscribe(this::onGoogleLoginProcessCompletedSuccessful, this::onGoogleLoginProcessFailure);
    }

    private void onSubscribe() {
        mNavigationCallback.showLoaderScreen(true);
        mNavigationCallback.showErrorScreen(false);
    }

    private void onGoogleLoginProcessCompletedSuccessful(NextStepResponse nextStepResponse) {
        mFirebaseResponse = nextStepResponse.getResponseData();
        onGoogleLoginProcessCompletedSuccessful();
    }

    private void onGoogleLoginProcessCompletedSuccessful() {
        if (getView() != null) {
            switch (mFirebaseResponse.getNextStep()) {
                case NextStepResponseData.NextStep.DASHBOARD:
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.DASHBOARD);
                    break;
                case NextStepResponseData.NextStep.DIAGNOSTICS:
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.DIAGNOSTICS);
                    break;
                case NextStepResponseData.NextStep.COMPLETE_PROFILE:
                    mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.COMPLETE_PROFILE);
                    mNavigationCallback.showLoaderScreen(false);
                    break;
                default:
                    Lh.e(this, "onGoogleLoginProcessCompletedSuccessful - Unknown next_status");
                    break;
            }
        }
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = null;
    }

    private void onGoogleLoginProcessFailure(Throwable throwable) {
        if (getView() != null) {
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
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::firebaseAuthWithGoogle);
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
                        mNavigationCallback.setErrorScreenOnActionClickListener(this::firebaseAuthWithGoogle);
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
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::onDeletedAccount);
                    mNavigationCallback.showErrorScreen(true);
                } else {
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::firebaseAuthWithGoogle);
                    mNavigationCallback.setErrorScreenText(throwable);
                    mNavigationCallback.showErrorScreen(true);
                }
            }
        }
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = null;
    }

    private void onDeletedAccount() {
        mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
    }
}