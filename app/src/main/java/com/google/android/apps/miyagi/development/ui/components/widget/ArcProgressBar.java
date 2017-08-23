package com.google.android.apps.miyagi.development.ui.components.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

import com.tsengvn.typekit.Typekit;

import static android.graphics.Typeface.DEFAULT_BOLD;
import static com.google.android.apps.miyagi.development.helpers.ViewUtils.dp2px;

/**
 * Created by lukaszweglinski on 28.11.2016.
 */

public class ArcProgressBar extends View {

    private static final int DEFAULT_FOREGROUND_COLOR = 0xFFFFFF;
    private static final int DEFAULT_BACKGROUND_COLOR = 0x486ab0;
    private static final int DEFAULT_TEXT_SIZE_SP = 18;
    private static final int DEFAULT_TEXT_MINUS_PADDING_DP = 5;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFF;
    private static final int DEFAULT_SUFFIX_TEXT_SIZE_SP = 15;
    private static final int DEFAULT_SUFFIX_PADDING_DP = 4;
    private static final int DEFAULT_BOTTOM_TEXT_SIZE_SP = 10;
    private static final int DEFAULT_BOTTOM_TEXT_HORIZONTAL_PADDING_DP = 4;
    private static final int DEFAULT_BOTTOM_TEXT_VERTICAL_PADDING_DP = 10;
    private static final String DEFAULT_SUFFIX_TEXT = "%";
    private static final int DEFAULT_STROKE_WIDTH_DP = 4;
    private static final int DEFAULT_MAX = 100;
    private static final float DEFAULT_ARC_ANGLE = 360 * 0.8f;

    private TextPaint mTextPaint;
    private TextPaint mSuffixTextPaint;
    private TextPaint mBottomTextPaint;
    private Paint mArcPaint;
    private RectF mRectF = new RectF();

    private float mStrokeWidth;
    private float mSuffixTextSize;
    private float mBottomTextSize;
    private String mBottomText;
    private float mTextSize;
    private float mTextMinusPadding;
    private int mTextColor;
    private int mProgress = 0;
    private int mMax;
    private int mForegroundStrokeColor;
    private int mBackgroundStrokeColor;
    private float mArcAngle;
    private float mFinalArcAngle;
    private String mSuffixText;
    private float mSuffixTextPadding;
    private float mBottomTextHorizontalPadding;
    private float mBottomTextVerticalPadding;

    private StaticLayout mBottomTextLayout;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Arc progress bar.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        try {
            initByAttributes(attributes);
        } finally {
            attributes.recycle();
        }
        initPainters();
    }

    private void initByAttributes(TypedArray attributes) {
        mForegroundStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_finished_color, DEFAULT_FOREGROUND_COLOR);
        mBackgroundStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_unfinished_color, DEFAULT_BACKGROUND_COLOR);
        mTextColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, DEFAULT_TEXT_COLOR);
        mTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_text_size, ViewUtils.sp2px(getContext(), DEFAULT_TEXT_SIZE_SP));
        mTextMinusPadding = ViewUtils.dp2px(getContext(),DEFAULT_TEXT_MINUS_PADDING_DP);
        mArcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, DEFAULT_ARC_ANGLE);
        mFinalArcAngle = attributes.getFloat(R.styleable.ArcProgress_final_arc_angle, DEFAULT_ARC_ANGLE);
        setMax(attributes.getInt(R.styleable.ArcProgress_arc_max, DEFAULT_MAX));
        setProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
        mStrokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, dp2px(getContext(), DEFAULT_STROKE_WIDTH_DP));
        mSuffixTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_size, ViewUtils.sp2px(getContext(), DEFAULT_SUFFIX_TEXT_SIZE_SP));
        mSuffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgress_arc_suffix_text)) ? DEFAULT_SUFFIX_TEXT : attributes.getString(R.styleable.ArcProgress_arc_suffix_text);
        mSuffixTextPadding = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_padding, dp2px(getContext(), DEFAULT_SUFFIX_PADDING_DP));
        mBottomTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_size, ViewUtils.sp2px(getContext(), DEFAULT_BOTTOM_TEXT_SIZE_SP));
        mBottomText = attributes.getString(R.styleable.ArcProgress_arc_bottom_text);
        mBottomTextHorizontalPadding = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_padding_horizontal, dp2px(getContext(), DEFAULT_BOTTOM_TEXT_HORIZONTAL_PADDING_DP));
        mBottomTextVerticalPadding = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_padding_vertical, dp2px(getContext(), DEFAULT_BOTTOM_TEXT_VERTICAL_PADDING_DP));
    }

    protected void initPainters() {
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(Typekit.getInstance().get("roboto_light"));

        mSuffixTextPaint = new TextPaint();
        mSuffixTextPaint.setColor(mTextColor);
        mSuffixTextPaint.setTextSize(mSuffixTextSize);
        mSuffixTextPaint.setAntiAlias(true);
        mSuffixTextPaint.setTypeface(DEFAULT_BOLD);

        mBottomTextPaint = new TextPaint();
        mBottomTextPaint.setColor(mTextColor);
        mBottomTextPaint.setTextSize(mBottomTextSize);
        mBottomTextPaint.setAntiAlias(true);
        mBottomTextPaint.setTypeface(Typekit.getInstance().get("roboto_regular"));

        mArcPaint = new Paint();
        mArcPaint.setColor(DEFAULT_BACKGROUND_COLOR);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(mStrokeWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(mStrokeWidth / 2f, mStrokeWidth / 2f, w - mStrokeWidth / 2f, h - mStrokeWidth / 2f);
        float radius = w / 2f;
        float angle = (360 - mFinalArcAngle) / 2f;

        int arcWidth = Math.max((int) ((2 * radius * Math.sin(Math.toRadians(angle))) - mBottomTextHorizontalPadding * 2), 0);
        mBottomTextLayout = new StaticLayout(getBottomText(), mBottomTextPaint, arcWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startAngle = 270 - mFinalArcAngle / 2f;
        mArcPaint.setColor(mBackgroundStrokeColor);
        canvas.drawArc(mRectF, startAngle, mArcAngle, false, mArcPaint);
        mArcPaint.setColor(mForegroundStrokeColor);

        float finishedSweepAngle = mProgress / (float) getMax() * mArcAngle;
        canvas.drawArc(mRectF, startAngle, finishedSweepAngle, false, mArcPaint);

        float textHeight = 0f;
        float textBaseline = 0f;

        String text = String.valueOf(getProgress());
        if (!TextUtils.isEmpty(text)) {
            float startTextPos = (getWidth() - mTextPaint.measureText(text) - mSuffixTextPaint.measureText(mSuffixText) - mSuffixTextPadding - mTextMinusPadding) / 2.0f;

            textHeight = mTextPaint.descent() + mTextPaint.ascent();
            textBaseline = (getHeight() - textHeight) / 2.3f;
            canvas.drawText(text, startTextPos, textBaseline, mTextPaint);
            canvas.drawText(mSuffixText, startTextPos + mSuffixTextPadding + mTextPaint.measureText(text), textBaseline, mSuffixTextPaint);
        }

        if (!TextUtils.isEmpty(getBottomText())) {
            float posX = (getWidth() - mBottomTextLayout.getWidth()) / 2;
            float posY = textBaseline + mBottomTextVerticalPadding;

            canvas.save();
            canvas.translate(posX, posY);
            mBottomTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        this.invalidate();
    }

    public float getSuffixTextSize() {
        return mSuffixTextSize;
    }

    public void setSuffixTextSize(float suffixTextSize) {
        this.mSuffixTextSize = suffixTextSize;
        this.invalidate();
    }

    public String getBottomText() {
        return mBottomText;
    }

    public void setBottomText(String bottomText) {
        this.mBottomText = bottomText;
        this.invalidate();
    }

    public int getProgress() {
        return mProgress;
    }


    /**
     * Sets value of progress.
     *
     * @param progress value of progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        if (this.mProgress > getMax()) {
            this.mProgress %= getMax();
        }
        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    /**
     * Sets max value of progress.
     *
     * @param max value of max progress.
     */
    public void setMax(int max) {
        if (max > 0) {
            this.mMax = max;
            invalidate();
        }
    }

    public float getBottomTextSize() {
        return mBottomTextSize;
    }

    public void setBottomTextSize(float bottomTextSize) {
        this.mBottomTextSize = bottomTextSize;
        this.invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        this.invalidate();
    }

    public int getForegroundStrokeColor() {
        return mForegroundStrokeColor;
    }

    public void setForegroundStrokeColor(int finishedStrokeColor) {
        this.mForegroundStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getBackgroundStrokeColor() {
        return mBackgroundStrokeColor;
    }

    public void setBackgroundStrokeColor(int unfinishedStrokeColor) {
        this.mBackgroundStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public float getArcAngle() {
        return mArcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.mArcAngle = arcAngle;
        this.invalidate();
    }

    public float getFinalArcAngle() {
        return mFinalArcAngle;
    }

    public void setFinalAngle(float arcAngle) {
        this.mFinalArcAngle = arcAngle;
        this.invalidate();
    }

    public String getSuffixText() {
        return mSuffixText;
    }

    public void setSuffixText(String suffixText) {
        this.mSuffixText = suffixText;
        this.invalidate();
    }

    public float getSuffixTextPadding() {
        return mSuffixTextPadding;
    }

    public void setSuffixTextPadding(float suffixTextPadding) {
        this.mSuffixTextPadding = suffixTextPadding;
        this.invalidate();
    }
}