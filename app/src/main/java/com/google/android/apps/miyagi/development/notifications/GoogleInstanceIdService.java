package com.google.android.apps.miyagi.development.notifications;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public class GoogleInstanceIdService extends FirebaseInstanceIdService {

    @Inject Lazy<ConfigStorage> mConfigStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        ((GoogleApplication) getApplication()).getAppComponent().inject(this);
    }

    @android.support.annotation.WorkerThread
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String previousToken = mConfigStorage.get().readPushToken();

        if (previousToken != null && refreshedToken != null) {
            if (!refreshedToken.equals(previousToken)) {
                mConfigStorage.get().savePushToken(refreshedToken);
                mConfigStorage.get().saveShouldUpdatePushToken(true);
            }
        }
    }
}