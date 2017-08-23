package com.google.android.apps.miyagi.development.data.models.commondata.errors;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class CommonDataError extends RealmObject {

    @SerializedName("text")
    private String mText;

    @SerializedName("cta")
    private String mCta;

    public String getCta() {
        return mCta;
    }

    public String getText() {
        return mText;
    }
}
