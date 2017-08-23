package com.google.android.apps.miyagi.development.utils;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RetrofitException;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.Lazy;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 03.01.2017.
 */
public class RetryWithTokenRefresh {

    @Inject Lazy<ConfigStorage> mConfigStorage;

    /**
     * Instantiates a new Retry with token refresh.
     */
    public RetryWithTokenRefresh() {
        GoogleApplication.getInstance().getAppComponent().inject(this);
    }

    /**
     * Try refresh token and retry last observable.
     *
     * @param <T>         the type parameter.
     * @param toBeResumed the Observable to be resumed.
     * @return Refresh token and retry Func1.
     */
    public <T> Func1<Throwable, ? extends Observable<? extends T>> refreshTokenAndRetry(final Observable<T> toBeResumed) {
        return (Func1<Throwable, Observable<? extends T>>) throwable -> {
            if (isInvalidTokenError(throwable)) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    return RxFirebaseAuth.getToken(currentUser, true)
                            .map(getTokenResult -> {
                                mConfigStorage.get().saveLoginToken(getTokenResult.getToken());
                                return getTokenResult;
                            })
                            .flatMap(o -> toBeResumed)
                            .onErrorResumeNext(throwable1 -> {
                                return Observable.error(RetrofitException.unauthorizedError(throwable1));
                            });
                } else {
                    return Observable.error(RetrofitException.unauthorizedError(throwable));
                }
            }
            // re-throw this error because it's not recoverable from here
            return Observable.error(throwable);
        };
    }

    private boolean isInvalidTokenError(Throwable throwable) {
        if (throwable instanceof RetrofitException) {
            if (((RetrofitException) throwable).getKind() == RetrofitException.Kind.INVALID_TOKEN) {
                return true;
            }
        }
        return false;
    }
}
