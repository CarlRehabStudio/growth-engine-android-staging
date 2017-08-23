package com.google.android.apps.miyagi.development.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Paweł on 2017-02-24.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView<T> {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onScreenEnter();
        injectSelf(getActivity());

        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onCreate(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onAttachView(this);
        }
    }

    @Override
    public void onPause() {
        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onDetachView();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getPresenter() != null) { //TODO remove when all child fragments will be changed to MVP
            getPresenter().onDestroy();
        }
    }

    @Override
    public void onScreenEnter() {

    }

    @Override
    public void onScreenExit() {

    }

    @Override
    public void injectSelf(Context context) {

    }

    @Override
    public T getPresenter() {
        return null;
    }
}
