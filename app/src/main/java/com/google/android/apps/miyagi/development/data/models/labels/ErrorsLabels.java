package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcin on 22.12.2016.
 */
public class ErrorsLabels extends RealmObject {

    @SerializedName("no_connection_error")
    private String mNoConnectionError;

    @SerializedName("no_connection_button")
    private String mNoConnectionButton;

    @SerializedName("server_error")
    private String mServerError;

    @SerializedName("server_error_button")
    private String mServerErrorButton;

    @SerializedName("deleted_account_button")
    private String mDeletedAccountButton;

    @SerializedName("deleted_account")
    private String mDeletedAccount;

    public String getDeletedAccount() {
        return mDeletedAccount;
    }

    public String getDeletedAccountButton() {
        return mDeletedAccountButton;
    }

    public String getNoConnectionError() {
        return mNoConnectionError;
    }

    public String getNoConnectionButton() {
        return mNoConnectionButton;
    }

    public String getServerError() {
        return mServerError;
    }

    public String getServerErrorButton() {
        return mServerErrorButton;
    }
}
