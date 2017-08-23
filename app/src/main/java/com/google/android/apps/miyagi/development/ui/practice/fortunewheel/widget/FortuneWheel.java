package com.google.android.apps.miyagi.development.ui.practice.fortunewheel.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel.FortuneWheelAnswerOption;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import com.tsengvn.typekit.Typekit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerzyw on 16.11.2016.
 */

public class FortuneWheel extends View {

    //.............................................................................. internal config
    private static final double MINIMAL_SPEED = 0.0006;
    //............................................................................... default colors
    private static final int DEFAULT_SECTOR_COLOR = 0xFF888888;
    private static final int DEFAULT_SECTOR_LABEL_TEXT_SIZE_PX = 80;
    private static final int DEFAULT_SECTOR_LABEL_TEXT_COLOR = 0xffffff;
    private static final int SECTOR_LABEL_TEXT_ALPHA = 255;
    private static final int SECTOR_ALPHA = 255;
    private static final int NO_TINT_COLOR = -1;
    //..............................................................................................
    private static final double RAD2DEG = 180 / Math.PI;

    //paints
    private Paint mSectorPaint;
    private TextPaint mLabelPaint;

    private List<SectorRange> mSectors = new ArrayList<>();
    private OnItemSelectedListener<FortuneWheelAnswerOption> mSelectionListener;
    //
    private float mCircleRadius;
    private RectF mOvalRect;
    private double mCurrentAngle;
    private double mAngularVelocityDelta;
    private double mCurrentSpeed;
    /**
     * User is touching the screen and dragging circle - no inertness mode.
     */
    private boolean mIsDragging;
    /**
     * Rotation to current touch point.
     * Be aware that circle can have some rotation state when touch was started (@see mAngleOffsetOnDragging).
     */
    private double mAngle2CurrentTouch;
    /**
     * Circle rotation when drag was started.
     */
    private double mAngleOffsetOnDragging;

    private State mCurrentState = State.INIT;

    private int mMaxContentHeight;

    //........
    private Drawable mMarkerDrawable;

    private SectorRange mPrevSelectedSector = null;
    private SectorRange mInitSector;

    public FortuneWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FortuneWheel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FortuneWheel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Init function.
     *
     * @param context      - parent context.
     * @param attributeSet - attribute set from xml.
     */
    public void init(Context context, AttributeSet attributeSet) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.FortuneWheel,
                0, 0);
        float labelTextSizePx = DEFAULT_SECTOR_LABEL_TEXT_SIZE_PX;
        int labelTextSizeDp = ViewUtils.px2dp(context, labelTextSizePx);
        int labelTextColor = DEFAULT_SECTOR_LABEL_TEXT_COLOR;
        int markerTintColor = NO_TINT_COLOR;
        try {
            mMaxContentHeight = (int) attributes.getDimension(R.styleable.FortuneWheel_maxHeight, 0);
            markerTintColor = attributes.getColor(R.styleable.FortuneWheel_markerColor, markerTintColor);
            mMarkerDrawable = attributes.getDrawable(R.styleable.FortuneWheel_markerDrawable);
            labelTextSizePx = attributes.getDimensionPixelSize(R.styleable.FortuneWheel_labelTextSize, labelTextSizeDp);
            labelTextColor = attributes.getColor(R.styleable.FortuneWheel_labelTextColor, DEFAULT_SECTOR_LABEL_TEXT_COLOR);
        } finally {
            attributes.recycle();
        }

        if (mMarkerDrawable != null) {
            mMarkerDrawable.setBounds(0,
                    0,
                    mMarkerDrawable.getIntrinsicWidth(),
                    mMarkerDrawable.getIntrinsicHeight()
            );
        }
        setMarkerTintColor(markerTintColor);

        mSectorPaint = new Paint();
        mSectorPaint.setAntiAlias(true);
        mSectorPaint.setColor(DEFAULT_SECTOR_COLOR);

        mLabelPaint = new TextPaint();
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setColor(labelTextColor);
        mLabelPaint.setAlpha(SECTOR_LABEL_TEXT_ALPHA);
        mLabelPaint.setTextSize(labelTextSizePx);
        mLabelPaint.setTypeface(Typekit.getInstance().get("Roboto-Medium.ttf"));
        mLabelPaint.setFakeBoldText(true);
    }

    /**
     * Sets options data to widget.
     *
     * @param sectors - options data.
     */
    public void setSectors(List<FortuneWheelAnswerOption> sectors) {
        mSectors.clear();
        //createPresenter sectors ranges
        if (sectors != null) {
            int sectorsNum = sectors.size();
            double currAngle;
            double prevAngle = 0;
            FortuneWheelAnswerOption answer;
            for (int i = 0; i < sectorsNum; ++i) {
                currAngle = 360 / sectorsNum * (i + 1);
                answer = sectors.get(i);
                mSectors.add(new SectorRange(i, prevAngle, currAngle, answer));
                prevAngle = currAngle;
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float circleCenterX = mOvalRect.centerX();
        float circleCenterY = mOvalRect.centerY();
        float currX = event.getX() - circleCenterX;
        float currY = event.getY() - circleCenterY;

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = true;
                mAngularVelocityDelta = 0;
                mAngle2CurrentTouch = Math.atan2(currY, currX) * RAD2DEG;
                mAngleOffsetOnDragging = mCurrentAngle - mAngle2CurrentTouch;
                mCurrentState = State.USER_SELECTION;
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int historySize = event.getHistorySize();
                if (historySize > 0) {
                    int prevTouchIndex = historySize - 1;
                    float prevX = event.getHistoricalX(prevTouchIndex) - circleCenterX;
                    float prevY = event.getHistoricalY(prevTouchIndex) - circleCenterY;
                    mAngle2CurrentTouch = Math.atan2(currY, currX) * RAD2DEG;
                    double angle2PrevTouch = Math.atan2(prevY, prevX) * RAD2DEG;
                    mAngularVelocityDelta = mAngle2CurrentTouch - angle2PrevTouch;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float paddingLeft = getPaddingLeft();
        float paddingTop = getPaddingTop();
        float horizontalPadding = (float) (getPaddingLeft() + getPaddingRight());
        float verticalPadding = (float) (getPaddingTop() + getPaddingBottom());

        float markerHalfHeight = (mMarkerDrawable == null) ? 0f : mMarkerDrawable.getBounds().height() * 0.5f;

        float circleAreaWidth = w - horizontalPadding;
        float circleAreaHeight = h - verticalPadding - markerHalfHeight;

        mCircleRadius = Math.min(circleAreaWidth, circleAreaHeight) * 0.5f;
        float circleX = paddingLeft + circleAreaWidth * 0.5f;
        float circleY = paddingTop + markerHalfHeight + circleAreaHeight * 0.5f;

        mOvalRect = new RectF(circleX - mCircleRadius, circleY - mCircleRadius, circleX + mCircleRadius, circleY + mCircleRadius);

    }

    @Override
    public int getPaddingTop() {
        if (mMaxContentHeight > 0 && mMaxContentHeight < getHeight()) {
            return getVerticalPadding();
        } else {
            return super.getPaddingTop();
        }
    }

    @Override
    public int getPaddingBottom() {
        if (mMaxContentHeight > 0 && mMaxContentHeight < getHeight()) {
            return getVerticalPadding();
        } else {
            return super.getPaddingBottom();
        }
    }

    private int getVerticalPadding() {
        int contentHeight = getHeight();
        return (int) ((contentHeight - mMaxContentHeight) / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngleOffset;
        float circleCenterX = mOvalRect.centerX();
        float circleCenterY = mOvalRect.centerY();
        SectorRange selectedSector = null;

        if (mSectors.size() == 0) {
            mSectorPaint.setColor(DEFAULT_SECTOR_COLOR);
            canvas.drawCircle(circleCenterX, circleCenterY, mCircleRadius, mSectorPaint);
        } else {
            float sectorAngleSize = 360 / mSectors.size();
            //0 angle point on the top
            startAngleOffset = -90;
            final float startDrawAngle = startAngleOffset + (float) mCurrentAngle;
            //............................ calc speed
            //different signs - reset inertia
            if (mAngularVelocityDelta * mCurrentSpeed < 0) {
                mCurrentSpeed = 0;
            }
            mCurrentSpeed += mAngularVelocityDelta;
            //friction
            mAngularVelocityDelta *= 0.9;
            mCurrentSpeed *= 0.9;
            //............................ update angle depend on current mode
            if (mIsDragging) {
                mCurrentAngle = mAngleOffsetOnDragging + mAngle2CurrentTouch;
            } else {
                mCurrentAngle += mCurrentSpeed;
            }
            mCurrentAngle = mCurrentAngle % 360;
            //............................ draw
            float currDrawAngle = startDrawAngle;
            //draw circle sectors
            for (SectorRange sector : mSectors) {
                mSectorPaint.setColor(sector.getAnswer().getOptionColor());
                mSectorPaint.setAlpha(SECTOR_ALPHA);
                canvas.drawArc(mOvalRect, currDrawAngle, sectorAngleSize, true, mSectorPaint);
                currDrawAngle = currDrawAngle + sectorAngleSize;
            }
            //draw labels
            float textWidth;
            String label;
            FortuneWheelAnswerOption answer;
            currDrawAngle = startDrawAngle + sectorAngleSize * 0.5f;
            for (SectorRange sector : mSectors) {
                canvas.save();
                //set pivot on center of circle
                canvas.translate(circleCenterX, circleCenterY);
                //rotate letter
                canvas.rotate(currDrawAngle);
                canvas.rotate(90);
                //
                answer = sector.getAnswer();
                label = answer.getLabel();
                textWidth = mLabelPaint.measureText(label);
                canvas.translate(-textWidth * 0.5f, -mCircleRadius * 0.5f);
                canvas.drawText(label, 0, 0, mLabelPaint);
                canvas.restore();
                currDrawAngle = currDrawAngle + sectorAngleSize;
                //....
                if (sector.contains(mCurrentAngle)) {
                    selectedSector = sector;

                    if (mCurrentAngle != 0.0) {
                        if (mPrevSelectedSector == null
                                || selectedSector.getSectorIndex() != mPrevSelectedSector.getSectorIndex()) {
                            mPrevSelectedSector = selectedSector;
                            dispatchSelectionChanged(selectedSector);
                        }
                    }
                }
            }
            //swapping - enabled after user first interaction
            if (mCurrentState == State.USER_SELECTION) {
                double normalizedAngle = -mCurrentAngle % 360;
                if (normalizedAngle < 0) {
                    normalizedAngle = 360 + normalizedAngle;
                }
                double sectorHalfAngleSize = sectorAngleSize * 0.5f;
                //<-sectorHalfAngleSize, sectorHalfAngleSize>
                double angleDistanceToSwapPoint = selectedSector.getCenter() - normalizedAngle;
                //<-1,...,0,..., 1> c R
                double forceNum = angleDistanceToSwapPoint / sectorHalfAngleSize;
                //swapping force
                mCurrentSpeed = mCurrentSpeed - forceNum * 0.7f;
                if (!mIsDragging) {
                    //disable sign check between mCurrentSpeed and mAngularVelocityDelta on next frame
                    mAngularVelocityDelta = 0;
                }
                if (Math.abs(mCurrentSpeed) > MINIMAL_SPEED) {
                    invalidate();
                } else {
                    mCurrentSpeed = 0;
                }
            } else if (mCurrentState == State.INIT_SELECTION) {
                mCurrentAngle = -mInitSector.getCenter();
                mCurrentState = State.USER_SELECTION;
                invalidate();
            }
        }

        if (mMarkerDrawable != null) {
            canvas.save();
            canvas.translate(circleCenterX - mMarkerDrawable.getBounds().width() * 0.5f, getPaddingTop());
            mMarkerDrawable.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * Call OnItemSelectedListener to notify selection change.
     *
     * @param selectedSector - selected sector.
     */
    private void dispatchSelectionChanged(SectorRange selectedSector) {
        if (mSelectionListener != null) {
            mSelectionListener.onItemSelected(selectedSector.getAnswer());
        }
    }

    public void setOnSelectionChangeListener(OnItemSelectedListener<FortuneWheelAnswerOption> listener) {
        mSelectionListener = listener;
    }

    /**
     * Change text color of label.
     *
     * @param color - color of text.
     */
    public void setLabelTextColor(int color) {
        mLabelPaint.setColor(color);
        mLabelPaint.setAlpha(SECTOR_LABEL_TEXT_ALPHA);
        invalidate();
    }

    /**
     * Set tint color to selection marker.
     *
     * @param color - tint color.
     */
    public void setMarkerTintColor(int color) {
        if (mMarkerDrawable != null) {
            if (color == NO_TINT_COLOR) {
                clearMarkerTintColor();
            } else {
                mMarkerDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    /**
     * Clears tint of selection marker.
     */
    public void clearMarkerTintColor() {
        if (mMarkerDrawable != null) {
            mMarkerDrawable.clearColorFilter();
        }
    }

    /**
     * Sets selection.
     *
     * @param currentItem the current item.
     */
    public void setSelection(FortuneWheelAnswerOption currentItem) {
        for (SectorRange sectorRange : mSectors) {
            if (sectorRange.getAnswer().equals(currentItem)) {
                mCurrentState = State.INIT_SELECTION;
                mInitSector = sectorRange;
                break;
            }
        }
    }

    //..............................................................................................
    private static class SectorRange {

        private int mSectorIndex;
        private final double mFrom;
        private final double mTo;
        private final double mCenter;
        private FortuneWheelAnswerOption mAnswer;

        public SectorRange(int sectorIndex, double from, double to, FortuneWheelAnswerOption answer) {
            mSectorIndex = sectorIndex;
            mFrom = from;
            mTo = to;
            mCenter = mFrom + (mTo - mFrom) * 0.5f;
            mAnswer = answer;
        }

        public boolean contains(double rawAngle) {
            double normalizedAngle = -rawAngle % 360;
            if (normalizedAngle < 0) {
                normalizedAngle = 360 + normalizedAngle;
            }

            return (normalizedAngle >= mFrom && normalizedAngle < mTo);
        }

        public int getSectorIndex() {
            return mSectorIndex;
        }

        public double getFrom() {
            return mFrom;
        }

        public double getTo() {
            return mTo;
        }

        public double getCenter() {
            return mCenter;
        }

        public FortuneWheelAnswerOption getAnswer() {
            return mAnswer;
        }
    }

    public enum State {
        /**
         * Init state. User has not made interaction with widget.
         * No sector is selected.
         * Swapping is disabled.
         */
        INIT,
        /**
         * When there is predefined sector (e.g. when parent view is recreating)
         */
        INIT_SELECTION,
        /**
         * After first user interaction or init selection.
         * Sectors center swapping is enabled.
         */
        USER_SELECTION
    }
}
