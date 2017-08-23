package com.google.android.apps.miyagi.development.ui.practice.reorder.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

import static com.google.android.apps.miyagi.development.ui.practice.reorder.widget.TransparentCircleLayout.BgStyle.DASH;
import static com.google.android.apps.miyagi.development.ui.practice.reorder.widget.TransparentCircleLayout.BgStyle.SOLID;

/**
 * Created by lukaszweglinski on 15.11.2016.
 */

public class TransparentCircleLayout extends FrameLayout {

    private static final int FULL_ALPHA = 255;
    private static final int DRAGGED_SHADOW_ALPHA = 170;
    private static final int SHADOW_COLOR = 0x000000;
    private static final int DASH_BORDER_COLOR = 0x000000;

    private static final float DEFAULT_DASH_STROKE_ALPHA = 0.3f;
    private static final float DEFAULT_SHADOW_ALPHA = 0.4f;

    private static final int DEFAULT_BG_COLOR = 0x005bbb;
    private static final int DEFAULT_SHADOW_RADIUS_DP = 5;
    private static final float DEFAULT_STROKE_WIDTH_DP = 2;
    private static final float DEFAULT_HOLE_RADIUS_DP = 30;
    private static final float DEFAULT_CORNER_ROUNDNESS_DP = 2;
    private static final float DEFAULT_HOLE_CIRCLE_OFFSET_DP = 30;
    private static final float DEFAULT_DASH_ON_DISTANCE_DP = 10;
    private static final float DEFAULT_DASH_OFF_DISTANCE_DP = DEFAULT_DASH_ON_DISTANCE_DP / 3;

    private Paint mEraserPaint;
    private Paint mShadowEraserPaint;
    private Paint mBgPaint;
    private Paint mDashPaint;
    private Paint mShadowPaint;
    private Paint mDraggedShadowPaint;

    private Bitmap mBitmapBuffer;
    private Canvas mBufferCanvas;
    private Bitmap mShadowBuffer;
    private Canvas mShadowCanvas;

    private BgStyle mBgStyle = SOLID;

    private RectF mBorderBounds;

    private int mStrokeWidthPx;
    private int mHoleRadiusPx;
    private int mCornerRoundnessPx;
    private int mHoleCircleLeftOffsetPx;


    private RectF mAreaBounds;
    private boolean mBitmapsAreDirty = true;

    public TransparentCircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TransparentCircleLayout(Context context, AttributeSet attrs,
                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setWillNotDraw(false);
        // blur effect will work only in software mode
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.TransparentCircleLayout,
                0, 0);

        float shadowAlpha = DEFAULT_SHADOW_ALPHA;
        float dashStrokeAlpha = DEFAULT_DASH_STROKE_ALPHA;

        mStrokeWidthPx = ViewUtils.dp2px(context, DEFAULT_STROKE_WIDTH_DP);
        mHoleRadiusPx = ViewUtils.dp2px(context, DEFAULT_HOLE_RADIUS_DP);
        mCornerRoundnessPx = ViewUtils.dp2px(context, DEFAULT_CORNER_ROUNDNESS_DP);
        mHoleCircleLeftOffsetPx = ViewUtils.dp2px(context, DEFAULT_HOLE_CIRCLE_OFFSET_DP);
        float shadowRadius = ViewUtils.dp2px(context, DEFAULT_SHADOW_RADIUS_DP);

        float dashOnDistance = ViewUtils.dp2px(context, DEFAULT_DASH_ON_DISTANCE_DP);
        float dashOffDistance = ViewUtils.dp2px(context, DEFAULT_DASH_OFF_DISTANCE_DP);
        try {
            shadowAlpha = attributes.getFloat(R.styleable.TransparentCircleLayout_shadowAlpha, shadowAlpha);
            dashStrokeAlpha = attributes.getFloat(R.styleable.TransparentCircleLayout_dashStrokeAlpha, dashStrokeAlpha);
            mStrokeWidthPx = attributes.getDimensionPixelSize(R.styleable.TransparentCircleLayout_strokeWidth, mStrokeWidthPx);
            mHoleRadiusPx = attributes.getDimensionPixelSize(R.styleable.TransparentCircleLayout_holeRadius, mHoleRadiusPx);
            mHoleCircleLeftOffsetPx = attributes.getDimensionPixelSize(R.styleable.TransparentCircleLayout_holeCircleLeftOffset, mHoleCircleLeftOffsetPx);
            dashOnDistance = attributes.getDimension(R.styleable.TransparentCircleLayout_dashOnDistance, dashOnDistance);
            dashOffDistance = attributes.getDimension(R.styleable.TransparentCircleLayout_dashOffDistance, dashOffDistance);
            shadowRadius = attributes.getDimension(R.styleable.TransparentCircleLayout_shadowRadius, shadowRadius);
        } finally {
            attributes.recycle();
        }

        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));


        mShadowPaint = new Paint();
        mShadowPaint.setColor(SHADOW_COLOR);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setAlpha((int) (FULL_ALPHA * shadowAlpha));
        mShadowPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL));

        mDraggedShadowPaint = new Paint();
        mDraggedShadowPaint.setColor(SHADOW_COLOR);
        mDraggedShadowPaint.setAntiAlias(true);
        mDraggedShadowPaint.setAlpha(DRAGGED_SHADOW_ALPHA);
        mDraggedShadowPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL));

        mShadowEraserPaint = new Paint();
        mShadowEraserPaint.setAlpha(0);
        mShadowEraserPaint.setAntiAlias(true);
        mShadowEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mShadowEraserPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL));

        mBgPaint = new Paint();
        setBgColor(DEFAULT_BG_COLOR);

        mDashPaint = new Paint();
        mDashPaint.setColor(DASH_BORDER_COLOR);
        mDashPaint.setAntiAlias(true);
        mDashPaint.setAlpha((int) (FULL_ALPHA * dashStrokeAlpha));
        mDashPaint.setPathEffect(new DashPathEffect(new float[]{dashOnDistance, dashOffDistance}, 0));
        mDashPaint.setStrokeWidth(mStrokeWidthPx);
        mDashPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Changes background color.
     */
    public void setBgColor(int color) {
        mBgPaint.setColor(color);
        mBgPaint.setAlpha(FULL_ALPHA);
        mBitmapsAreDirty = true;
        invalidate();
    }

    /**
     * Changes layout background style.
     */
    public void setBgStyle(BgStyle style) {
        mBgStyle = style;
        mBitmapsAreDirty = true;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        if (width != oldWidth || height != oldHeight) {
            if (mBitmapBuffer != null) {
                mBitmapBuffer.recycle();
            }
            if (mShadowBuffer != null) {
                mShadowBuffer.recycle();
            }
            //in ARGB_8888 mode blur filter operates only on color pixels
            mBitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ALPHA_8);
            mShadowBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            mBufferCanvas = new Canvas(mBitmapBuffer);
            mShadowCanvas = new Canvas(mShadowBuffer);
            mBitmapsAreDirty = true;
        }

        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();

        float strokeHalfWidth = mStrokeWidthPx * 0.5f;

        mAreaBounds = new RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom);

        mBorderBounds = new RectF(
                mAreaBounds.left + strokeHalfWidth,
                mAreaBounds.top + strokeHalfWidth,
                mAreaBounds.right - strokeHalfWidth,
                mAreaBounds.bottom - strokeHalfWidth
        );
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.restore();

        int circleHeightCenter = getPaddingTop() + ((getHeight() - getPaddingTop() - getPaddingBottom()) / 2);
        if (mBgStyle == DASH) {
            //drop zone
            canvas.drawRoundRect(mBorderBounds, mCornerRoundnessPx, mCornerRoundnessPx, mDashPaint);
            canvas.drawCircle(mHoleCircleLeftOffsetPx + mHoleRadiusPx, circleHeightCenter, mHoleRadiusPx + mStrokeWidthPx, mDashPaint);
        } else {
            if (mBitmapsAreDirty) {
                mBitmapsAreDirty = false;
                mShadowBuffer.eraseColor(0x00FFFFFF);
                mBitmapBuffer.eraseColor(0x00FFFFFF);
                //draw shadow
                if (mBgStyle == SOLID) {
                    mShadowCanvas.drawRoundRect(mAreaBounds, mCornerRoundnessPx, mCornerRoundnessPx, mShadowPaint);
                } else {
                    mShadowCanvas.drawRoundRect(mAreaBounds, mCornerRoundnessPx, mCornerRoundnessPx, mDraggedShadowPaint);
                }
                mShadowCanvas.drawCircle(mHoleCircleLeftOffsetPx + mHoleRadiusPx, circleHeightCenter, mHoleRadiusPx, mShadowEraserPaint);
                //draw bg
                mBufferCanvas.drawRoundRect(mAreaBounds, mCornerRoundnessPx, mCornerRoundnessPx, mBgPaint);
                mBufferCanvas.drawCircle(mHoleCircleLeftOffsetPx + mHoleRadiusPx, circleHeightCenter, mHoleRadiusPx, mEraserPaint);
            }
            if (mBgStyle == SOLID) {
                canvas.drawBitmap(mShadowBuffer, 5, 5, mShadowPaint);
            } else {
                canvas.drawBitmap(mShadowBuffer, 13, 13, mDraggedShadowPaint);
            }
            canvas.drawBitmap(mBitmapBuffer, 0, 0, mBgPaint);
        }
        super.dispatchDraw(canvas);
    }

    public enum BgStyle {
        SOLID, DRAGGED, DASH
    }
}