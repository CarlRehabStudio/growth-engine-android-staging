package com.google.android.apps.miyagi.development.data.net.responses.profile;

import com.google.android.apps.miyagi.development.data.models.profile.Form;
import com.google.android.apps.miyagi.development.data.models.profile.Login;
import com.google.android.apps.miyagi.development.data.models.profile.Personal;
import com.google.android.apps.miyagi.development.data.models.profile.Settings;
import com.google.android.apps.miyagi.development.data.models.profile.Student;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

@Parcel
public class ProfileResponseData {

    @SerializedName("form")
    protected Form mForm;

    @SerializedName("settings")
    protected Settings mSettings;

    @SerializedName("personal")
    protected Personal mPersonal;

    @SerializedName("header")
    protected String mHeader;

    @SerializedName("student")
    protected Student mStudent;

    @SerializedName("login")
    protected Login mLogin;

    @SerializedName("student_wipeout_xsrf_token")
    protected String mStudentWipeoutXsrfToken;

    @SerializedName("student_edit_xsrf_token")
    protected String mStudentEditXsrfToken;

    @SerializedName("web_profile_url")
    protected String mWebProfileUrl;

    @SerializedName("push_xsrf_token")
    protected String mPushXsrfToken;

    public String getWebProfileUrl() {
        return mWebProfileUrl;
    }

    public Form getForm() {
        return mForm;
    }

    public Settings getSettings() {
        return mSettings;
    }

    public Personal getPersonal() {
        return mPersonal;
    }

    public String getHeader() {
        return mHeader;
    }

    public Student getStudent() {
        return mStudent;
    }

    public Login getLogin() {
        return mLogin;
    }

    public String getStudentWipeoutXsrfToken() {
        return mStudentWipeoutXsrfToken;
    }

    public String getStudentEditXsrfToken() {
        return mStudentEditXsrfToken;
    }

    public String getPushXsrfToken() {
        return mPushXsrfToken;
    }
}
