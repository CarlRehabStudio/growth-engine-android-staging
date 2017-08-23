package com.google.android.apps.miyagi.development.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.dagger.ApplicationComponent;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by marcinarciszew on 18.11.2016.
 */

/**
 * Base Activity class for every Activity in this application.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView<T> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onScreenEnter();
        injectSelf(this);

        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onCreate(this);
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(TypekitContextWrapper.wrap(context));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onAttachView(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onDetachView();
        }
    }

    @Override
    protected void onDestroy() {
        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onDestroy();
        }
        super.onDestroy();
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((GoogleApplication) getApplication()).getAppComponent();
    }

    protected void showInfoSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, getResources().getInteger(R.integer.snackbar_duration));
        View snackBarView = snackbar.getView();
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(5);
        snackbar.show();
    }

    @Override
    public void onScreenEnter() {

    }

    @Override
    public void onScreenExit() {

    }

    @Override
    public T getPresenter() {
        return null;
    }
}
