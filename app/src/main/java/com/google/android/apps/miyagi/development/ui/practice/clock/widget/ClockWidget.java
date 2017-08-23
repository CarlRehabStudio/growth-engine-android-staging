package com.google.android.apps.miyagi.development.ui.practice.clock.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.clock.ClockAnswerOption;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.practice.clock.FaceId;
import com.google.android.apps.miyagi.development.ui.practice.common.OnBtnSubmitUpdateListener;

import com.tsengvn.typekit.Typekit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerzyw on 30.11.2016.
 */

public class ClockWidget extends View {

    private static final double DEG2RAD = Math.PI / 180;
    private static final double RAD2DEG = 180 / Math.PI;
    private static final float INDICATOR_STROKE_WIDTH_SCALE_FACTOR = 0.7f;
    //.................................................................... Attributes default values
    private static final int DEFAULT_CLOCK_STROKE_COLOR = 0xFF26A69A;
    private static final int DEFAULT_CLOCK_STROKE_WIDTH_DP = 20;

    private static final int DEFAULT_CLOCK_BG_COLOR = 0xFFB2DFDB;

    private static final float DEFAULT_POINTER_STROKE_WIDTH_DP = 13;
    private static final int DEFAULT_POINTER_COLOR = 0xEF5350;
    private static final float DEFAULT_POINTER_CIRCLE_RADIUS = 30;
    private static final int DEFAULT_POINTER_DOT_RADIUS_DP = 2;
    private static final int DEFAULT_POINTER_DOT_STROKE_WIDTH = 2;

    private static final float DEFAULT_SCALE_INDICATOR_LENGTH_DP = 16;
    private static final float DEFAULT_LABEL_PADDING_DP = 6;

    private static final int DEFAULT_SHADOW_BLUR_RADIUS_DP = 5;
    private static final int DEFAULT_SHADOW_X_OFFSET_DP = 5;
    private static final int DEFAULT_SHADOW_Y_OFFSET_DP = 5;
    private static final float DEFAULT_SHADOW_ALPHA = 0.2f;

    private static final float DEFAULT_LABEL_TEXT_SIZE_SP = 18;
    //....................................................................................... Paints
    private Paint mBgPaint;
    private TextPaint mLabelPaint;
    private Paint mStrokePaint;
    private Paint mIndicatorStrokePaint;
    private Paint mPointerShadowPaint;
    private Paint mPointerPaint;
    private Paint mDotPaint;
    //....................................................................................... Radius
    private float mCircleRadius;
    private float mLabelCircleRadius;
    private float mLongestLabelWidth;
    private float mScaleIndicatorRadius;
    //..................................................................... attributes actual values
    private int mPointerCircleRadiusPx;
    private int mPointerStrokeWidthPx;
    private int mPointerDotRadiusPx;
    private int mStrokeColor;
    private int mPointerColor;
    private int mStrokeWidthPx;
    private float mScaleIndicatorLengthPx;
    private int mLabelTextSize;
    private int mLabelPadding;

    private int mFaceMaxWidth;
    private int mShadowPixelOffsetXPx;
    private int mShadowPixelOffsetYPx;
    private int mShadowBlurRadiusPx;
    private int mBgColor;
    private int mShadowAlpha;

    private SparseArray<Drawable> mFaceDrawables;
    private List<SnapPoint> mSnapPoints = new ArrayList<>();
    private float mCircleX;
    private float mCircleY;
    private double mAngle2CurrentTouch;
    private int mFaceMaxHeight;
    private SnapPoint mCurrentSnapPoint;
    private int mStartSnapPointId;

    private OnBtnSubmitUpdateListener mBtnSubmitUpdateListener;

    public ClockWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClockWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClockWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.ClockWidget,
                0, 0);
        try {
            readAttributes(context, attributes);
        } finally {
            attributes.recycle();
        }
        createPaints(context);

        mFaceDrawables = new SparseArray<>();
        FaceId[] allFaces = FaceId.values();
        FaceId currFace;
        Drawable faceDrawable;
        mFaceMaxWidth = 0;
        mFaceMaxHeight = 0;
        for (int i = 0; i < allFaces.length; ++i) {
            currFace = allFaces[i];
            faceDrawable = ContextCompat.getDrawable(context, currFace.getFaceDrawableId());
            faceDrawable.setBounds(0,
                    0,
                    faceDrawable.getIntrinsicWidth(),
                    faceDrawable.getIntrinsicHeight()
            );
            mFaceMaxWidth = Math.max(mFaceMaxWidth, faceDrawable.getIntrinsicWidth());
            mFaceMaxHeight = Math.max(mFaceMaxHeight, faceDrawable.getIntrinsicHeight());
            mFaceDrawables.append(currFace.getApiId(), faceDrawable);
        }

    }

    private void readAttributes(Context context, TypedArray attributes) {
        mStrokeWidthPx = ViewUtils.dp2px(context, DEFAULT_CLOCK_STROKE_WIDTH_DP);
        mStrokeWidthPx = attributes.getDimensionPixelSize(R.styleable.ClockWidget_strokeWidth, mStrokeWidthPx);
        mStrokeColor = attributes.getColor(R.styleable.ClockWidget_strokeColor, DEFAULT_CLOCK_STROKE_COLOR);

        mBgColor = attributes.getColor(R.styleable.ClockWidget_strokeColor, DEFAULT_CLOCK_BG_COLOR);

        mPointerStrokeWidthPx = ViewUtils.dp2px(context, DEFAULT_POINTER_STROKE_WIDTH_DP);
        mPointerStrokeWidthPx = (int) attributes.getDimension(R.styleable.ClockWidget_pointerThickness, mPointerStrokeWidthPx);
        mPointerColor = attributes.getColor(R.styleable.ClockWidget_pointerColor, DEFAULT_POINTER_COLOR);
        mPointerDotRadiusPx = ViewUtils.dp2px(context, DEFAULT_POINTER_DOT_RADIUS_DP);

        mPointerCircleRadiusPx = ViewUtils.dp2px(context, DEFAULT_POINTER_CIRCLE_RADIUS);
        mPointerCircleRadiusPx = attributes.getDimensionPixelSize(R.styleable.ClockWidget_pointerThickness, mPointerCircleRadiusPx);

        mScaleIndicatorLengthPx = ViewUtils.dp2px(context, DEFAULT_SCALE_INDICATOR_LENGTH_DP);
        mScaleIndicatorLengthPx = attributes.getDimensionPixelOffset(R.styleable.ClockWidget_scaleIndicatorLength, (int) mScaleIndicatorLengthPx);

        mLabelPadding = ViewUtils.dp2px(context, DEFAULT_LABEL_PADDING_DP);
        mLabelPadding = attributes.getDimensionPixelOffset(R.styleable.ClockWidget_labelPadding, mLabelPadding);

        mShadowPixelOffsetXPx = ViewUtils.dp2px(context, DEFAULT_SHADOW_X_OFFSET_DP);
        mShadowPixelOffsetXPx = attributes.getDimensionPixelOffset(R.styleable.ClockWidget_pointerShadowOffsetX, mShadowPixelOffsetXPx);
        mShadowPixelOffsetYPx = ViewUtils.dp2px(context, DEFAULT_SHADOW_Y_OFFSET_DP);
        mShadowPixelOffsetYPx = attributes.getDimensionPixelOffset(R.styleable.ClockWidget_pointerShadowOffsetY, mShadowPixelOffsetYPx);

        mShadowBlurRadiusPx = ViewUtils.dp2px(context, DEFAULT_SHADOW_BLUR_RADIUS_DP);
        mShadowBlurRadiusPx = attributes.getDimensionPixelSize(R.styleable.ClockWidget_shadowBlurRadius, mShadowBlurRadiusPx);
        mShadowAlpha = (int) (attributes.getFloat(R.styleable.ClockWidget_shadowAlpha, DEFAULT_SHADOW_ALPHA) * 255);
        mLabelTextSize = (int) ViewUtils.sp2px(context, DEFAULT_LABEL_TEXT_SIZE_SP);
        mLabelTextSize = (int) attributes.getDimension(R.styleable.ClockWidget_labelTextSize, mLabelTextSize);
    }

    private void createPaints(Context context) {
        final int noAlpha = 255;
        // ...
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAlpha(noAlpha);
        mBgPaint.setStyle(Paint.Style.FILL);
        // ...
        mStrokePaint = new Paint();
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setAlpha(noAlpha);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidthPx);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        // ...
        mIndicatorStrokePaint = new Paint();
        mIndicatorStrokePaint.setColor(mStrokeColor);
        mIndicatorStrokePaint.setAlpha(noAlpha);
        mIndicatorStrokePaint.setAntiAlias(true);
        mIndicatorStrokePaint.setStyle(Paint.Style.STROKE);
        mIndicatorStrokePaint.setStrokeWidth(INDICATOR_STROKE_WIDTH_SCALE_FACTOR * mStrokeWidthPx);
        mIndicatorStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        // ...
        mPointerPaint = new Paint();
        mPointerPaint.setColor(mPointerColor);
        mPointerPaint.setAlpha(noAlpha);
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStyle(Paint.Style.STROKE);
        mPointerPaint.setStrokeWidth(mPointerStrokeWidthPx);
        mPointerPaint.setStrokeCap(Paint.Cap.ROUND);

        mDotPaint = new Paint();
        mDotPaint.setColor(Color.BLACK);
        mDotPaint.setAlpha(noAlpha);
        mDotPaint.setAntiAlias(true);
        mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint.setStrokeWidth(DEFAULT_POINTER_DOT_STROKE_WIDTH);
        mDotPaint.setStrokeCap(Paint.Cap.ROUND);
        // ...
        //shadow blur effect will work only in software mode
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // ...
        mPointerShadowPaint = new Paint();
        mPointerShadowPaint.setColor(Color.BLACK);
        mPointerShadowPaint.setAlpha(mShadowAlpha);
        mPointerShadowPaint.setAntiAlias(true);
        mPointerShadowPaint.setStyle(Paint.Style.STROKE);
        mPointerShadowPaint.setStrokeWidth(mPointerStrokeWidthPx);
        mPointerShadowPaint.setStrokeCap(Paint.Cap.ROUND);
        mPointerShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowBlurRadiusPx, BlurMaskFilter.Blur.NORMAL));
        // ...
        mLabelPaint = new TextPaint();
        mLabelPaint.setTextSize(mLabelTextSize);
        mLabelPaint.setColor(mStrokeColor);
        mLabelPaint.setAlpha(noAlpha);
        mLabelPaint.setTypeface(Typekit.getInstance().get("Roboto-Medium.ttf"));
        mLabelPaint.setFakeBoldText(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        float currX = event.getX() - mCircleX;
        float currY = event.getY() - mCircleY;
        mAngle2CurrentTouch = Math.atan2(currY, currX) * RAD2DEG + 90;
        if (mAngle2CurrentTouch < 0) {
            mAngle2CurrentTouch += 360;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final float paddingLeft = getPaddingLeft();
        final float paddingTop = getPaddingTop();
        final float horizontalPadding = (float) (getPaddingLeft() + getPaddingRight());
        final float verticalPadding = (float) (getPaddingTop() + getPaddingBottom());

        final float circleAreaWidth = w - horizontalPadding;
        final float circleAreaHeight = h - verticalPadding;

        float strokeHalfWidth = (float) Math.ceil(mStrokeWidthPx * 0.5f);
        mCircleRadius = Math.min(circleAreaWidth, circleAreaHeight) * 0.5f - strokeHalfWidth;
        mScaleIndicatorRadius = mCircleRadius - mScaleIndicatorLengthPx;
        //distance from center of clock to center of label
        mLabelCircleRadius = mScaleIndicatorRadius - mLongestLabelWidth * 0.5f - strokeHalfWidth - mLabelPadding;

        mCircleX = paddingLeft + circleAreaWidth * 0.5f;
        mCircleY = paddingTop + circleAreaHeight * 0.5f;
        float faceAreaSize = mLabelCircleRadius * 2;
        //if faces are to big scale them down (fit inside)
        float scaleX = faceAreaSize / mFaceMaxWidth;
        float scaleY = faceAreaSize / mFaceMaxWidth;
        float drawableScale = Math.min(scaleX, scaleY);
        if (drawableScale < 1) {
            //resize faces if needed
            Drawable faceDrawable;
            int size = mFaceDrawables.size();
            for (int i = 0; i < size; ++i) {
                faceDrawable = mFaceDrawables.valueAt(i);
                faceDrawable.setBounds(
                        0,
                        0,
                        (int) (faceDrawable.getIntrinsicWidth() * drawableScale),
                        (int) (faceDrawable.getIntrinsicHeight() * drawableScale));

            }
        }
    }

    /**
     * Set practice options available for user.
     */
    public void setOptions(List<ClockAnswerOption> options) {
        mSnapPoints.clear();
        mLongestLabelWidth = 0;
        mCurrentSnapPoint = null;
        mAngle2CurrentTouch = 0;
        mLongestLabelWidth = 0;
        if (options == null && options.isEmpty()) {
            invalidate();
            return;
        }
        float textWidth;
        SnapPoint snapPoint;
        for (ClockAnswerOption option : options) {
            snapPoint = new SnapPoint(option);
            mSnapPoints.add(snapPoint);
            textWidth = mLabelPaint.measureText(snapPoint.getLabel());
            mLongestLabelWidth = Math.max(textWidth, mLongestLabelWidth);
        }
        mCurrentSnapPoint = mSnapPoints.get(0);
        mStartSnapPointId = mCurrentSnapPoint.getOption().getId();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int pointsNum = mSnapPoints.size();
        if (pointsNum == 0) {
            canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mBgPaint);
            return;
        }
        final float sectorAngleSize = 360 / pointsNum;
        //0 angle point on the top
        final float startAngleOffset = -90;
        SnapPoint snapPoint;
        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mBgPaint);
        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mStrokePaint);

        float currentAngle = startAngleOffset;
        float currentAngleRad;
        float textHalfWidth;
        final float textHalfHeight = (mLabelPaint.descent() + mLabelPaint.ascent()) * 0.5f;
        float currX;
        float currX2;
        float currY;
        float currY2;
        float vecX;
        float vecY;
        String label;
        for (int i = 0; i < pointsNum; ++i) {
            snapPoint = mSnapPoints.get(i);
            //... calculate sin, cos for this angle (normalized direction vector)
            currentAngleRad = (float) (currentAngle * DEG2RAD);
            vecX = (float) Math.sin(currentAngleRad);
            vecY = (float) Math.cos(currentAngleRad);
            //... draw scale indicator for this point
            currX = mCircleX + vecY * mScaleIndicatorRadius;
            currY = mCircleY + vecX * mScaleIndicatorRadius;
            currX2 = mCircleX + vecY * mCircleRadius;
            currY2 = mCircleY + vecX * mCircleRadius;
            canvas.drawLine(currX, currY, currX2, currY2, mIndicatorStrokePaint);
            //.. draw label
            currX = mCircleX + vecY * mLabelCircleRadius;
            currY = mCircleY + vecX * mLabelCircleRadius;
            label = snapPoint.getLabel();
            textHalfWidth = mLabelPaint.measureText(label) * 0.5f;
            canvas.drawText(label, currX - textHalfWidth, currY - textHalfHeight, mLabelPaint);
            //... angle for next point
            currentAngle += sectorAngleSize;
        }
        //... draw pointer and face
        int selectedSectorIndex = (int) Math.round(mAngle2CurrentTouch / sectorAngleSize);
        selectedSectorIndex = selectedSectorIndex % pointsNum;
        //... draw face
        updateCurrentSnapPoint(mSnapPoints.get(selectedSectorIndex));

        Drawable faceDrawable = mFaceDrawables.get(mCurrentSnapPoint.getFaceId());
        if (faceDrawable != null) {
            canvas.save();
            Rect bounds = faceDrawable.getBounds();
            canvas.translate(mCircleX - bounds.width() * 0.5f, mCircleY - bounds.height() * 0.5f);
            faceDrawable.draw(canvas);
            canvas.restore();
        }
        //draw pointer and its shadow
        float angleToSnap = -sectorAngleSize * selectedSectorIndex + 180;
        drawPointer(angleToSnap, canvas);
    }

    private void updateCurrentSnapPoint(SnapPoint snapPoint) {
        if (!mCurrentSnapPoint.equals(snapPoint)) {
            mCurrentSnapPoint = snapPoint;
            if (mStartSnapPointId == mCurrentSnapPoint.getOption().getId()) {
                mBtnSubmitUpdateListener.enable(false);
            } else {
                mBtnSubmitUpdateListener.enable(true);
            }
        }
    }

    private void drawPointer(float angle, Canvas canvas) {
        final float pointerAngleRad = (float) (angle * DEG2RAD);
        float vecX = (float) Math.sin(pointerAngleRad);
        float vecY = (float) Math.cos(pointerAngleRad);
        //... first line
        float lineLength = (mLabelCircleRadius - mPointerCircleRadiusPx);
        float currX2 = mCircleX + vecX * lineLength;
        float currY2 = mCircleY + vecY * lineLength;
        //... circle
        float currX3 = mCircleX + vecX * mLabelCircleRadius;
        float currY3 = mCircleY + vecY * mLabelCircleRadius;
        //... second line
        lineLength = (mLabelCircleRadius + mPointerCircleRadiusPx);
        float currX4 = mCircleX + vecX * lineLength;
        float currY4 = mCircleY + vecY * lineLength;
        lineLength = mCircleRadius;
        float currX5 = mCircleX + vecX * lineLength;
        float currY5 = mCircleY + vecY * lineLength;
        //... shadow
        canvas.drawLine(mCircleX + mShadowPixelOffsetXPx,
                mCircleY + mShadowPixelOffsetYPx,
                currX2 + mShadowPixelOffsetXPx,
                currY2 + mShadowPixelOffsetYPx,
                mPointerShadowPaint);
        canvas.drawCircle(currX3 + mShadowPixelOffsetXPx,
                currY3 + mShadowPixelOffsetYPx,
                mPointerCircleRadiusPx,
                mPointerShadowPaint);
        canvas.drawLine(currX4 + mShadowPixelOffsetXPx,
                currY4 + mShadowPixelOffsetYPx,
                currX5 + mShadowPixelOffsetXPx,
                currY5 + mShadowPixelOffsetYPx,
                mPointerShadowPaint);
        //.. pointer
        canvas.drawLine(mCircleX, mCircleY, currX2, currY2, mPointerPaint);
        canvas.drawCircle(currX3, currY3, mPointerCircleRadiusPx, mPointerPaint);
        canvas.drawLine(currX4, currY4, currX5, currY5, mPointerPaint);
        canvas.drawCircle(mCircleX, mCircleY, mPointerDotRadiusPx, mDotPaint);
    }

    @Nullable
    public ClockAnswerOption getCurrentAnswer() {
        return mCurrentSnapPoint == null ? null : mCurrentSnapPoint.getOption();
    }

    public void setBtnSubmitUpdateListener(OnBtnSubmitUpdateListener listener) {
        mBtnSubmitUpdateListener = listener;
    }

    /**
     * Sets option.
     *
     * @param currentOption the current option.
     */
    public void setCurrentOption(String currentOption) {
        post(() -> {
            for (int i = 0; i < mSnapPoints.size(); i++) {
                SnapPoint snapPoint = mSnapPoints.get(i);
                if (Integer.toString(snapPoint.getOption().getId()).equals(currentOption)) {
                    updateCurrentSnapPoint(snapPoint);
                    mAngle2CurrentTouch = i * 360 / (float) mSnapPoints.size();
                    invalidate();
                    break;
                }
            }
        });
    }

    private static class SnapPoint {

        private final ClockAnswerOption mOption;

        public SnapPoint(ClockAnswerOption option) {
            mOption = option;
        }

        public String getLabel() {
            return mOption.getValue();
        }

        public int getFaceId() {
            return mOption.getFaceId();
        }

        public ClockAnswerOption getOption() {
            return mOption;
        }
    }
}
