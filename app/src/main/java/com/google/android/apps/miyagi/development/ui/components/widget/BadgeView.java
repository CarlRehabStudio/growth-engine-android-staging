package com.google.android.apps.miyagi.development.ui.components.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by lukaszweglinski on 30.11.2016.
 */

public class BadgeView extends AppCompatImageView {

    private Paint mBackgroundPaint;
    private RectF mOvalRect = new RectF();
    private int mBackgroundColor;

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPainters();
    }

    private void initPainters() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(mBackgroundColor);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mOvalRect.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(mOvalRect, mBackgroundPaint);
        super.onDraw(canvas);
    }
}
