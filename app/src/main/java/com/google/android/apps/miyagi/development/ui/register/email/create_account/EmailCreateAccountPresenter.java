package com.google.android.apps.miyagi.development.ui.register.email.create_account;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.validator.NextStepResponseValidator;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.RegisterUserResponse;
import com.google.android.apps.miyagi.development.data.net.responses.register.user.validator.RegisterUserValidator;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataService;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.net.services.RegisterUserService;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.utils.RxFirebaseAuth;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.auth.FirebaseAuth;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 10.04.2017.
 */
public class EmailCreateAccountPresenter extends BasePresenterImpl<EmailCreateAccountContract.View> implements EmailCreateAccountContract.Presenter {

    @Inject CommonDataService mCommonDataService;
    @Inject RegisterUserService mRegisterUserService;
    @Inject NextStepService mNextStepService;
    @Inject ConfigStorage mConfigStorage;

    private Observable<RegisterUserResponse> mCreateAccountObservable;
    private Subscription mCreateAccountSubscription;
    private boolean isDuringRequest;

    public EmailCreateAccountPresenter() {

    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        Market selectedMarket = mConfigStorage.getSelectedMarket();
        String endpointUrl = selectedMarket.getEndpointUrl();
        mCommonDataService.changeServiceUrl(endpointUrl);
        mRegisterUserService.changeServiceUrl(endpointUrl);
    }

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void onAttachView(@NonNull EmailCreateAccountContract.View view) {
        super.onAttachView(view);
        if (isDuringRequest) {
            onSubscribe();
            SubscriptionHelper.unsubscribe(mCreateAccountSubscription);
            mCreateAccountSubscription = mCreateAccountObservable.subscribe(this::onCreateAccountProcessSuccessful, this::onCreateAccountProcessFailure);
        }
    }

    @Override
    public void createAccount(String email, String password, String firstName, String lastName, boolean isEmailNotification) {
        isDuringRequest = true;

        SubscriptionHelper.unsubscribe(mCreateAccountSubscription);
        mCreateAccountObservable = RxFirebaseAuth.createUserWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                .flatMap(authResult -> RxFirebaseAuth.getToken(authResult.getUser(), false)
                        .map(tokenResult -> {
                            mConfigStorage.saveLoginToken(tokenResult.getToken());
                            return tokenResult;
                        }))
                .flatMap(getTokenResult -> mNextStepService.getNextStepData()
                        .map(NextStepResponseValidator::validate)
                        .flatMap(nextStepResponse -> mRegisterUserService.registerUser(firstName, lastName, isEmailNotification, nextStepResponse.getResponseData().getXsrfToken())
                                .map(RegisterUserValidator::validate)
                                .subscribeOn(Schedulers.io())))
                .doOnSubscribe(this::onSubscribe)
                .cache();

        mCreateAccountSubscription = mCreateAccountObservable.subscribe(this::onCreateAccountProcessSuccessful, this::onCreateAccountProcessFailure);
    }

    @Override
    public void onCreateAccountProcessSuccessful(RegisterUserResponse registerUserResponse) {
        isDuringRequest = false;
        if (getView() != null) {
            getView().onCreateAccountProcessSuccessful(registerUserResponse);
        }
    }

    @Override
    public void onCreateAccountProcessFailure(Throwable throwable) {
        isDuringRequest = false;
        if (getView() != null) {
            getView().onCreateAccountProcessFailure(throwable);
        }
    }

    private void onSubscribe() {
        if (getView() != null) {
            getView().onSubscribe();
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        SubscriptionHelper.unsubscribe(mCreateAccountSubscription);
        mCreateAccountSubscription = null;
    }

    @Override
    public void onDestroy() {

    }
}
