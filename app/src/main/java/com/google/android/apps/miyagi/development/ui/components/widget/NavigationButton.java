package com.google.android.apps.miyagi.development.ui.components.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by marcin on 16.12.2016.
 */

public class NavigationButton extends FrameLayout {

    private static final float DISABLED_BUTTON_ALPHA = 0.26f;
    private static final float ENABLED_BUTTON_ALPHA = 1.f;

    private boolean mIsEnabled;
    private OnClickListener mEnabledOnClickListener;
    private OnClickListener mDisabledOnClickListener;

    private OnClickListener mInternalOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsEnabled) {
                if (mEnabledOnClickListener != null) {
                    mEnabledOnClickListener.onClick(v);
                }
            } else {
                if (mDisabledOnClickListener != null) {
                    mDisabledOnClickListener.onClick(v);
                }
            }
        }
    };

    public NavigationButton(Context context) {
        this(context, null);
    }

    public NavigationButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIsEnabled = super.isEnabled();
    }

    /**
     * Sets enabled state of button.
     */
    @Override
    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
        TextView textView = (TextView) getChildAt(0);
        textView.setEnabled(enabled);
        textView.setAlpha(enabled ? ENABLED_BUTTON_ALPHA : DISABLED_BUTTON_ALPHA);
        textView.invalidate();
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void enable() {
        setVisibility(VISIBLE);
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(mInternalOnClickListener);
        setClickable(true);
        TextView textView = (TextView) getChildAt(0);
        textView.setClickable(false);
        setEnabledOnClickListener(l);
    }


    public void setEnabledOnClickListener(OnClickListener enableOnClickListener) {
        mEnabledOnClickListener = enableOnClickListener;
    }

    public void setDisabledOnClickListener(OnClickListener disabledOnClickListener) {
        mDisabledOnClickListener = disabledOnClickListener;
    }

    public void setText(String text) {
        TextView textView = (TextView) getChildAt(0);
        textView.setText(text);
    }

    public CharSequence getText() {
        TextView textView = (TextView) getChildAt(0);
        return textView.getText();
    }

    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        TextView textView = (TextView) getChildAt(0);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    }

    public void setCompoundDrawables(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        TextView textView = (TextView) getChildAt(0);
        textView.setCompoundDrawables(start, top, end, bottom);
    }
}
