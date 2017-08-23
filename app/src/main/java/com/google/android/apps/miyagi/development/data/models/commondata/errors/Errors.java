package com.google.android.apps.miyagi.development.data.models.commondata.errors;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class Errors extends RealmObject {

    @SerializedName("no_network")
    private CommonDataError mNoNetworkError;

    @SerializedName("go_to_offline")
    private CommonDataError mGoToOfflineError;

    @SerializedName("internal")
    private CommonDataError mInternalError;

    public CommonDataError getGoToOfflineError() {
        return mGoToOfflineError;
    }

    public CommonDataError getInternalError() {
        return mInternalError;
    }

    public CommonDataError getNoNetworkError() {
        return mNoNetworkError;
    }
}
