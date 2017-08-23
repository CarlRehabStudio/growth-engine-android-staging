package com.google.android.apps.miyagi.development.data.net.responses.commondata;

import com.google.android.apps.miyagi.development.data.models.commondata.AppColors;
import com.google.android.apps.miyagi.development.data.models.commondata.Copy;
import com.google.android.apps.miyagi.development.data.models.commondata.Student;
import com.google.android.apps.miyagi.development.data.models.commondata.errors.Errors;
import com.google.android.apps.miyagi.development.data.models.commondata.menu.Menu;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class CommonDataResponseData extends RealmObject {

    @SerializedName("menu")
    private Menu mMenu;

    @SerializedName("errors")
    private Errors mErrors;

    @SerializedName("student")
    private Student mStudent;

    @SerializedName("colors")
    private AppColors mColors;

    @SerializedName("push_xsrf_token")
    private String mPushXsrfToken;

    @SerializedName("copy")
    private Copy mCopy;

    public Errors getErrors() {
        return mErrors;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public Student getStudent() {
        return mStudent;
    }

    public AppColors getColors() {
        return mColors;
    }

    public String getPushXsrfToken() {
        return mPushXsrfToken;
    }

    public Copy getCopy() {
        return mCopy;
    }
}
