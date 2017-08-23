package com.google.android.apps.miyagi.development.ui.components.widget.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;

/**
 * Created by lukaszweglinski on 30.11.2016.
 */

public abstract class BaseMeasureSpecAdapterView extends AdapterView {

    private int mHeightMeasureSpec;
    private int mWidthMeasureSpec;

    public BaseMeasureSpecAdapterView(Context context) {
        super(context);
    }

    public BaseMeasureSpecAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMeasureSpecAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidthMeasureSpec = widthMeasureSpec;
        this.mHeightMeasureSpec = heightMeasureSpec;
    }

    public int getWidthMeasureSpec() {
        return mWidthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return mHeightMeasureSpec;
    }
}
