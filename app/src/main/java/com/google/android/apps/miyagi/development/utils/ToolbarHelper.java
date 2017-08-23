package com.google.android.apps.miyagi.development.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.google.android.apps.miyagi.development.R;

/**
 * Created by jerzyw on 28.10.2016.
 */

public class ToolbarHelper {

    /**
     * Sets up navigation mode to toolbar.
     */
    public static Toolbar setUpChildActivityToolbar(AppCompatActivity toolbarActivity) {
        Toolbar toolbar = (Toolbar) toolbarActivity.findViewById(R.id.toolbar);
        toolbarActivity.setSupportActionBar(toolbar);
        toolbarActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = toolbarActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            final Drawable upArrow = ContextCompat.getDrawable(toolbarActivity, android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
            TypedValue typedValue = new TypedValue();

            int color = toolbar.getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorControlNormal}).getColor(0, 0);
            upArrow.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            toolbarActivity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        return toolbar;
    }

    /**
     * Sets background color for statusbar and toolbar.
     */
    public static void setColorForStatusAndToolbar(AppCompatActivity activity, int color) {
        setColorForStatus(activity, color);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(color);
    }

    /**
     * Sets background color for statusbar.
     */
    public static void setColorForStatus(AppCompatActivity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }
}



