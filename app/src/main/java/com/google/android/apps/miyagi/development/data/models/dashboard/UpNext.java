package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lukasz on 27.12.2016.
 */

public class UpNext {

    @SerializedName("images")
    private ImagesBaseModel mImages;

    @SerializedName("subhead")
    private String mSubhead;

    @SerializedName("cta")
    private String mCta;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("image_alt")
    private String mImageAlt;

    @SerializedName("action")
    private UpNextAction mAction;

    public UpNextAction getAction() {
        return mAction;
    }

    public String getImageAlt() {
        return mImageAlt;
    }

    public ImagesBaseModel getImages() {
        return mImages;
    }

    public String getSubhead() {
        return mSubhead;
    }

    public String getCta() {
        return mCta;
    }

    public String getTitle() {
        return mTitle;
    }
}
