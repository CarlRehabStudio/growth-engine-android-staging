package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;

import com.google.android.apps.miyagi.development.R;

/**
 * Created by lukaszweglinski on 10.11.2016.
 */

public class ViewUtils {

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale);
    }

    public static float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getBoolean(R.bool.isLandscape);
    }

    public static boolean isLandTablet(Context context) {
        return isTablet(context) && isLandscape(context);
    }

    public static boolean isPortTablet(Context context) {
        return isTablet(context) && !isLandscape(context);
    }

    public static boolean isSmallDevice(Context context) {
        return context.getResources().getBoolean(R.bool.isSmallDevice);
    }
}