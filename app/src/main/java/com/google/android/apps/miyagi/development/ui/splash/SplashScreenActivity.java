package com.google.android.apps.miyagi.development.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponseData;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.BaseActivity;
import com.google.android.apps.miyagi.development.ui.dashboard.DashboardActivity;
import com.google.android.apps.miyagi.development.ui.register.RegisterActivity;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public class SplashScreenActivity extends BaseActivity {

    @Inject
    protected ConfigStorage mConfigStorage;
    private int mSplashScreenDelay;
    private Subscription mSplashSubscription;


    /**
     * Creates new instance of SplashScreenActivity.
     */
    public static Intent createIntent(Context context, boolean clearHistory) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        if (clearHistory) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        mSplashScreenDelay = getResources().getInteger(R.integer.splash_screen_delay_ms);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!ViewUtils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SubscriptionHelper.unsubscribe(mSplashSubscription);
        mSplashSubscription = Observable.timer(mSplashScreenDelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    String loginToken = mConfigStorage.readLoginToken();
                    CommonDataResponseData commonDataResponseData = mConfigStorage.getCommonData();
                    if (commonDataResponseData != null && loginToken != null) {
                        if ((commonDataResponseData.getMenu() != null) && commonDataResponseData.getMenu().isUsesSystemFont()) {
                            GoogleApplication.getInstance().setupSystemFonts();
                        }
                        startDashboardActivity();
                    } else {
                        startRegistrationActivity();
                    }
                    finish();
                }, throwable -> Actions.empty());
    }

    private void startDashboardActivity() {
        Intent intent = DashboardActivity.createIntent(this, false);
        startActivity(intent);
        finish();
    }

    private void startRegistrationActivity() {
        Intent intent = RegisterActivity.createIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        SubscriptionHelper.unsubscribe(mSplashSubscription);
        super.onPause();
    }
}
