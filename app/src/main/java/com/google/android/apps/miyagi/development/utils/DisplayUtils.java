package com.google.android.apps.miyagi.development.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by marcin on 25.01.2017.
 */

public class DisplayUtils {

    /**
     * Returns device screen width.
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int orientation = display.getRotation();
        if (orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180) {
            return metrics.widthPixels;
        }
        return metrics.heightPixels;
    }

    /**
     * Returns device screen height.
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int orientation = display.getRotation();
        if (orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180) {
            return metrics.heightPixels;
        }
        return metrics.widthPixels;
    }

    /**
     * Returns display height.
     */
    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * Returns system status bar height.
     */
    public static int getStatusBarHeight(Context context) {
        int result;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        } else {
            return (int) Math.ceil(
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
        }
        return result;
    }

}
