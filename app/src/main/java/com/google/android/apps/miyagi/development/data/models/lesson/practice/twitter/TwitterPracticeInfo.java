package com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter;


import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class TwitterPracticeInfo {

    @SerializedName("images")
    protected ImagesBaseModel mImages;

    @SerializedName("handle")
    protected String mHandle;

    @SerializedName("name")
    protected String mName;

    public String getHandle() {
        return mHandle;
    }

    public ImagesBaseModel getImages() {
        return mImages;
    }

    public String getName() {
        return mName;
    }

}