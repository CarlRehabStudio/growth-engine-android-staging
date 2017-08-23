package com.google.android.apps.miyagi.development.helpers.realm;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;

/**
 * Created by lukaszweglinski on 06.02.2017.
 */

public final class RealmObservable {
    private RealmObservable() {
    }

    public static <T> Observable<T> object(final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealm<T>() {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T> Observable<List<T>> list(final Func1<Realm, List<T>> function) {
        return Observable.create(new OnSubscribeRealm<List<T>>() {
            @Override
            public List<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }
}