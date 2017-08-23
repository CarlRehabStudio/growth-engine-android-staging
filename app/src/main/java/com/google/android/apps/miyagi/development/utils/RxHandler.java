package com.google.android.apps.miyagi.development.utils;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import rx.Subscriber;

/**
 * Created by lukaszweglinski on 09.11.2016.
 */

class RxHandler<T> implements OnSuccessListener<T>, OnFailureListener, OnCompleteListener<T> {

    private final Subscriber<? super T> mSubscriber;

    private RxHandler(Subscriber<? super T> observer) {
        this.mSubscriber = observer;
    }

    /**
     *
     * @param subscriber - subscriber object.
     * @param task - Firebase task.
     * @param <T> - type.
     */
    static <T> void assignOnTask(Subscriber<? super T> subscriber, Task<T> task) {
        RxHandler<T> handler = new RxHandler<>(subscriber);
        task.addOnSuccessListener(handler);
        task.addOnFailureListener(handler);
        try {
            task.addOnCompleteListener(handler);
        } catch (Throwable throwable) {
            // ignore
        }
    }

    @Override
    public void onSuccess(T res) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onNext(res);
        }
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onCompleted();
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onError(e);
        }
    }
}