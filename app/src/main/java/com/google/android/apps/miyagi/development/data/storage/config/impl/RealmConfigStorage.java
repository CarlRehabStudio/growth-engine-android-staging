package com.google.android.apps.miyagi.development.data.storage.config.impl;

import android.support.annotation.Nullable;
import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponseData;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by jerzyw on 07.11.2016.
 */

public class RealmConfigStorage implements ConfigStorage {
    private final RealmConfiguration mConfig;

    /**
     * Instantiates a new RealmConfigStorage.
     */
    public RealmConfigStorage() {
        mConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    private Realm getRealmForThisThread() {
        return Realm.getInstance(mConfig);
    }

    @Override
    public void saveSelectedMarket(Market market) {
        Realm realmInstance = getRealmForThisThread();
        UserConfig userConfig = getUserConfig(realmInstance);
        realmInstance.executeTransaction(realm -> {
            //remove current selected market from DB (no need to store more than one Market right now)
            Market currentSelectedMarket = userConfig.getSelectedMarket();
            if (currentSelectedMarket != null) {
                currentSelectedMarket.deleteFromRealm();
            }
            //need create realm managed object to set it to other realm managed object
            Market realmMarket = realmInstance.copyToRealm(market);
            userConfig.setSelectedMarket(realmMarket);
        });
        realmInstance.close();
    }

    @Override
    @Nullable
    public Market getSelectedMarket() {
        Realm realmInstance = getRealmForThisThread();
        Market realmMarket = getUserConfig(realmInstance).getSelectedMarket();
        Market market = null;
        if (realmMarket != null) {
            market = realmInstance.copyFromRealm(realmMarket);
        }
        realmInstance.close();
        return market;
    }

    @Override
    public void saveLabels(LabelsResponseData labels) {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> {
            UserConfig userConfig = getUserConfig(realm);

            LabelsResponseData currentLabels = userConfig.getLabels();
            if (currentLabels != null) {
                currentLabels.deleteFromRealm();
            }

            LabelsResponseData realLabels = realm.copyToRealm(labels);
            userConfig.setLabels(realLabels);
        });
        realmInstance.close();
    }

    @Override
    public LabelsResponseData getLabels() {
        Realm realmInstance = getRealmForThisThread();
        LabelsResponseData labels;
        try {
            labels = getUserConfig(realmInstance).getLabels();
            if (labels != null) {
                labels = realmInstance.copyFromRealm(labels);
            }
        } finally {
            realmInstance.close();
        }
        return labels;
    }

    @Override
    public void saveCommonData(CommonDataResponseData data) {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> {
            UserConfig userConfig = getUserConfig(realm);
            CommonDataResponseData currentData = userConfig.getCommonData();
            if (currentData != null) {
                currentData.deleteFromRealm();
            }
            //need create realm managed object to set it to other realm managed object
            CommonDataResponseData realData = realm.copyToRealm(data);
            userConfig.setCommonData(realData);
        });
        realmInstance.close();
    }

    @Override
    public CommonDataResponseData getCommonData() {
        Realm realmInstance = getRealmForThisThread();
        CommonDataResponseData commonDataResponseData = null;
        try {
            commonDataResponseData = getUserConfig(realmInstance).getCommonData();
            if (commonDataResponseData != null) {
                commonDataResponseData = realmInstance.copyFromRealm(commonDataResponseData);
            }
        } finally {
            realmInstance.close();
        }
        return commonDataResponseData;
    }

    @Override
    public void saveLoginToken(String token) {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> {
            UserConfig userConfig = getUserConfig(realm);
            userConfig.setLoginToken(token);
        });
        realmInstance.close();
    }

    @Override
    @Nullable
    public String readLoginToken() {
        Realm realmInstance = getRealmForThisThread();
        UserConfig userConfig = getUserConfig(realmInstance);
        String token = userConfig.getLoginToken();
        realmInstance.close();
        return token;
    }

    @Override
    public void savePushToken(String token) {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> {
            UserConfig userConfig = getUserConfig(realm);
            userConfig.setFirebasePushToken(token);
        });
        realmInstance.close();
    }

    @Override
    @Nullable
    public String readPushToken() {
        Realm realmInstance = getRealmForThisThread();
        UserConfig userConfig = getUserConfig(realmInstance);
        String token = userConfig.getFirebasePushToken();
        realmInstance.close();
        return token;
    }

    @Override
    public void saveShouldUpdatePushToken(boolean shouldUpdatePushToken) {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> {
            UserConfig userConfig = getUserConfig(realm);
            userConfig.setShouldUpdatePushToken(shouldUpdatePushToken);
        });
        realmInstance.close();
    }

    @Override
    public boolean readShouldUpdatePushToken() {
        Realm realmInstance = getRealmForThisThread();
        UserConfig userConfig = getUserConfig(realmInstance);
        boolean shouldUpdate = userConfig.getShouldUpdatePushToken();
        realmInstance.close();
        return shouldUpdate;
    }

    @Override
    public void saveShouldUpdateDashboard(boolean shouldUpdateDashboard) {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> {
            UserConfig userConfig = getUserConfig(realm);
            userConfig.setShouldUpdateDashboard(shouldUpdateDashboard);
        });
        realmInstance.close();
    }

    @Override
    public boolean readShouldUpdateDashboard() {
        Realm realmInstance = getRealmForThisThread();
        UserConfig userConfig = getUserConfig(realmInstance);
        boolean shouldUpdate = userConfig.getShouldUpdateDashboard();
        realmInstance.close();
        return shouldUpdate;
    }

    private UserConfig getUserConfig(Realm realm) {
        RealmResults<UserConfig> result = realm.where(UserConfig.class).findAll();
        if (result.size() == 0) {
            realm.beginTransaction();
            UserConfig userConfig = realm.createObject(UserConfig.class);
            realm.commitTransaction();
            return userConfig;
        } else {
            return result.get(0);
        }
    }

    @Override
    public void clearAllData() {
        Realm realmInstance = getRealmForThisThread();
        realmInstance.executeTransaction(realm -> realm.deleteAll());
        realmInstance.close();
    }
}
