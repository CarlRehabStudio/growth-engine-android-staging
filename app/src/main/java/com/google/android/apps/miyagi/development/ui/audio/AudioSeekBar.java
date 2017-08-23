package com.google.android.apps.miyagi.development.ui.audio;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

import static android.graphics.drawable.GradientDrawable.RADIAL_GRADIENT;

/**
 * Created by lukaszweglinski on 21.03.2017.
 */

public class AudioSeekBar extends View {

    private static final int DEFAULT_PROGRESS_COLOR = 0XFF3F80;
    private static final int DEFAULT_THUMB_SIZE_DP = 16;
    private static final int DEFAULT_THUMB_SHADOW_RADIUS_DP = 12;
    private static final int DEFAULT_THUMB_SHADOW_OFFSET_DP = 10;
    private static final int DEFAULT_TRACK_HEIGHT_DP = 8;

    private static final int DEFAULT_THUMB_TOUCH_PADDING_DP = 10;

    private int mThumbTouchPadding;
    private int mCornerRoundnessPx;

    private Paint mTrackPaint;
    private Paint mProgressPaint;
    private Paint mThumbPaint;

    private RectF mTrackBounds;
    private RectF mProgressAreaBounds;
    private RectF mThumbBounds;

    private int mThumbSize;
    private int mTrackHeight;

    private float mProgress;
    private AudioSeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private boolean mIsDragging;
    private float mTouchDownX;
    private float mScaledTouchSlop;
    private float mMax = 1.0f;
    private GradientDrawable mThumbShadow;
    private int mThumbShadowOffset;

    public AudioSeekBar(Context context) {
        this(context, null);
    }

    public AudioSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AudioSeekBar, 0, 0);

        try {
            mThumbTouchPadding = ViewUtils.dp2px(context, DEFAULT_THUMB_TOUCH_PADDING_DP);
            mThumbShadowOffset = ViewUtils.dp2px(context, DEFAULT_THUMB_SHADOW_OFFSET_DP);
            mThumbSize = attributes.getDimensionPixelSize(R.styleable.AudioSeekBar_thumbSize, ViewUtils.dp2px(context, DEFAULT_THUMB_SIZE_DP));
            mTrackHeight = attributes.getDimensionPixelSize(R.styleable.AudioSeekBar_trackHeight, ViewUtils.dp2px(context, DEFAULT_TRACK_HEIGHT_DP));
        } finally {
            attributes.recycle();
        }

        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setColor(Color.WHITE);
        mTrackPaint.setAlpha(70);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(DEFAULT_PROGRESS_COLOR);

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        final int START_COLOR = Color.parseColor("#55000000");
        final int END_COLOR = Color.parseColor("#00000000");
        int[] colors = new int[]{START_COLOR, END_COLOR};
        mThumbShadow = new GradientDrawable();
        mThumbShadow.setColor(Color.WHITE);
        mThumbShadow.setGradientType(RADIAL_GRADIENT);
        mThumbShadow.setGradientRadius(ViewUtils.dp2px(context, DEFAULT_THUMB_SHADOW_RADIUS_DP));
        mThumbShadow.setColors(colors);

        mTrackBounds = new RectF();
        mProgressAreaBounds = new RectF();
        mThumbBounds = new RectF();

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minHeight = getMinimumHeight();

        int dh = 0;
        dh = Math.max(minHeight, Math.max(mThumbSize, mTrackHeight));
        dh += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(widthMeasureSpec,
                resolveSizeAndState(dh, heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();

        mTrackBounds.set(paddingLeft, h / 2f - mTrackHeight / 2f, w - paddingRight, h / 2f + mTrackHeight / 2f);
        mProgressAreaBounds.set(mTrackBounds);
        mProgressAreaBounds.right = mProgressAreaBounds.left + mTrackBounds.width() * mProgress;

        mThumbBounds.set(paddingLeft, h / 2f - mThumbSize / 2f, paddingLeft + mThumbSize, h / 2f + mThumbSize / 2f);
        mThumbBounds.offsetTo(mProgressAreaBounds.right - mThumbBounds.width() / 2f, mThumbBounds.top);

        mThumbShadow.setBounds((int) mThumbBounds.left - mThumbShadowOffset,
                (int) mThumbBounds.top - mThumbShadowOffset,
                (int) mThumbBounds.right + mThumbShadowOffset,
                (int) mThumbBounds.bottom + mThumbShadowOffset);

        mCornerRoundnessPx = Math.round(mTrackBounds.height() * 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() + mThumbTouchPadding < mThumbBounds.left
                        || event.getX() - mThumbTouchPadding > mThumbBounds.right
                        || event.getY() - mThumbTouchPadding > mThumbBounds.bottom
                        || event.getY() + mThumbTouchPadding < mThumbBounds.top) {
                    return false;
                }
                if (isScrollContainer()) {
                    mTouchDownX = event.getX();
                } else {
                    startDrag(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    trackTouchEvent(event);
                } else {
                    final float x = event.getX();
                    if (Math.abs(x - mTouchDownX) > mScaledTouchSlop) {
                        startDrag(event);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate();
                break;
        }
        return true;
    }

    private void startDrag(MotionEvent event) {
        setPressed(true);

        onStartTrackingTouch();
        trackTouchEvent(event);
        getParent().requestDisallowInterceptTouchEvent(true);
    }

    private void trackTouchEvent(MotionEvent event) {
        final int x = Math.round(event.getX());
        final int width = getWidth();
        final int availableWidth = width - getPaddingLeft() - getPaddingRight();

        final float scale;
        float progress = 0.0f;
        if (x < getPaddingLeft()) {
            scale = 0.0f;
        } else if (x > width - getPaddingRight()) {
            scale = 1.0f;
        } else {
            scale = (x - getPaddingLeft()) / (float) availableWidth;
        }

        final float max = getMax();
        progress += scale * max;
        setProgressInternal(progress, true);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrack(canvas);
        drawThumb(canvas);
    }

    private void drawThumb(Canvas canvas) {
        mThumbShadow.draw(canvas);
        canvas.drawOval(mThumbBounds, mThumbPaint);
    }

    private void drawTrack(Canvas canvas) {
        canvas.drawRoundRect(mTrackBounds, mCornerRoundnessPx, mCornerRoundnessPx, mTrackPaint);
        canvas.drawRoundRect(mProgressAreaBounds, mCornerRoundnessPx, mCornerRoundnessPx, mProgressPaint);
    }

    /**
     * This is called when the user has started touching this widget.
     */
    void onStartTrackingTouch() {
        mIsDragging = true;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    void onStopTrackingTouch() {
        mIsDragging = false;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    private void setProgressInternal(float progress, boolean fromUser) {
        setProgress(progress);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, progress, fromUser);
        }
    }

    public void setTintColor(int color) {
        mProgressPaint.setColor(color);
        mThumbPaint.setColor(color);
        invalidate();
    }

    /**
     * Sets AudioSeekBar change listener.
     *
     * @param onSeekBarChangeListener AudioSeekBar change listener.
     */
    public void setOnSeekBarChangeListener(AudioSeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    /**
     * Gets max value.
     *
     * @return the max value.
     */
    public float getMax() {
        return mMax;
    }

    /**
     * Sets max value.
     *
     * @param max the max.
     */
    public void setMax(float max) {
        mMax = max;
    }

    /**
     * Gets progress.
     *
     * @return current seek bar progress.
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Sets progress.
     */
    public void setProgress(@FloatRange(from = 0.0, to = 1.0) float progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        mProgress = progress;
        mProgressAreaBounds.right = mProgressAreaBounds.left + mTrackBounds.width() * mProgress;
        mThumbBounds.offsetTo(mProgressAreaBounds.right - mThumbBounds.width() / 2f, mThumbBounds.top);

        mThumbShadow.setBounds((int) mThumbBounds.left - mThumbShadowOffset,
                (int) mThumbBounds.top - mThumbShadowOffset,
                (int) mThumbBounds.right + mThumbShadowOffset,
                (int) mThumbBounds.bottom + mThumbShadowOffset);
        invalidate();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);

        savedState.progress = mProgress;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setProgress(ss.progress);
    }

    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture as well as changes that were initiated
     * programmatically.
     */
    public interface OnSeekBarChangeListener {

        void onProgressChanged(AudioSeekBar seekBar, float progress, boolean fromUser);

        void onStartTrackingTouch(AudioSeekBar seekBar);

        void onStopTrackingTouch(AudioSeekBar seekBar);
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        float progress;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(progress);
        }
    }
}
