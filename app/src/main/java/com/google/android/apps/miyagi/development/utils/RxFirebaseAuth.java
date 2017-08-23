package com.google.android.apps.miyagi.development.utils;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.ProviderQueryResult;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by lukaszweglinski on 09.11.2016.
 */

public class RxFirebaseAuth {

    /**
     * @param firebaseAuth - FirebaseAuth object.
     * @param email        - user email.
     * @param password     - password.
     *
     * @return - observable object.
     */
    @NonNull
    public static Observable<AuthResult> signInWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                    @NonNull final String email,
                                                                    @NonNull final String password) {
        return Observable.create(new Observable.OnSubscribe<AuthResult>() {
            @Override
            public void call(final Subscriber<? super AuthResult> subscriber) {
                RxHandler.assignOnTask(subscriber, firebaseAuth.signInWithEmailAndPassword(email, password));
            }
        });
    }

    /**
     * @param firebaseAuth - FirebaseAuth object.
     * @param email        - user email
     *
     * @return - observable object.
     */
    @NonNull
    public static Observable<ProviderQueryResult> fetchProvidersForEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                                         @NonNull final String email) {
        return Observable.create(new Observable.OnSubscribe<ProviderQueryResult>() {
            @Override
            public void call(final Subscriber<? super ProviderQueryResult> subscriber) {
                RxHandler.assignOnTask(subscriber, firebaseAuth.fetchProvidersForEmail(email));
            }
        });
    }

    /**
     * @param firebaseUser - FirebaseUser object.
     * @param forceRefresh - force refresh token.
     *
     * @return - observable object.
     */
    @NonNull
    public static Observable<GetTokenResult> getToken(@NonNull final FirebaseUser firebaseUser,
                                                      final boolean forceRefresh) {
        return Observable.create(new Observable.OnSubscribe<GetTokenResult>() {
            @Override
            public void call(final Subscriber<? super GetTokenResult> subscriber) {
                RxHandler.assignOnTask(subscriber, firebaseUser.getToken(forceRefresh));
            }
        });
    }

    /**
     * Creates user with given email and password.
     */
    public static Observable<AuthResult> createUserWithEmailAndPassword(
            @NonNull final FirebaseAuth firebaseAuth,
            @NonNull final String email,
            @NonNull final String password) {

        return Observable.create(new Observable.OnSubscribe<AuthResult>() {
            @Override
            public void call(Subscriber<? super AuthResult> subscriber) {
                RxHandler.assignOnTask(subscriber, firebaseAuth.createUserWithEmailAndPassword(email, password));
            }
        });
    }

    /**
     * Signs user in with auth credential.
     */
    public static Observable<AuthResult> signInWithCredential(FirebaseAuth firebaseAuth, AuthCredential credential) {
        return Observable.create(new Observable.OnSubscribe<AuthResult>() {
            @Override
            public void call(Subscriber<? super AuthResult> subscriber) {
                RxHandler.assignOnTask(subscriber, firebaseAuth.signInWithCredential(credential));
            }
        });
    }

    /**
     * Reset user password by sending reset email to user.
     */
    public static Observable<Void> resetPassword(FirebaseAuth firebaseAuth, String email) {
        return Observable.create((Observable.OnSubscribe<Void>) subscriber ->
                RxHandler.assignOnTask(subscriber, firebaseAuth.sendPasswordResetEmail(email))
        );
    }
}
