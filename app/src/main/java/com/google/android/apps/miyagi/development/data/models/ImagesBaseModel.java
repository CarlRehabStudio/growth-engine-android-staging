package com.google.android.apps.miyagi.development.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 29.12.2016.
 */

@Parcel
public class ImagesBaseModel extends RealmObject {

    @SerializedName("mdpi")
    protected String mMdpi;

    @SerializedName("hdpi")
    protected String mHdpi;

    @SerializedName("xhdpi")
    protected String mXhdpi;

    @SerializedName("xxhdpi")
    protected String mXxhdpi;

    @SerializedName("xxxhdpi")
    protected String mXxxhdpi;

    public String getMdpi() {
        return mMdpi;
    }

    public String getHdpi() {
        return mHdpi;
    }

    public String getXhdpi() {
        return mXhdpi;
    }

    public String getXxhdpi() {
        return mXxhdpi;
    }

    public String getXxxhdpi() {
        return mXxxhdpi;
    }
}
