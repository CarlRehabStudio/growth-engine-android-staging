package com.google.android.apps.miyagi.development.data.models.commondata.menu;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

import java.util.List;


/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class Menu extends RealmObject {

    @SerializedName("offline_dash")
    private MenuItem mOfflineDash;

    @SerializedName("profile")
    private MenuItem mProfile;

    @SerializedName("signout")
    private MenuItem mSignout;

    @SerializedName("dash")
    private MenuItem mDash;

    @SerializedName("uses_system_font")
    private boolean mUsesSystemFont;

    @SerializedName("external")
    private RealmList<MenuItem> mExternal;

    @SerializedName("legal")
    private Legal mLegal;

    public MenuItem getDash() {
        return mDash;
    }

    public List<MenuItem> getExternal() {
        return mExternal;
    }

    public Legal getLegal() {
        return mLegal;
    }

    public MenuItem getOfflineDash() {
        return mOfflineDash;
    }

    public MenuItem getProfile() {
        return mProfile;
    }

    public MenuItem getSignout() {
        return mSignout;
    }

    public boolean isUsesSystemFont() {
        return mUsesSystemFont;
    }
}
