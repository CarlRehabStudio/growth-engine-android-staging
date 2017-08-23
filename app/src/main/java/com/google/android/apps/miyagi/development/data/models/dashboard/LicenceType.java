package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.android.apps.miyagi.development.R;

/**
 * Created by Paweł on 2017-02-18.
 */

public enum LicenceType {
    APACHE2(1, R.raw.licence_apache2),
    JUNIT(2, R.raw.licence_junit),
    GLIDE(3, R.raw.licence_glide);

    private final int mType;
    private final int mLicenceId;

    LicenceType(int type, int licence) {
        mType = type;
        mLicenceId = licence;
    }

    public int getType() {
        return mType;
    }

    public int getLicenceId() {
        return mLicenceId;
    }

    /**
     * Returns licence type from integer.
     */
    public static LicenceType fromType(int type) {
        switch (type) {
            case 2:
                return JUNIT;
            case 3:
                return GLIDE;
            case 1:
            default:
                return APACHE2;
        }
    }
}
