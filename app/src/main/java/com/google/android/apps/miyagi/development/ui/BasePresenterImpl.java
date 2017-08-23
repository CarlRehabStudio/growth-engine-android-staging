package com.google.android.apps.miyagi.development.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.apps.miyagi.development.helpers.ViewUtils;

import java.lang.ref.WeakReference;

/**
 * Created by marcin on 15.01.2017.
 */

public abstract class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {

    protected WeakReference<V> mView;
    protected boolean mIsTablet;

    public V getView() {
        return mView != null ? mView.get() : null;
    }

    public void onCreate(Context context) {
        injectSelf(context);
        mIsTablet = ViewUtils.isTablet(context);
    }

    public abstract void injectSelf(Context context);

    public void onAttachView(@NonNull V view) {
        mView = new WeakReference<V>(view);
    }

    public void onDetachView() {
        mView = null;
    }

    public abstract void onDestroy();
}