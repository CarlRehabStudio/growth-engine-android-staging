package com.google.android.apps.miyagi.development.helpers.realm;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lukaszweglinski on 06.02.2017.
 */

public abstract class OnSubscribeRealm<T> implements Observable.OnSubscribe<T> {

    private final List<Subscriber<? super T>> mSubscribers = new ArrayList<>();
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final Object mLock = new Object();

    public OnSubscribeRealm() {
    }

    @Override
    public void call(final Subscriber<? super T> subscriber) {
        synchronized (mLock) {
            boolean canceled = this.mCancelled.get();
            if (!canceled && !mSubscribers.isEmpty()) {
                subscriber.add(newUnsubscribeAction(subscriber));
                mSubscribers.add(subscriber);
                return;
            } else if (canceled) {
                return;
            }
        }
        subscriber.add(newUnsubscribeAction(subscriber));
        mSubscribers.add(subscriber);

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded();
        Realm realm = Realm.getInstance(builder.build());
        boolean withError = false;

        T object = null;
        try {
            if (!this.mCancelled.get()) {
                realm.beginTransaction();
                object = get(realm);
                if (object != null && !this.mCancelled.get()) {
                    realm.commitTransaction();
                } else {
                    realm.cancelTransaction();
                }
            }
        } catch (RuntimeException exception) {
            realm.cancelTransaction();
            sendOnError(new RealmException("Error during transaction.", exception));
            withError = true;
        } catch (Error error) {
            realm.cancelTransaction();
            sendOnError(error);
            withError = true;
        }
        if (!this.mCancelled.get() && !withError) {
            sendOnNext(object);
        }

        try {
            realm.close();
        } catch (RealmException ex) {
            sendOnError(ex);
            withError = true;
        }
        if (!withError) {
            sendOnCompleted();
        }
        this.mCancelled.set(false);
    }

    private void sendOnNext(T object) {
        for (int i = 0; i < mSubscribers.size(); i++) {
            Subscriber<? super T> subscriber = mSubscribers.get(i);
            subscriber.onNext(object);
        }
    }

    private void sendOnError(Throwable e) {
        for (int i = 0; i < mSubscribers.size(); i++) {
            Subscriber<? super T> subscriber = mSubscribers.get(i);
            subscriber.onError(e);
        }
    }

    private void sendOnCompleted() {
        for (int i = 0; i < mSubscribers.size(); i++) {
            Subscriber<? super T> subscriber = mSubscribers.get(i);
            subscriber.onCompleted();
        }
    }

    @NonNull
    private Subscription newUnsubscribeAction(final Subscriber<? super T> subscriber) {
        return Subscriptions.create(() -> {
            synchronized (mLock) {
                mSubscribers.remove(subscriber);
                if (mSubscribers.isEmpty()) {
                    mCancelled.set(true);
                }
            }
        });
    }

    public abstract T get(Realm realm);
}