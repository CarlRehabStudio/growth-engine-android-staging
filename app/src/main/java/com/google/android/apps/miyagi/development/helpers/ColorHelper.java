package com.google.android.apps.miyagi.development.helpers;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;

/**
 * Created by Lukasz on 18.12.2016.
 */

public class ColorHelper {

    /**
     * Gets color with alpha.
     *
     * @param color the color.
     * @param ratio the ratio of alpha 0.0f - 1.0f.
     * @return the color with alpha.
     */
    public static int getColorWithAlpha(int color, @FloatRange(from = 0f, to = 1.0f) float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        newColor = Color.argb(alpha, red, green, blue);
        return newColor;
    }


    /**
     * Try parse color string to int or return Color.WHITE if error.
     *
     * @param colorString color in hex string.
     * @return int color;
     */
    public static int parseColor(String colorString) {
        try {
            return Color.parseColor(colorString);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return Color.WHITE;
        }
    }

    /**
     * Tint background color on support button.
     *
     * @param button button.
     * @param color  accent color for button.
     */
    public static void tintButtonBackground(AppCompatButton button, int color) {
        ViewCompat.setBackgroundTintMode(button, PorterDuff.Mode.MULTIPLY);
        ViewCompat.setBackgroundTintList(button, getButtonColorStateList(color));
    }

    private static ColorStateList getButtonColorStateList(int accentColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new ColorStateList(
                    new int[][]{{android.R.attr.state_pressed}, {android.R.attr.state_enabled}, {-android.R.attr.state_enabled}},
                    new int[]{accentColor, accentColor, accentColor});
        } else {
            float[] colorHsv = new float[3];
            Color.colorToHSV(accentColor, colorHsv);
            colorHsv[2] *= 0.9f;
            int darkerAccent = Color.HSVToColor(colorHsv);

            return new ColorStateList(
                    new int[][]{{android.R.attr.state_pressed}, {android.R.attr.state_enabled}, {-android.R.attr.state_enabled}},
                    new int[]{darkerAccent, accentColor, accentColor});
        }
    }

    /**
     * Tint foreground color.
     *
     * @param foreground drawable background.
     * @param color      color to change.
     */
    public static void tintForeground(Drawable foreground, int color) {
        foreground.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
