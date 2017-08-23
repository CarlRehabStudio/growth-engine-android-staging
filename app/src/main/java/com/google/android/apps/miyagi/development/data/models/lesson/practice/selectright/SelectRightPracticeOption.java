package com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 13.12.2016.
 */
@Parcel
public class SelectRightPracticeOption {

    public int getId() {
        return mId;
    }

    public String getImage() {
        return mImage;
    }

    public ImagesBaseModel getImages() {
        return mImages;
    }

    public String getText() {
        return mText;
    }

    @SerializedName("id")
    protected int mId;

    @SerializedName("text")
    protected String mText;

    @SerializedName("images")
    protected ImagesBaseModel mImages;

    @SerializedName("image")
    protected String mImage;

}
