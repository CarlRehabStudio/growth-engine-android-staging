package com.google.android.apps.miyagi.development.data.models.profile;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

@Parcel
public class Form {

    @SerializedName("save_button")
    protected String mSaveButton;

    @SerializedName("validation")
    protected Validation mValidation;

    @SerializedName("save_failure")
    protected String mSaveFailure;

    @SerializedName("save_success")
    protected String mSaveSuccess;

    public String getSaveButton() {
        return mSaveButton;
    }

    public Validation getValidation() {
        return mValidation;
    }

    public String getSaveFailure() {
        return mSaveFailure;
    }

    public String getSaveSuccess() {
        return mSaveSuccess;
    }
}
