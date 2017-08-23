package com.google.android.apps.miyagi.development.data.models.commondata.menu;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class LegalPage extends RealmObject {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("terms_text")
    private String mTermsText;
    @SerializedName("terms_link")
    private String mTermsLink;

    @SerializedName("policy_text")
    private String mPolicyText;
    @SerializedName("policy_link")
    private String mPolicyLink;

    @SerializedName("licence_text")
    private String mLicenceText;

    @SerializedName("impressum_text")
    private String mImpressumText;

    @SerializedName("impressum_link")
    private String mImpressumLink;

    public String getTitle() {
        return mTitle;
    }

    public String getTermsText() {
        return mTermsText;
    }

    public String getTermsLink() {
        return mTermsLink;
    }

    public String getPolicyText() {
        return mPolicyText;
    }

    public String getPolicyLink() {
        return mPolicyLink;
    }

    public String getLicenceText() {
        return mLicenceText;
    }

    public String getImpressumText() {
        return mImpressumText;
    }

    public String getImpressumLink() {
        return mImpressumLink;
    }

}
