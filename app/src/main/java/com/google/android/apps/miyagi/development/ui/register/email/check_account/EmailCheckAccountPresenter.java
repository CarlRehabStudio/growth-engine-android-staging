package com.google.android.apps.miyagi.development.ui.register.email.check_account;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.services.NextStepService;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.BasePresenterImpl;
import com.google.android.apps.miyagi.development.utils.RxFirebaseAuth;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import rx.Observable;
import rx.Subscription;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 10.04.2017.
 */
public class EmailCheckAccountPresenter extends BasePresenterImpl<EmailCheckAccountContract.View> implements EmailCheckAccountContract.Presenter {

    @Inject ConfigStorage mConfigStorage;
    @Inject NextStepService mNextStepService;

    private Observable<ProviderQueryResult> mCheckAccountObservable;
    private Subscription mCheckAccountSubscription;
    private String mEmail;
    private boolean isDuringRequest;

    @Override
    public void injectSelf(Context context) {
        ((GoogleApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void onAttachView(@NonNull EmailCheckAccountContract.View view) {
        super.onAttachView(view);
        if (isDuringRequest) {
            onSubscribe();
            SubscriptionHelper.unsubscribe(mCheckAccountSubscription);
            mCheckAccountSubscription = mCheckAccountObservable.subscribe(providerQueryResult -> onCheckAccountReceived(mEmail, providerQueryResult), this::onCheckAccountFailure);
        }
    }

    @Override
    public void checkAccount(String email) {
        isDuringRequest = true;
        mEmail = email;

        SubscriptionHelper.unsubscribe(mCheckAccountSubscription);
        mCheckAccountObservable = RxFirebaseAuth.fetchProvidersForEmail(FirebaseAuth.getInstance(), email)
                .doOnSubscribe(this::onSubscribe)
                .doOnTerminate(this::onTerminate)
                .cache();

        mCheckAccountSubscription = mCheckAccountObservable.subscribe(providerQueryResult -> onCheckAccountReceived(mEmail, providerQueryResult), this::onCheckAccountFailure);
    }

    private void onTerminate() {
        if (getView() != null) {
            getView().onTerminate();
        }
    }

    private void onCheckAccountReceived(String email, ProviderQueryResult providerQueryResult) {
        isDuringRequest = false;
        if (getView() != null) {
            getView().onCheckAccountReceived(email, providerQueryResult);
        }
    }

    private void onCheckAccountFailure(Throwable throwable) {
        isDuringRequest = false;
        if (getView() != null) {
            getView().onCheckAccountFailure(throwable);
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
        SubscriptionHelper.unsubscribe(mCheckAccountSubscription);
        mCheckAccountSubscription = null;
    }

    @Override
    public void onDestroy() {

    }
}
