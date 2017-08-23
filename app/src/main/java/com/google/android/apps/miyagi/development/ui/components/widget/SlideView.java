package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.apps.miyagi.development.R;

import org.parceler.Parcel;


/**
 * Created by lukaszweglinski on 17.11.2016.
 */
public class SlideView extends FrameLayout {

    private static final int DEFAULT_LEFT_COLOR = 0xFFFF8A80;
    private static final int DEFAULT_ACTIVE_LEFT_COLOR = 0xFFFFCDD2;
    private static final int DEFAULT_RIGHT_COLOR = 0xFF9CCC65;
    private static final int DEFAULT_ACTIVE_RIGHT_COLOR = 0xFFDCEDC8;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_SIZE_DP = 12;
    private static final int DEFAULT_TEXT_PADDING_DP = 16;
    private static final int DEFAULT_IMAGE_WIDTH_DP = 40;
    private static final int DEFAULT_ARROW_WIDTH_DP = 20;
    private static final long DEFAULT_ANIMATION_DURATION = 300L;

    private int mTouchSlop;
    private float mCurrentX;
    private boolean mIsDragging;

    private ImageView mLeftImageView;
    private ImageView mRightImageView;

    private StaticLayout mTextLayout;
    private TextPaint mTextPaint;
    private Paint mLeftPaint;
    private Paint mRightPaint;
    private Path mLeftPath;
    private Path mRightPath;

    private State mState = State.IDLE;

    private int mTextHeight;
    private int mTextWidth;
    private float mScale;

    private int mTouchDownX;

    private String mText;
    private float mTextPaddingVertical;
    private float mTextPaddingHorizontal;

    private int mLeftColor;
    private int mLeftActiveColor;
    private int mRightColor;
    private int mRightActiveColor;

    private int mDefaultArrowWidth;
    private int mImageWidth;

    private ValueAnimator mCurrentAnimator;
    private Animator.AnimatorListener mCurrentAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mCurrentAnimator = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private OnStateChange mOnStateChangeListener;

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public static void setTintDrawable(ImageView imageView, @ColorInt int color) {
        imageView.setColorFilter(color);
    }

    public void setOnStateChangeListener(OnStateChange listener) {
        mOnStateChangeListener = listener;
    }

    void init(Context context, AttributeSet attrs) {

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
        int drawableLeftResId;
        int drawableRightResId;
        int textColor;
        float textSize;
        try {
            mText = arr.getString(R.styleable.SlideView_text);
            textColor = arr.getColor(R.styleable.SlideView_textColor, DEFAULT_TEXT_COLOR);
            textSize = arr.getDimension(R.styleable.SlideView_textSize, dp2px(DEFAULT_TEXT_SIZE_DP));
            mTextPaddingVertical = arr.getDimension(R.styleable.SlideView_textPaddingVertical, dp2px(DEFAULT_TEXT_PADDING_DP));
            mTextPaddingHorizontal = arr.getDimension(R.styleable.SlideView_textPaddingHorizontal, dp2px(DEFAULT_TEXT_PADDING_DP));
            mLeftColor = arr.getColor(R.styleable.SlideView_leftColor, DEFAULT_LEFT_COLOR);
            mLeftActiveColor = arr.getColor(R.styleable.SlideView_leftColor, DEFAULT_ACTIVE_LEFT_COLOR);
            mRightColor = arr.getColor(R.styleable.SlideView_leftColor, DEFAULT_RIGHT_COLOR);
            mRightActiveColor = arr.getColor(R.styleable.SlideView_leftColor, DEFAULT_ACTIVE_RIGHT_COLOR);
            mImageWidth = (int) arr.getDimension(R.styleable.SlideView_imageWidth, dp2px(DEFAULT_IMAGE_WIDTH_DP));
            drawableLeftResId = arr.getResourceId(R.styleable.SlideView_drawableLeft, 0);
            drawableRightResId = arr.getResourceId(R.styleable.SlideView_drawableRight, 0);
            mDefaultArrowWidth = (int) dp2px(DEFAULT_ARROW_WIDTH_DP);
        } finally {
            arr.recycle();
        }

        mLeftImageView = new ImageView(context);
        mRightImageView = new ImageView(context);

        LayoutParams imageParam = new LayoutParams(mImageWidth, LayoutParams.MATCH_PARENT);

        mLeftImageView.setImageResource(drawableLeftResId);
        mLeftImageView.setBackgroundColor(mLeftColor);
        mLeftImageView.setLayoutParams(imageParam);
        mLeftImageView.setScaleType(ImageView.ScaleType.CENTER);

        mRightImageView.setImageResource(drawableRightResId);
        mRightImageView.setBackgroundColor(mRightColor);
        mRightImageView.setLayoutParams(imageParam);
        mRightImageView.setScaleType(ImageView.ScaleType.CENTER);

        mLeftImageView.setOnTouchListener((v, event) -> mState != State.LEFT_ACTIVE && SlideView.this.handleLeft(event));
        mRightImageView.setOnTouchListener((v, event) -> mState != State.RIGHT_ACTIVE && SlideView.this.handleRight(event));

        addView(mLeftImageView);
        addView(mRightImageView);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftPaint.setColor(DEFAULT_ACTIVE_LEFT_COLOR);
        mLeftPaint.setStyle(Paint.Style.FILL);

        mRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRightPaint.setColor(DEFAULT_ACTIVE_RIGHT_COLOR);
        mRightPaint.setStyle(Paint.Style.FILL);

        mLeftPath = new Path();
        mRightPath = new Path();

        setWillNotDraw(false);
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mTextLayout = getTextLayout();
        mTextHeight = mTextLayout.getHeight();

        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mTextHeight > height) {
            height = (int) (mTextHeight + mTextPaddingVertical);
            if (getMinimumHeight() > height) {
                height = getMinimumHeight();
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, height);
        }

        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mLeftImageView.layout(left, 0, mLeftImageView.getMeasuredWidth(), getMeasuredHeight());
        mRightImageView.layout(right - mRightImageView.getMeasuredWidth(), 0, right, getMeasuredHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        switch (mState) {
            case IDLE:
                break;
            case LEFT:
            case LEFT_ACTIVE:
                drawLeft(canvas);
                break;
            case RIGHT:
            case RIGHT_ACTIVE:
                drawRight(canvas);
                break;
            default:
                break;
        }
        drawLabel(canvas);
    }

    private void drawLeft(Canvas canvas) {
        canvas.save();
        canvas.translate(mRightImageView.getX(), mRightImageView.getY());
        mRightImageView.draw(canvas);
        canvas.restore();

        int startPos = (int) (mCurrentX + mLeftImageView.getMeasuredWidth());
        int endPos = 0;
        int topPos = 0;
        int bottomPos = getMeasuredHeight();

        mLeftPath.reset();
        mLeftPath.moveTo(endPos, topPos);
        mLeftPath.lineTo(startPos, topPos);
        mLeftPath.lineTo(startPos + mDefaultArrowWidth, bottomPos / 2);
        mLeftPath.lineTo(startPos, bottomPos);
        mLeftPath.lineTo(endPos, bottomPos);
        mLeftPath.lineTo(endPos, topPos);
        canvas.drawPath(mLeftPath, mLeftPaint);

        mLeftImageView.draw(canvas);
    }

    private void drawRight(Canvas canvas) {
        mLeftImageView.draw(canvas);

        int startPos = (int) (mCurrentX - mLeftImageView.getMeasuredWidth());
        int endPos = getMeasuredWidth();
        int topPos = 0;
        int bottomPos = getMeasuredHeight();

        mRightPath.reset();
        mRightPath.moveTo(endPos, topPos);
        mRightPath.lineTo(startPos, topPos);
        mRightPath.lineTo(startPos - mDefaultArrowWidth, bottomPos / 2);
        mRightPath.lineTo(startPos, bottomPos);
        mRightPath.lineTo(getMeasuredWidth(), bottomPos);
        mRightPath.lineTo(getMeasuredWidth(), bottomPos);
        canvas.drawPath(mRightPath, mRightPaint);

        canvas.save();
        canvas.translate(mRightImageView.getX(), mRightImageView.getY());
        mRightImageView.draw(canvas);
        canvas.restore();
    }

    private void drawLabel(Canvas canvas) {
        float posX = (getWidth() - mTextWidth) / 2;
        float posY = (getHeight() - mTextHeight) / 2;

        canvas.save();
        canvas.translate(posX, posY);
        mTextLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (mState == State.LEFT_ACTIVE || mState == State.RIGHT_ACTIVE) {
                    if (!mIsDragging) {
                        animateToIdle();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean handleLeft(MotionEvent event) {
        if (mState == State.IDLE) {
            mState = State.LEFT;
        }
        return handleTouch(event, 0);
    }

    private boolean handleRight(MotionEvent event) {
        if (mState == State.IDLE) {
            mState = State.RIGHT;
        }
        return handleTouch(event, mRightImageView.getLeft());
    }

    private boolean handleTouch(MotionEvent event, int startX) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = startX + Math.round(event.getX());
                onTouchDown();
                trackTouchEvent(event, startX);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    trackTouchEvent(event, startX);
                } else {
                    final float x = startX + Math.round(event.getX());
                    if (Math.abs(x - mTouchDownX) > mTouchSlop) {
                        startDrag();
                        trackTouchEvent(event, startX);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mIsDragging) {
                    trackTouchEvent(event, startX);
                    stopDrag();
                } else {
                    switch (mState) {
                        case LEFT:
                            animateToLeftActive();
                            break;
                        case RIGHT:
                            animateToRightActive();
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                return true;
        }
        return true;
    }

    private void trackTouchEvent(MotionEvent event, int startX) {
        final int x = startX + Math.round(event.getX());
        final int width = getWidth();
        final int availableWidth = width - getPaddingLeft() - getPaddingRight();

        mScale = (availableWidth - x + getPaddingLeft()) / (float) availableWidth;
        mCurrentX = x;
        invalidate();
    }

    private void onTouchDown() {
        if (mState == State.LEFT || mState == State.LEFT_ACTIVE) {
            mLeftImageView.setBackgroundColor(mLeftActiveColor);
            setTintDrawable(mLeftImageView, mLeftColor);
        } else if (mState == State.RIGHT || mState == State.RIGHT_ACTIVE) {
            mRightImageView.setBackgroundColor(mRightActiveColor);
            setTintDrawable(mRightImageView, mRightColor);
        }
        requestDisallowInterceptTouchEvent(true);
        invalidate();
    }

    private void startDrag() {
        mIsDragging = true;
    }

    private void stopDrag() {
        switch (mState) {
            case LEFT:
            case LEFT_ACTIVE:
                if (mScale < 0.5f) {
                    animateToLeftActive();
                } else {
                    animateToIdle();
                }
                break;
            case RIGHT:
            case RIGHT_ACTIVE:
                if (mScale > 0.5f) {
                    animateToRightActive();
                } else {
                    animateToIdle();
                }
                break;
            default:
                break;
        }
        mIsDragging = false;
    }

    private void reset() {
        mState = State.IDLE;
        mOnStateChangeListener.change(mState);
        mIsDragging = false;
        mCurrentX = 0;
        mLeftImageView.setBackgroundColor(mLeftColor);
        mRightImageView.setBackgroundColor(mRightColor);
        setTintDrawable(mLeftImageView, Color.WHITE);
        setTintDrawable(mRightImageView, Color.WHITE);
    }

    /**
     * Sets active state.
     */
    public void setState(State state) {
        post(() -> {
            mState = state;
            if (mState == State.RIGHT_ACTIVE) {
                mCurrentX = 0;
                mTouchDownX = 0;
            } else if (mState == State.LEFT_ACTIVE) {
                mCurrentX = getWidth();
                mTouchDownX = getWidth();
            }
            onTouchDown();
        });
    }

    /**
     * Sets text.
     *
     * @param text the text to set
     */
    public void setText(String text) {
        mText = text;
        requestLayout();
    }

    private StaticLayout getTextLayout() {
        mTextWidth = ((int) (getMeasuredWidth() - mImageWidth * 2 - mTextPaddingHorizontal));
        if (mTextWidth <= 0) {
            //fix for crash when mTextWidth < 0
            mTextWidth = getMeasuredWidth();
        }

        if (mText != null) {
            mTextLayout = new StaticLayout(mText, mTextPaint, mTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        } else {
            mTextLayout = new StaticLayout("", mTextPaint, mTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        }

        mTextHeight = (int) (mTextLayout.getHeight() + mTextPaddingHorizontal);
        return mTextLayout;
    }

    private void animateToIdle() {
        AnimateToIdleAction action = new AnimateToIdleAction();
        postOnAnimation(action);
    }


    private void animateToLeftActive() {
        AnimateToLeftActiveAction action = new AnimateToLeftActiveAction();
        postOnAnimation(action);
    }

    private void animateToRightActive() {
        AnimateToRightActiveAction action = new AnimateToRightActiveAction();
        postOnAnimation(action);
    }

    private void clearOldAnimationIfExists() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.removeAllListeners();
            mCurrentAnimator.cancel();
        }
    }

    @Parcel
    public enum State {
        LEFT, LEFT_ACTIVE, RIGHT, RIGHT_ACTIVE, IDLE
    }

    public interface OnStateChange {
        void change(State state);
    }

    private class AnimateToIdleAction implements Runnable {
        @Override
        public void run() {
            clearOldAnimationIfExists();

            if (mState == State.LEFT || mState == State.LEFT_ACTIVE) {
                mCurrentAnimator = ValueAnimator.ofFloat(mCurrentX, 0);
            } else {
                mCurrentAnimator = ValueAnimator.ofFloat(mCurrentX, getWidth());
            }

            mCurrentAnimator.setDuration(DEFAULT_ANIMATION_DURATION);

            mCurrentAnimator.setInterpolator(new AccelerateInterpolator());
            mCurrentAnimator.addUpdateListener(animation -> {
                mCurrentX = (float) animation.getAnimatedValue();
                invalidate();
            });

            mCurrentAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    reset();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }

    private class AnimateToLeftActiveAction implements Runnable {
        @Override
        public void run() {
            clearOldAnimationIfExists();

            mCurrentAnimator = ValueAnimator.ofFloat(mCurrentX, getWidth());
            mCurrentAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
            mCurrentAnimator.setInterpolator(new AccelerateInterpolator());

            mCurrentAnimator.addUpdateListener(animation -> {
                mCurrentX = (float) animation.getAnimatedValue();
                invalidate();
            });

            mCurrentAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mState = State.LEFT_ACTIVE;
                    mOnStateChangeListener.change(mState);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }

    private class AnimateToRightActiveAction implements Runnable {
        @Override
        public void run() {
            clearOldAnimationIfExists();

            mCurrentAnimator = ValueAnimator.ofFloat(mCurrentX, 0);
            mCurrentAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
            mCurrentAnimator.setInterpolator(new AccelerateInterpolator());

            mCurrentAnimator.addUpdateListener(animation -> {
                mCurrentX = (float) animation.getAnimatedValue();
                invalidate();
            });

            mCurrentAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mState = State.RIGHT_ACTIVE;
                    mOnStateChangeListener.change(mState);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mCurrentAnimator.addListener(mCurrentAnimatorListener);
            mCurrentAnimator.start();
        }
    }
}
