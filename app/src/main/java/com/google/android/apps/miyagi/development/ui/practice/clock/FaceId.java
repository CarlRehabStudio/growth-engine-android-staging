package com.google.android.apps.miyagi.development.ui.practice.clock;

import com.google.android.apps.miyagi.development.R;

/**
 * Created by jerzyw on 01.12.2016.
 */

public enum FaceId {
    FACE_1(7, R.drawable.ic_mr_clock_0),
    FACE_2(0, R.drawable.ic_mr_clock_3_0),
    FACE_3(1, R.drawable.ic_mr_clock_6_0),
    FACE_4(2, R.drawable.ic_mr_clock_9_0),
    FACE_5(3, R.drawable.ic_mr_clock_1_2_0),
    FACE_6(4, R.drawable.ic_mr_clock_1_5_0),
    FACE_7(5, R.drawable.ic_mr_clock_1_8_0);

    private final int mApiId;
    private int mFaceDrawableId;

    FaceId(int apiId, int faceDrawableId) {
        mApiId = apiId;
        mFaceDrawableId = faceDrawableId;
    }

    public int getApiId() {
        return mApiId;
    }

    public int getFaceDrawableId() {
        return mFaceDrawableId;
    }
}
