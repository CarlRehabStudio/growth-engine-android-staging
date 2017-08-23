package com.google.android.apps.miyagi.development.data.net.responses.labels;

import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.labels.CheckAccountLabels;
import com.google.android.apps.miyagi.development.data.models.labels.CreateAccountFullLabels;
import com.google.android.apps.miyagi.development.data.models.labels.CreateAccountMissingInfoLabels;
import com.google.android.apps.miyagi.development.data.models.labels.EnterPasswordLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ErrorsLabels;
import com.google.android.apps.miyagi.development.data.models.labels.FirebaseMessagesLabels;
import com.google.android.apps.miyagi.development.data.models.labels.ResetPasswordLabels;
import com.google.android.apps.miyagi.development.data.models.labels.SignInSelectionLabels;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcin on 22.12.2016.
 */

public class LabelsResponseData extends RealmObject {

    @SerializedName("register_xsrf_token")
    private String mRegisterXsrfToken;

    @SerializedName("push_xsrf_token")
    private String mPushXsrfToken;

    @SerializedName("sign_in_method_selection")
    private SignInSelectionLabels mSignInSelectionLabels;

    @SerializedName("check_account")
    private CheckAccountLabels mCheckAccountLabels;

    @SerializedName("create_account_full")
    private CreateAccountFullLabels mCreateAccountFullLabels;

    @SerializedName("create_account_missing_info")
    private CreateAccountMissingInfoLabels mCreateAccountMissingInfoLabels;

    @SerializedName("enter_password")
    private EnterPasswordLabels mEnterPasswordLabels;

    @SerializedName("firebase_messages")
    private FirebaseMessagesLabels mFirebaseMessagesLabels;

    @SerializedName("errors")
    private ErrorsLabels mErrorsLabels;

    @SerializedName("colors")
    private AppColors mColors;

    @SerializedName("reset_password")
    private ResetPasswordLabels mResetPasswordLabels;

    public String getRegisterXsrfToken() {
        return mRegisterXsrfToken;
    }

    public String getPushXsrfToken() {
        return mPushXsrfToken;
    }

    public CreateAccountMissingInfoLabels getCreateAccountMissingInfoLabels() {
        return mCreateAccountMissingInfoLabels;
    }

    public SignInSelectionLabels getSignInSelectionLabels() {
        return mSignInSelectionLabels;
    }

    public CheckAccountLabels getCheckAccountLabels() {
        return mCheckAccountLabels;
    }

    public CreateAccountFullLabels getCreateAccountFullLabels() {
        return mCreateAccountFullLabels;
    }

    public EnterPasswordLabels getEnterPasswordLabels() {
        return mEnterPasswordLabels;
    }

    public FirebaseMessagesLabels getFirebaseMessagesLabels() {
        return mFirebaseMessagesLabels;
    }

    public ErrorsLabels getErrorsLabels() {
        return mErrorsLabels;
    }

    public AppColors getColors() {
        return mColors;
    }

    public ResetPasswordLabels getResetPasswordLabels() {
        return mResetPasswordLabels;
    }
}
