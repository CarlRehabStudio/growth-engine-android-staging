package com.google.android.apps.miyagi.development.data.net.services.errorhandling;

import com.google.android.apps.miyagi.development.utils.RetryWithTokenRefresh;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lukaszweglinski on 03.01.2017.
 */
public class RefreshTokenTransformer {

    /**
     * Handle refresh token and retry on error.
     *
     * @param <T> the type parameter.
     * @return Transformer with handle refresh token and retry on error.
     */
    public <T> Observable.Transformer<T, T> refreshTokenOnError() {
        return observable -> {
            Observable<T> ob = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            return ob.onErrorResumeNext(new RetryWithTokenRefresh().refreshTokenAndRetry(ob));
        };
    }
}
