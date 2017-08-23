package com.google.android.apps.miyagi.development.helpers;

import android.view.View;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;

import java.util.concurrent.TimeUnit;

/**
 * A wrapper that simplifies work with preloader view.
 */
public class PreloaderHelper {

    public static final int LOADER_DELAY = 500;

    private final View mPreloaderView;
    private final TextView mMainMessageTv;
    private final TextView mSecondaryMessageTv;

    /**
     * PreloaderHelper constructor.
     *
     * @param preloaderView - view to wrap.
     */
    public PreloaderHelper(View preloaderView) {
        mPreloaderView = preloaderView;
        mMainMessageTv = (TextView) mPreloaderView.findViewById(R.id.fullscreen_preloader_main_message);
        mSecondaryMessageTv = (TextView) mPreloaderView.findViewById(R.id.fullscreen_preloader_secondary_message);
        //capture all clicks
        preloaderView.setOnClickListener(view -> {
            // do nothing
        });
        hideWithoutDelay();
    }

    public void setMainMessage(String mainMessage) {
        mMainMessageTv.setText(mainMessage);
    }

    public void setMainMessage(int mainMessageStringId) {
        mMainMessageTv.setText(mainMessageStringId);
    }

    public void setSecondaryMessage(String secondaryMessage) {
        mSecondaryMessageTv.setText(secondaryMessage);
    }

    public void setSecondaryMessage(int secondaryMessageStringId) {
        mSecondaryMessageTv.setText(secondaryMessageStringId);
    }

    public void setBackgroundColor(int backgroundColor) {
        mPreloaderView.setBackgroundColor(backgroundColor);
    }

    public void show() {
        mPreloaderView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides loader with standard delay.
     */
    public void hide() {
        Observable.timer(LOADER_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> mPreloaderView.setVisibility(View.GONE),
                        throwable -> Actions.empty());
    }

    public void hideWithoutDelay() {
        mPreloaderView.setVisibility(View.GONE);
    }
}
