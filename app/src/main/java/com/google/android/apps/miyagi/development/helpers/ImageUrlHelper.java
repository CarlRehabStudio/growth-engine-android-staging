package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;

/**
 * Created by lukaszweglinski on 29.12.2016.
 */

public class ImageUrlHelper {

    /**
     * Gets proper image url for device screen density.
     */
    @Nullable
    public static String getUrlFor(Context context, ImagesBaseModel imagesBaseModel) {
        int density = context.getResources().getDisplayMetrics().densityDpi;

        if (ViewUtils.isTablet(context)) {
            if (density < DisplayMetrics.DENSITY_XHIGH) {
                return imagesBaseModel.getXhdpi();
            }
        }
        if (density >= DisplayMetrics.DENSITY_XXXHIGH) {
            return imagesBaseModel.getXxxhdpi();
        }
        if (density >= DisplayMetrics.DENSITY_XXHIGH) {
            return imagesBaseModel.getXxhdpi();
        }
        if (density >= DisplayMetrics.DENSITY_XHIGH) {
            return imagesBaseModel.getXhdpi();
        }
        if (density >= DisplayMetrics.DENSITY_HIGH) {
            return imagesBaseModel.getHdpi();
        }
        if (density >= DisplayMetrics.DENSITY_MEDIUM) {
            return imagesBaseModel.getMdpi();
        }
        return null;
    }
}
