package com.google.android.apps.miyagi.development.ui;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by marcin on 15.01.2017.
 */

public interface BasePresenter<V> {

    BasePresenter NONE = new BasePresenter() {

        @Override
        public Object getView() {
            return null;
        }

        @Override
        public void onCreate(Context context) {
            // idle
        }

        @Override
        public void injectSelf(Context context) {
            // idle
        }

        @Override
        public void onAttachView(@NonNull Object view) {
            // idle
        }

        @Override
        public void onDetachView() {
            // idle
        }

        @Override
        public void onDestroy() {
            // idle
        }
    };

    /**
     * Returns view associated with this presenter.
     */
    V getView();

    /**
     * Callback called for the initialization, the view is currently not attached.
     */
    void onCreate(Context context);

    /**
     * Method forcing Dagger injection.
     */
    void injectSelf(Context context);

    /**
     * Callback called when the view was attached and is visible to the user.
     * TODO: use with onResume().
     */
    void onAttachView(@NonNull V view);

    /**
     * Callback called when the view will be detached after this call and
     * is not visible to the user anymore.
     * TODO: use with onPause().
     */
    void onDetachView();

    /**
     * Callback called when the view (activity / fragment) is completely destroyed
     * and will never return again. Stop all work and clean cached data.
     */
    void onDestroy();
}