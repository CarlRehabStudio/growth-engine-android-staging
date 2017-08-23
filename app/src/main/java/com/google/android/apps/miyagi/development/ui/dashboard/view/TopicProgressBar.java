package com.google.android.apps.miyagi.development.ui.dashboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

/**
 * TODO: document your custom view class.
 */
public class TopicProgressBar extends View {

    private static final float DEFAULT_CORNER_RADIUS_DP = 5.5f;
    private static final float DEFAULT_ALPHA_LESSONS_COMPLETED = 1f;
    private static final float DEFAULT_ALPHA_TOTAL_LESSONS = 0.15f;
    private static final int DEFAULT_MAIN_COLOR = 0x7e57c2;
    private static final int DEFAULT_BAR_HEIGHT_DP = 10;

    private Paint mLessonsCompletedPaint;
    private Paint mLessonsTotalPaint;

    private float mProgressValue = 0f;
    //styles
    private float mCornerRadiusPx;
    private int mMainColor = DEFAULT_MAIN_COLOR;
    private float mLessonsCompletedAlpha = DEFAULT_ALPHA_LESSONS_COMPLETED;
    private float mTotalLessonsAlpha = DEFAULT_ALPHA_TOTAL_LESSONS;

    //object need for optimization
    private RectF mRect = new RectF();
    private static final int INDEX_LESSONS_COMPLETED = 0;
    private static final int INDEX_BG = 1;
    private float[] mProgresses = {0f, 0f};
    private Paint[] mPaints = {null, null};
    private int mDefaultMinHeightPx;

    public TopicProgressBar(Context context) {
        super(context);
        init(null, 0);
    }

    public TopicProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TopicProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mLessonsCompletedPaint = new Paint();
        mLessonsTotalPaint = new Paint();

        mPaints[INDEX_LESSONS_COMPLETED] = mLessonsCompletedPaint;
        mPaints[INDEX_BG] = mLessonsTotalPaint;

        // Load attributes
        final TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.TopicProgressBar, defStyle, 0);
        mMainColor = typedArray.getColor(R.styleable.TopicProgressBar_mainColor, mMainColor);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mCornerRadiusPx = typedArray.getDimensionPixelSize(R.styleable.TopicProgressBar_cornerRadius,
                ViewUtils.dp2px(getContext(), DEFAULT_CORNER_RADIUS_DP));

        mLessonsCompletedAlpha = typedArray.getFloat(R.styleable.TopicProgressBar_lessonsCompletedAlpha, mLessonsCompletedAlpha);
        mTotalLessonsAlpha = typedArray.getFloat(R.styleable.TopicProgressBar_totalLessonsAlpha, mTotalLessonsAlpha);
        typedArray.recycle();

        mDefaultMinHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BAR_HEIGHT_DP, displayMetrics);
        invalidatePaints();
        invalidateProgress();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            int minimumHeight = mDefaultMinHeightPx + getPaddingTop() + getPaddingBottom();
            heightMeasureSpec = resolveSizeAndState(minimumHeight, heightMeasureSpec, 0);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = width - paddingLeft - paddingRight;
        //...
        for (int i = 0; i < mProgresses.length; ++i) {
            mRect.set(paddingLeft, paddingTop, paddingLeft + contentWidth * mProgresses[i], height - paddingBottom);
            canvas.drawRoundRect(mRect, mCornerRadiusPx, mCornerRadiusPx, mPaints[i]);
        }
    }

    private void invalidateProgress() {
        mProgresses[INDEX_LESSONS_COMPLETED] = Math.min(1f, mProgressValue);
        mProgresses[INDEX_BG] = 1;
        invalidate();
    }

    private void invalidatePaints() {
        final boolean antiAlias = true;
        final int totalLessonsAlpha = (int) (mTotalLessonsAlpha * 255);
        final int lessonsCompletedAlpha = (int) (mLessonsCompletedAlpha * 255);

        mLessonsCompletedPaint.setColor(mMainColor);
        mLessonsCompletedPaint.setAlpha(lessonsCompletedAlpha);
        mLessonsCompletedPaint.setAntiAlias(antiAlias);

        mLessonsTotalPaint.setColor(mMainColor);
        mLessonsTotalPaint.setAlpha(totalLessonsAlpha);
        mLessonsTotalPaint.setAntiAlias(antiAlias);
        invalidate();
    }

    //..............................................................................................

    /**
     * Sets all progress variable at once.
     *
     * @param totalLessons     - total lessons number.
     * @param completedLessons - completed lessons.
     */
    public void setProgress(int totalLessons, int completedLessons) {
        mProgressValue = (float) completedLessons / totalLessons;
        invalidateProgress();
    }

    public void setProgress(int value) {
        mProgressValue = (float) value / 100;
        invalidateProgress();
    }

    public void setProgress(float value) {
        mProgressValue = value;
        invalidateProgress();
    }

    public float getLessonsCompletedAlpha() {
        return mLessonsCompletedAlpha;
    }

    public void setLessonsCompletedAlpha(float lessonsCompletedAlpha) {
        mLessonsCompletedAlpha = lessonsCompletedAlpha;
        invalidatePaints();
    }

    public float getTotalLessonsAlpha() {
        return mTotalLessonsAlpha;
    }

    public void setTotalLessonsAlpha(float totalLessonsAlpha) {
        mTotalLessonsAlpha = totalLessonsAlpha;
        invalidatePaints();
    }

    public int getMainColor() {
        return mMainColor;
    }

    public void setMainColor(int mainColor) {
        mMainColor = mainColor;
        invalidatePaints();
    }
}
