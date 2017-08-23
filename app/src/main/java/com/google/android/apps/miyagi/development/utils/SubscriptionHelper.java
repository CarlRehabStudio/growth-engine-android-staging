package com.google.android.apps.miyagi.development.utils;

import android.support.annotation.Nullable;

import rx.Subscription;

/**
 * Created by jerzyw on 11.10.2016.
 */

public class SubscriptionHelper {

    /**
     * Small helper method to unsubscribe subscription to Observable.
     * @param subscription - subscription to Observable.
     * @return true if subscription wasn't null and wasn't unsubscribed.
     */
    public static boolean unsubscribe(@Nullable Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            return true;
        } else {
            return false;
        }
    }
}
