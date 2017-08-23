package com.google.android.apps.miyagi.development.ui.register.signin;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.dagger.ViewScope;
import com.google.android.apps.miyagi.development.data.error.DeletedAccountException;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.validator.NextStepResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataService;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.net.services.RegisterUserService;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ErrorScreenHelper;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.ui.register.common.FirebaseProviders;
import com.google.android.apps.miyagi.development.ui.register.common.HttpStatusCode;
import com.google.android.apps.miyagi.development.ui.register.common.NavigationCallback;
import com.google.android.apps.miyagi.development.utils.Lh;
import com.google.android.apps.miyagi.development.utils.RxFirebaseAuth;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
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
import com.google.firebase.auth.ProviderQueryResult;

import retrofit2.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Paweł on 2017-02-24.
 */
@ViewScope
public class SignInPresenter extends BasePresenterImpl<SignInContract.View> implements SignInContract.Presenter {

    private static final String BUNDLE_EMAIL = "EMAIL";

    @Inject RegisterUserService mRegisterUserService;
    @Inject CommonDataService mCommonDataService;
    @Inject ConfigStorage mConfigStorage;
    @Inject NextStepService mNextStepService;

    private boolean mIsSingleMarket;
    private NavigationCallback mNavigationCallback;
    private Subscription mSubscription;
    private FirebaseAuth mAuth;
    private String mGoogleToken;
    private String mGoogleAuthCode;
    private String mEmail;
    private NextStepResponseData mFirebaseResponse;

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);

        getNavigationCallback(context);

        Market selectedMarket = mConfigStorage.getSelectedMarket();
        if (selectedMarket != null) {
            String endpointUrl = selectedMarket.getEndpointUrl();
            mCommonDataService.changeServiceUrl(endpointUrl);
            mRegisterUserService.changeServiceUrl(endpointUrl);
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            mNavigationCallback.setErrorScreenOnActionClickListener(this::onGoogleLoginSelected);
        } else {
            goToStep(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
        }
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void onAttachView(@NonNull SignInContract.View view) {
        super.onAttachView(view);
        view.setUpUi(mConfigStorage.getLabels().getColors().getMainCtaColor(), mConfigStorage.getLabels().getColors().getMainBackgroundColor());
        view.setUpActionBar(!mIsSingleMarket);
        view.bindData(mConfigStorage.getLabels().getSignInSelectionLabels());

        if (mSubscription != null) {
            onSubscribe();
        } else if (mFirebaseResponse != null) {
            onGoogleLoginProcessCompletedSuccessful();
        } else {
            mNavigationCallback.showLoaderScreen(false);
        }
    }

    @Override
    public void setSingleMarket(boolean singleMarket) {
        mIsSingleMarket = singleMarket;
    }

    private void getNavigationCallback(Context context) {
        try {
            mNavigationCallback = (NavigationCallback) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement NavigationCallback");
        }
    }

    @Override
    public void onGoogleLoginSelected() {
        if (getView() != null) {
            getView().signInWithGoogle();
        }
    }

    @Override
    public void onEmailLoginSelected() {
        goToStep(NavigationCallback.RegistrationSteps.EMAIL_CHECK_ACCOUNT_EXISTS);
    }

    @Override
    public void onGoogleLoggedIn(String token, String authCode, String email, boolean isOnline) {
        mGoogleToken = token;
        mGoogleAuthCode = authCode;
        mEmail = email;

        if (isOnline) {
            checkEmailProviders();
        }
    }

    private void checkEmailProviders() {
        SubscriptionHelper.unsubscribe(mSubscription);
        mSubscription = RxFirebaseAuth.fetchProvidersForEmail(FirebaseAuth.getInstance(), mEmail)
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .subscribe(providerQueryResult -> onCheckAccountReceived(mEmail, providerQueryResult), this::onCheckAccountFailure);
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

    private void onTerminate() {
        mSubscription = null;
    }

    private void onGoogleLoginProcessCompletedSuccessful(NextStepResponse nextStepResponse) {
        mFirebaseResponse = nextStepResponse.getResponseData();
        onGoogleLoginProcessCompletedSuccessful();
    }

    private void onGoogleLoginProcessCompletedSuccessful() {
        if (getView() != null) {
            switch (mFirebaseResponse.getNextStep()) {
                case NextStepResponseData.NextStep.DASHBOARD:
                    goToStep(NavigationCallback.RegistrationSteps.DASHBOARD);
                    break;
                case NextStepResponseData.NextStep.DIAGNOSTICS:
                    goToStep(NavigationCallback.RegistrationSteps.DIAGNOSTICS);
                    break;
                case NextStepResponseData.NextStep.COMPLETE_PROFILE:
                    goToStep(NavigationCallback.RegistrationSteps.COMPLETE_PROFILE);
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
                        goToStep(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
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
                        goToStep(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
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
        goToStep(NavigationCallback.RegistrationSteps.MARKET_SELECTION);
    }

    private void goToStep(NavigationCallback.RegistrationSteps step) {
        if (getView() != null) {
            mNavigationCallback.goToStepRqst(step);
            getView().onScreenExit();
        }
    }

    private void onCheckAccountReceived(String email, ProviderQueryResult providerQueryResult) {
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

        if (hasGoogleProvider) {
            firebaseAuthWithGoogle();
        } else if (hasEmailProvider) {
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.SIGN_IN_WITH_EMAIL, BUNDLE_EMAIL, email);
            mNavigationCallback.showLoaderScreen(false);
            getView().onScreenExit();
        } else {
            // create local account
            mNavigationCallback.goToStepRqst(NavigationCallback.RegistrationSteps.EMAIL_CREATE_ACCOUNT, BUNDLE_EMAIL, email);
            mNavigationCallback.showLoaderScreen(false);
            getView().onScreenExit();
        }
    }

    private void onCheckAccountFailure(Throwable throwable) {
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
                mNavigationCallback.setErrorScreenOnActionClickListener(this::checkEmailProviders);
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
                    mNavigationCallback.setErrorScreenOnActionClickListener(this::checkEmailProviders);
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
    public void onDestroy() {
        mNavigationCallback.setErrorScreenOnActionClickListener(ErrorScreenHelper.OnActionListener.EMPTY);
    }
}
