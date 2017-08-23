package com.google.android.apps.miyagi.development.data.models.commondata;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Lukasz on 20.01.2017.
 */

public class AppColors extends RealmObject {

    @SerializedName("main_cta")
    private String mMainCta;

    @SerializedName("main_background")
    private String mMainBackground;

    @SerializedName("section_background")
    private String mSectionBackground;


    public int getMainCtaColor() {
        return ColorHelper.parseColor(mMainCta);
    }

    public int getMainBackgroundColor() {
        return ColorHelper.parseColor(mMainBackground);
    }

    public int getSectionBackgroundColor() {
        return ColorHelper.parseColor(mSectionBackground);
    }
}
