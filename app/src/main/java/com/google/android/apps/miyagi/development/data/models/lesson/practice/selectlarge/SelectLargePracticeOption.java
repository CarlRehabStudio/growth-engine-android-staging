package com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 05.12.2016.
 */

@Parcel
public class SelectLargePracticeOption {

    @SerializedName("id")
    protected String mId;

    @SerializedName("text")
    protected String mText;

    @SerializedName("images")
    protected ImagesBaseModel mImages;

    public String getId() {
        return mId;
    }

    public ImagesBaseModel getImages() {
        return mImages;
    }

    public String getText() {
        return mText;
    }

}
