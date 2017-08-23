package com.google.android.apps.miyagi.development.data.models.markets;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

/**
 * Created by jerzyw on 10.10.2016.
 */
@Parcel
public class Market extends RealmObject {

    @PrimaryKey
    @SerializedName("market_id")
    protected String mMarketId;

    @SerializedName("flag")
    protected ImagesBaseModel mFlag;

    @SerializedName("display_name")
    protected String mDisplayName;

    @SerializedName("endpoint_url")
    protected String mEndpointUrl;

    public String getMarketId() {
        return mMarketId;
    }

    public ImagesBaseModel getFlag() {
        return mFlag;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getEndpointUrl() {
        return mEndpointUrl;
    }
}
