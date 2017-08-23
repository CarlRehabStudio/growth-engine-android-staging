package com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class SwipePracticeOption {

    @SerializedName("id")
    protected String mId;

    // TODO check if needed?
    @SerializedName("image")
    protected String mImage;

    @SerializedName("images")
    protected ImagesBaseModel mImages;

    @SerializedName("text")
    protected String mText;

    public String getId() {
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

}
