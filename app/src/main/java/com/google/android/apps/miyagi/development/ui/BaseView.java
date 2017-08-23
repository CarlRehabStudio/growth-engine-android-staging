package com.google.android.apps.miyagi.development.ui;

import android.content.Context;

/**
 * Created by marcin on 15.01.2017.
 */

public interface BaseView<T extends BasePresenter> {

    /**
     * Called when view enters the screen.
     * TODO: create @ViewScope component
     */
    void onScreenEnter();

    /**
     * Called when view is no longer on the screen.
     * TODO: clean @ViewScope component
     */
    void onScreenExit();

    /**
     * Method forcing Dagger injection.
     */
    void injectSelf(Context context);

    /**
     * Gets view presenter.
     * @return view presenter.
     */
    T getPresenter();

}
