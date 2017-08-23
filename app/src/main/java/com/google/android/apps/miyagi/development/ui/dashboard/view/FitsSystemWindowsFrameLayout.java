package com.google.android.apps.miyagi.development.ui.dashboard.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Created by Paweł on 2017-03-14.
 */

/**
 * Class that handles problem with dispatching fitSystemWindow flag to children (especially Fragments).
 */
public class FitsSystemWindowsFrameLayout extends FrameLayout {

    public FitsSystemWindowsFrameLayout(Context context) {
        super(context);
    }

    public FitsSystemWindowsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitsSystemWindowsFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            getChildAt(index).dispatchApplyWindowInsets(insets);
        }
        return insets;
    }
}
