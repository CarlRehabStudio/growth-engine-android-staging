package com.google.android.apps.miyagi.development.ui.components.widget;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 09.12.2016.
 */
public class SelectRightView extends View {

    private static final int DEFAULT_STROKE_LENGTH_DP = 10;
    private static final int DEFAULT_STROKE_PADDING_DP = 4;
    private static final int DEFAULT_STROKE_WIDTH_DP = 2;
    private static final int DEFAULT_DRAWABLE_SIZE_DP = 64;
    private static final int DEFAULT_CORNER_RADIUS_DP = 6;
    private static final int DEFAULT_SHADOW_SIZE_DP = 6;
    private static final long SCALE_ANIMATION_DURATION = 150;
    private static final long COLOR_ANIMATION_DURATION = 150;
    private static final int INACTIVE_COLOR = 0xFFB2B2B2;
    private static final int ACTIVE_LEFT_COLOR = 0xFFFF8A80;
    private static final int ACTIVE_RIGHT_COLOR = 0xFF9CCC65;

    private Paint mDashPaint;
    private Paint mDashLeftPaint;
    private Paint mDashRightPaint;
    private Paint mShadowPaint;
    private Paint mUnderBigHandPaint;
    private Path mDashPath;
    private Path mDashLeftPath;
    private Path mDashRightPath;
    private RectF mDashRectLeft;
    private RectF mDashRectRight;
    private Rect mImageRect;
    private RectF mShadowRect;
    private Drawable mLeftHandDrawable;
    private Drawable mRightHandDrawable;
    private Drawable mLeftHandBigDrawable;
    private Drawable mRightHandBigDrawable;
    private Drawable mImage;
    private int mMaxContentWidth;
    private float mDashLength;
    private float mDashWidth;
    private int mDrawableSize;
    private int mCornerRadius;
    private boolean mDragging = false;
    private float mCurrentX;
    private int mTopPosition;
    private int mShadowSize;
    private int mBigImageAlpha = 0;
    private State mState = State.IDLE;
    private OnStateChange mOnStateChangeListener;

    private ValueAnimator mAlphaAnimation = null;
    private ValueAnimator mScaleAnimation = null;
    private ValueAnimator mLeftHandColorAnimator = null;
    private ValueAnimator mRightHandColorAnimator = null;
    private ValueAnimator mLeftDashAnimator = null;
    private ValueAnimator mRightDashAnimator = null;
    private ValueAnimator mPositionAnimator = null;

    public SelectRightView(Context context) {
        this(context, null);
    }

    public SelectRightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectRightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SelectRightView, 0, 0);
        float dashPadding;
        int dashColor;
        int shadowStartColor;
        try {
            mMaxContentWidth = attributes.getLayoutDimension(R.styleable.SelectRightView_rw_max_width, 0);
            mDashLength = attributes.getDimension(R.styleable.SelectRightView_rw_dash_length, ViewUtils.dp2px(context, DEFAULT_STROKE_LENGTH_DP));
            dashPadding = attributes.getDimension(R.styleable.SelectRightView_rw_dash_padding, ViewUtils.dp2px(context, DEFAULT_STROKE_PADDING_DP));
            mDashWidth = attributes.getDimension(R.styleable.SelectRightView_rw_dash_width, ViewUtils.dp2px(context, DEFAULT_STROKE_WIDTH_DP));
            dashColor = attributes.getColor(R.styleable.SelectRightView_rw_dash_color, 0xFFB2B2B2);
            mCornerRadius = (int) attributes.getDimension(R.styleable.SelectRightView_rw_corner_radius, ViewUtils.dp2px(getContext(), DEFAULT_CORNER_RADIUS_DP));
            mShadowSize = (int) attributes.getDimension(R.styleable.SelectRightView_rw_shadow_size, ViewUtils.dp2px(getContext(), DEFAULT_SHADOW_SIZE_DP));
            shadowStartColor = attributes.getColor(R.styleable.SelectRightView_rw_shadow_start_color, 0xFF757575);
            mDrawableSize = ViewUtils.dp2px(context, DEFAULT_DRAWABLE_SIZE_DP);
        } finally {
            attributes.recycle();
        }

        mLeftHandDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_thumb_down_wht).mutate());
        mRightHandDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_thumb_up_wht).mutate());

        mLeftHandBigDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_thumb_down_big).mutate();
        mRightHandBigDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_thumb_up_big).mutate();
        mLeftHandBigDrawable.setAlpha(mBigImageAlpha);
        mRightHandBigDrawable.setAlpha(mBigImageAlpha);

        mUnderBigHandPaint = new Paint();
        mUnderBigHandPaint.setAlpha(mBigImageAlpha);

        DrawableCompat.setTint(mLeftHandDrawable, ACTIVE_LEFT_COLOR);
        DrawableCompat.setTint(mRightHandDrawable, ACTIVE_RIGHT_COLOR);

        mDashPath = new Path();
        mDashLeftPath = new Path();
        mDashRightPath = new Path();

        mDashRectLeft = new RectF();
        mDashRectRight = new RectF();
        mImageRect = new Rect();
        mShadowRect = new RectF();

        mDashPaint = new Paint();
        mDashPaint.setAntiAlias(true);
        mDashPaint.setColor(dashColor);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(mDashWidth);

        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{mDashLength, dashPadding}, 0);
        mDashPaint.setPathEffect(dashPathEffect);

        mDashLeftPaint = new Paint(mDashPaint);
        mDashRightPaint = new Paint(mDashPaint);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setAlpha(255);
        mShadowPaint.setShadowLayer(mCornerRadius, 0, mShadowSize, shadowStartColor);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = mDrawableSize + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        mTopPosition = h + paddingTop - paddingBottom;

        mDashPath.reset();
        mDashLeftPath.reset();
        mDashRightPath.reset();

        float dashRectSize = mDrawableSize - mDashWidth * 2;
        mDashRectLeft.set(0, 0, dashRectSize, dashRectSize);
        mDashRectRight.set(0, 0, dashRectSize, dashRectSize);
        mDashRectLeft.offsetTo(paddingLeft, (mTopPosition - mDashRectLeft.height()) / 2f);
        mDashRectRight.offsetTo(w - paddingRight - mDashRectRight.width(), (mTopPosition - mDashRectLeft.height()) / 2f);

        mImageRect.set(0, 0, mDrawableSize, mDrawableSize);
        mImageRect.offsetTo((int) ((w - mImageRect.width()) / 2f), (mTopPosition - mImageRect.height()) / 2);
        mShadowRect = new RectF(mImageRect);
        mShadowRect.inset(mShadowSize / 2f, mShadowSize / 2f);

        float lineY = (mTopPosition) / 2f;

        mDashLeftPath.addRoundRect(mDashRectLeft, mCornerRadius, mCornerRadius, Path.Direction.CW);
        mDashRightPath.addRoundRect(mDashRectRight, mCornerRadius, mCornerRadius, Path.Direction.CW);

        mDashPath.moveTo(mDashRectLeft.right + mDashLength, lineY);
        mDashPath.lineTo(mDashRectRight.left - mDashLength, lineY);

        Rect leftHandRect = convertRectFtoRect(mDashRectLeft);
        Rect rightHandRect = convertRectFtoRect(mDashRectRight);

        int imagePadding = Math.max((leftHandRect.width() - mLeftHandDrawable.getIntrinsicWidth()) / 2, 0);

        leftHandRect.inset(imagePadding, imagePadding);
        rightHandRect.inset(imagePadding, imagePadding);

        mLeftHandDrawable.setBounds(leftHandRect);
        mRightHandDrawable.setBounds(rightHandRect);

        mLeftHandBigDrawable.setBounds(mImageRect);
        mRightHandBigDrawable.setBounds(mImageRect);
    }

    @Override
    public int getPaddingRight() {
        if (mMaxContentWidth > 0) {
            return getHorizontalPadding();
        } else {
            return super.getPaddingRight();
        }
    }

    @Override
    public int getPaddingLeft() {
        if (mMaxContentWidth > 0) {
            return getHorizontalPadding();
        } else {
            return super.getPaddingLeft();
        }
    }

    private int getHorizontalPadding() {
        int contentWidth = getWidth();
        return (int) ((contentWidth - mMaxContentWidth) / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawDashes(canvas);
        drawHands(canvas);
        drawImage(canvas);
        super.onDraw(canvas);
    }

    private void drawDashes(Canvas canvas) {
        canvas.drawPath(mDashPath, mDashPaint);
        canvas.drawPath(mDashLeftPath, mDashLeftPaint);
        canvas.drawPath(mDashRightPath, mDashRightPaint);
    }

    private void drawHands(Canvas canvas) {
        mLeftHandDrawable.draw(canvas);
        mRightHandDrawable.draw(canvas);
    }

    private void drawImage(Canvas canvas) {
        if (mImage != null) {
            float imageTop = (mTopPosition - mImageRect.height()) / 2;
            float shadowTop = (mTopPosition - mShadowRect.height()) / 2;
            if (mState == State.IDLE) {
                mImageRect.offsetTo((getWidth() - mImageRect.width()) / 2, (int) imageTop);
                mShadowRect.offsetTo((getWidth() - mShadowRect.width()) / 2f, shadowTop);
            } else {
                mImageRect.offsetTo((int) mCurrentX - mImageRect.width() / 2, (int) imageTop);
                mShadowRect.offsetTo(mCurrentX - mShadowRect.width() / 2, shadowTop);
            }

            canvas.drawRect(mRightHandBigDrawable.getBounds(), mUnderBigHandPaint);

            if (mState == State.LEFT) {
                mLeftHandBigDrawable.draw(canvas);
            } else if (mState == State.RIGHT) {
                mRightHandBigDrawable.draw(canvas);
            }

            canvas.drawRoundRect(mShadowRect, mCornerRadius, mCornerRadius, mShadowPaint);
            mImage.setBounds(mImageRect);
            mImage.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int posX = (int) event.getX();
        int posY = (int) event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mImage != null) {
                    if (mImageRect.contains(posX, posY)) {
                        startDrag();
                        mCurrentX = posX;
                        setStateByPosX(mCurrentX);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDragging) {
                    mCurrentX = posX;
                    setStateByPosX(mCurrentX);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mDragging) {
                    mCurrentX = posX;
                    setStateByPosX(mCurrentX);
                    stopDrag();
                } else if (mDashRectLeft.contains(posX, posY) && mState != State.LEFT) {
                    setStateAnim(State.LEFT);
                } else if (mDashRectRight.contains(posX, posY) && mState != State.RIGHT) {
                    setStateAnim(State.RIGHT);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void startDrag() {
        mDragging = true;
        animateImageScale();
        animateIconColor();
        animateBigHandAlpha();
        getParent().requestDisallowInterceptTouchEvent(true);
        invalidate();
    }

    private void stopDrag() {
        mDragging = false;
        animateImageScale();
        animateIconColor();
        animateToState();
        animateBigHandAlpha();
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.change(mState);
        }
        invalidate();
    }

    private void setStateByPosX(float currentX) {
        State state;
        if (currentX < getWidth() / 2) {
            state = State.LEFT;
        } else {
            state = State.RIGHT;
        }
        if (mState != state) {
            mState = state;
            animateStateChanges();
        }
        invalidate();
    }

    private void animateImageScale() {
        if (mScaleAnimation != null) {
            mScaleAnimation.removeAllListeners();
            mScaleAnimation.cancel();
        }

        if (mDragging) {
            mScaleAnimation = ObjectAnimator.ofInt(mDrawableSize, (int) (mDrawableSize * 1.25f));
        } else {
            mScaleAnimation = ObjectAnimator.ofInt((int) (mDrawableSize * 1.25f), mDrawableSize);
        }
        mScaleAnimation.setInterpolator(new OvershootInterpolator());
        mScaleAnimation.setDuration(SCALE_ANIMATION_DURATION);

        mScaleAnimation.addUpdateListener(animation -> {
            int size = (int) animation.getAnimatedValue();
            mImageRect.set(0, 0, size, size);
            mShadowRect.set(0, 0, size, size);
            if (!mDragging) {
                mShadowRect.inset(mShadowSize, mShadowSize);
            } else {
                mShadowRect.inset(mShadowSize / 3, mShadowSize / 3);
            }

            invalidate();
        });

        mScaleAnimation.start();
    }

    private void animateIconColor() {
        if (mLeftHandColorAnimator != null) {
            mLeftHandColorAnimator.removeAllListeners();
            mLeftHandColorAnimator.cancel();
        }
        if (mRightHandColorAnimator != null) {
            mRightHandColorAnimator.removeAllListeners();
            mRightHandColorAnimator.cancel();
        }

        if (mDragging) {
            mLeftHandColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), INACTIVE_COLOR, ACTIVE_LEFT_COLOR);
            mRightHandColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), INACTIVE_COLOR, ACTIVE_RIGHT_COLOR);
        } else {
            mLeftHandColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), ACTIVE_LEFT_COLOR, INACTIVE_COLOR);
            mRightHandColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), ACTIVE_RIGHT_COLOR, INACTIVE_COLOR);
        }

        mLeftHandColorAnimator.addUpdateListener(animation -> {
            mLeftHandDrawable.setColorFilter((Integer) animation.getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
            invalidate();
        });
        mLeftHandColorAnimator.setDuration(COLOR_ANIMATION_DURATION);

        mRightHandColorAnimator.addUpdateListener(animation -> {
            mRightHandDrawable.setColorFilter((Integer) animation.getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
            invalidate();
        });
        mRightHandColorAnimator.setDuration(COLOR_ANIMATION_DURATION);

        mLeftHandColorAnimator.start();
        mRightHandColorAnimator.start();
    }

    private void animateStateChanges() {
        if (mLeftDashAnimator != null) {
            mLeftDashAnimator.removeAllListeners();
            mLeftDashAnimator.cancel();
        }
        if (mRightDashAnimator != null) {
            mRightDashAnimator.removeAllListeners();
            mRightDashAnimator.cancel();
        }

        if (mState == State.LEFT) {
            mLeftDashAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mDashLeftPaint.getColor(), ACTIVE_LEFT_COLOR);
            mRightDashAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mDashRightPaint.getColor(), INACTIVE_COLOR);
        } else if (mState == State.RIGHT) {
            mLeftDashAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mDashLeftPaint.getColor(), INACTIVE_COLOR);
            mRightDashAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mDashRightPaint.getColor(), ACTIVE_RIGHT_COLOR);
        }

        if (mLeftDashAnimator != null) {
            mLeftDashAnimator.setDuration(COLOR_ANIMATION_DURATION);
            mLeftDashAnimator.addUpdateListener(animation -> {
                mDashLeftPaint.setColor((Integer) animation.getAnimatedValue());
                invalidate();
            });
            mLeftDashAnimator.start();
        }
        if (mRightDashAnimator != null) {
            mRightDashAnimator.setDuration(COLOR_ANIMATION_DURATION);
            mRightDashAnimator.addUpdateListener(animation -> {
                mDashRightPaint.setColor((Integer) animation.getAnimatedValue());
                invalidate();
            });
            mRightDashAnimator.start();
        }
    }

    private void animateToState() {
        if (mPositionAnimator != null) {
            mPositionAnimator.removeAllListeners();
            mPositionAnimator.cancel();
        }

        if (mState == State.LEFT) {
            mPositionAnimator = ValueAnimator.ofFloat(mCurrentX, mDashRectLeft.right - mDashRectLeft.width() / 2f);
        } else if (mState == State.RIGHT) {
            mPositionAnimator = ValueAnimator.ofFloat(mCurrentX, mDashRectRight.left + mDashRectRight.width() / 2f);
        }

        if (mPositionAnimator != null) {
            mPositionAnimator.addUpdateListener(animation -> {
                mCurrentX = (float) animation.getAnimatedValue();
                invalidate();
            });
            mPositionAnimator.start();
        }
    }

    private void animateBigHandAlpha() {
        if (mAlphaAnimation != null) {
            mAlphaAnimation.removeAllListeners();
            mAlphaAnimation.cancel();
        }

        if (mDragging) {
            mAlphaAnimation = ValueAnimator.ofInt(mBigImageAlpha, 0);
        } else {
            mAlphaAnimation = ValueAnimator.ofInt(mBigImageAlpha, 255);
        }

        mAlphaAnimation.setDuration(COLOR_ANIMATION_DURATION);
        mAlphaAnimation.addUpdateListener(animation -> {
            mBigImageAlpha = (int) animation.getAnimatedValue();
            mLeftHandBigDrawable.setAlpha(mBigImageAlpha);
            mRightHandBigDrawable.setAlpha(mBigImageAlpha);
            mUnderBigHandPaint.setAlpha(mBigImageAlpha);
            invalidate();
        });
        mAlphaAnimation.start();
    }

    private Rect convertRectFtoRect(RectF rectF) {
        return new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    /**
     * Sets drag image.
     *
     * @param image drag image.
     */
    public void setDrawable(Drawable image) {
        mImage = image;
        invalidate();
    }

    /**
     * Sets background color under center big hand image, to clip dashes.
     *
     * @param backgroundColor color of background.
     */
    public void setBackgroundColor(int backgroundColor) {
        mUnderBigHandPaint.setColor(backgroundColor);
        invalidate();
    }

    /**
     * Gets state.
     *
     * @return the state of view {@link State}
     */
    public State getState() {
        return mState;
    }

    /**
     * Sets current state.
     */
    public void setState(State state) {
        post(() -> {
            mState = state;
            if (state == State.RIGHT) {
                mCurrentX = mDashRectRight.left + mDashRectRight.width() / 2f;
                mLeftHandDrawable.setColorFilter(INACTIVE_COLOR, PorterDuff.Mode.MULTIPLY);
            } else {
                mCurrentX = mDashRectLeft.right - mDashRectLeft.width() / 2f;
                mRightHandDrawable.setColorFilter(INACTIVE_COLOR, PorterDuff.Mode.MULTIPLY);
            }
            invalidate();
            animateBigHandAlpha();
        });
    }

    private void setStateAnim(State state) {
        post(() -> {
            if (mState == State.IDLE) {
                mCurrentX = getWidth() / 2;
            }
            mState = state;
            if (mOnStateChangeListener != null) {
                mOnStateChangeListener.change(mState);
            }
            animateIconColor();
            animateToState();
            animateBigHandAlpha();
            animateStateChanges();
        });
    }

    /**
     * Sets on state change listener.
     *
     * @param onStateChangeListener the on state change listener
     */
    public void setOnStateChangeListener(OnStateChange onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
    }

    @Parcel
    public enum State {
        IDLE, LEFT, RIGHT
    }

    public interface OnStateChange {
        void change(State state);
    }
}

