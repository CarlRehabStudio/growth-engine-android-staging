package com.google.android.apps.miyagi.development.data.models.commondata;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class Student extends RealmObject {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("avatar")
    private String mAvatar;

    public String getAvatar() {
        return mAvatar;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUsername() {
        return mUsername;
    }
}
