package com.google.android.apps.miyagi.development.data.models.dashboard;

/**
 * Created by Paweł on 2017-02-18.
 */

public class LegalItem {

    private LegalItemType mType;

    private String mTitle;

    private String mUrl;

    private LegalItem(Builder builder) {
        setType(builder.mType);
        setTitle(builder.mTitle);
        setUrl(builder.mUrl);
    }

    public LegalItemType getType() {
        return mType;
    }

    public void setType(LegalItemType type) {
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public static final class Builder {
        private LegalItemType mType;
        private String mTitle;
        private String mUrl;

        public Builder() {
        }

        public Builder withType(LegalItemType val) {
            mType = val;
            return this;
        }

        public Builder withTitle(String val) {
            mTitle = val;
            return this;
        }

        public Builder withUrl(String val) {
            mUrl = val;
            return this;
        }

        public LegalItem build() {
            return new LegalItem(this);
        }
    }
}
