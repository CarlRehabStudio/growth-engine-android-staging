package com.google.android.apps.miyagi.development.data.models.profile;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 11.01.2017.
 */

@Parcel
public class Login {

    @SerializedName("header")
    protected String mHeader;

    @SerializedName("edit_profile")
    protected String mEditProfile;

    @SerializedName("delete_account")
    protected String mDeleteAccount;

    public String getHeader() {
        return mHeader;
    }

    public String getEditProfile() {
        return mEditProfile;
    }

    public String getDeleteAccount() {
        return mDeleteAccount;
    }
}
