package com.google.android.apps.miyagi.development.helpers;

import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by lukaszweglinski on 04.11.2016.
 */

public class DrawableHelper {
    public static void setTintDrawable(Drawable drawable, int color) {
        DrawableCompat.setTint(drawable, color);
    }

    /**
     * Specifies a tint for drawable.
     */
    public static Drawable setTint(Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}
