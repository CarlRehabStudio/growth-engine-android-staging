package com.google.android.apps.miyagi.development.data.models.commondata.menu;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class MenuItem extends RealmObject {

    @SerializedName("icon")
    private String mIcon;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("url")
    private String mUrl;

    public String getIcon() {
        return mIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }
}
