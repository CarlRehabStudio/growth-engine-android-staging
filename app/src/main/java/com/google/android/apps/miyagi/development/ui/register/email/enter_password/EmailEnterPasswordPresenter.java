package com.google.android.apps.miyagi.development.ui.register.email.enter_password;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.NextStepResponse;
import com.google.android.apps.miyagi.development.data.net.responses.next.step.validator.NextStepResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.utils.RxFirebaseAuth;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.auth.FirebaseAuth;

import rx.Observable;
import rx.Subscription;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 10.04.2017.
 */
public class EmailEnterPasswordPresenter extends BasePresenterImpl<EmailEnterPasswordContract.View> implements EmailEnterPasswordContract.Presenter {

    @Inject ConfigStorage mConfigStorage;
    @Inject NextStepService mNextStepService;

    private Observable<NextStepResponse> mSignInObservable;
    private Subscription mTokenSubscription;
    private boolean isDuringRequest;

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void onAttachView(@NonNull EmailEnterPasswordContract.View view) {
        super.onAttachView(view);
        if (isDuringRequest) {
            onSubscribe();
            SubscriptionHelper.unsubscribe(mTokenSubscription);
            mTokenSubscription = mSignInObservable.subscribe(this::onLoginProcessCompletedSuccessful, this::onLoginProcessFailure);
        }
    }

    public void signInWithEmail(String email, String password) {
        isDuringRequest = true;

        SubscriptionHelper.unsubscribe(mTokenSubscription);
        mSignInObservable = RxFirebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                //get token
                .flatMap(authResult -> RxFirebaseAuth.getToken(authResult.getUser(), true)
                        .map(tokenResult -> {
                            mConfigStorage.saveLoginToken(tokenResult.getToken());
                            return tokenResult;
                        }))
                //get next step
                .flatMap(getTokenResult -> {
                    //getNextStep api call
                    return mNextStepService.getNextStepData()
                            .map(NextStepResponseValidator::validate);
                })
                .doOnSubscribe(this::onSubscribe)
                .cache();

        mTokenSubscription = mSignInObservable.subscribe(this::onLoginProcessCompletedSuccessful, this::onLoginProcessFailure);
    }

    @Override
    public void onLoginProcessFailure(Throwable throwable) {
        isDuringRequest = false;
        if (getView() != null) {
            getView().onLoginProcessFailure(throwable);
        }
    }

    @Override
    public void onLoginProcessCompletedSuccessful(NextStepResponse nextStepResponse) {
        isDuringRequest = false;
        if (getView() != null) {
            getView().onLoginProcessCompletedSuccessful(nextStepResponse);
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
        SubscriptionHelper.unsubscribe(mTokenSubscription);
        mTokenSubscription = null;
    }

    @Override
    public void onDestroy() {

    }
}
