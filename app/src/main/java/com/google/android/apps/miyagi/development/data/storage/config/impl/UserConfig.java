package com.google.android.apps.miyagi.development.data.storage.config.impl;

import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.data.models.markets.Market;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.labels.LabelsResponseData;

import io.realm.RealmObject;

/**
 * Created by jerzyw on 08.11.2016.
 */

public class UserConfig extends RealmObject {

    private Market mSelectedMarket;
    private String mLoginToken;
    private LabelsResponseData mLabels;
    private CommonDataResponseData mCommonData;
    private String mFirebasePushToken;
    private boolean mShouldUpdatePushToken;
    private boolean mShouldUpdateDashboard;

    public UserConfig() {
        super();
    }

    public void setSelectedMarket(Market selectedMarket) {
        mSelectedMarket = selectedMarket;
    }

    public Market getSelectedMarket() {
        return mSelectedMarket;
    }

    public void setLoginToken(@Nullable String loginToken) {
        mLoginToken = loginToken;
    }

    public String getLoginToken() {
        return mLoginToken;
    }

    public void setLabels(@Nullable LabelsResponseData labels) {
        mLabels = labels;
    }

    public LabelsResponseData getLabels() {
        return mLabels;
    }

    public void setCommonData(@Nullable CommonDataResponseData data) {
        mCommonData = data;
    }

    public CommonDataResponseData getCommonData() {
        return mCommonData;
    }

    public void setFirebasePushToken(@Nullable String pushToken) {
        mFirebasePushToken = pushToken;
    }

    public String getFirebasePushToken() {
        return mFirebasePushToken;
    }

    public void setShouldUpdatePushToken(boolean shouldUpdatePushToken) {
        mShouldUpdatePushToken = shouldUpdatePushToken;
    }

    public boolean getShouldUpdatePushToken() {
        return mShouldUpdatePushToken;
    }

    public boolean getShouldUpdateDashboard() {
        return mShouldUpdateDashboard;
    }

    public void setShouldUpdateDashboard(boolean shouldUpdateDashboard) {
        mShouldUpdateDashboard = shouldUpdateDashboard;
    }
}
