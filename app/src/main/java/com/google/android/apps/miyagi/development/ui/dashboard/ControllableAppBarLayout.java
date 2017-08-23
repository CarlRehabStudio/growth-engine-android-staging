package com.google.android.apps.miyagi.development.ui.dashboard;

/**
 * Created by Paweł on 2017-03-02.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.apps.miyagi.development.ui.dashboard.support.widget.AppBarLayout;
import com.google.android.apps.miyagi.development.ui.dashboard.support.widget.CoordinatorLayout;

public class ControllableAppBarLayout extends AppBarLayout implements AppBarLayout.OnOffsetChangedListener {

    private OnStateChangeListener onStateChangeListener;
    private State mState;
    private State mLastFullState;
    private boolean mIsMoved;
    private float mOffsetY;

    public ControllableAppBarLayout(Context context) {
        super(context);
    }

    public ControllableAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!(getLayoutParams() instanceof CoordinatorLayout.LayoutParams)
                || !(getParent() instanceof CoordinatorLayout)) {
            throw new IllegalStateException(
                    "ControllableAppBarLayout must be a direct child of CoordinatorLayout.");
        }
        addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            if (onStateChangeListener != null && mState != State.EXPANDED) {
                onStateChangeListener.onStateChange(State.EXPANDED);
            }
            mState = State.EXPANDED;
            mLastFullState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (onStateChangeListener != null && mState != State.COLLAPSED) {
                onStateChangeListener.onStateChange(State.COLLAPSED);
            }
            mState = State.COLLAPSED;
            mLastFullState = State.COLLAPSED;
        } else {
            if (onStateChangeListener != null && mState != State.IDLE) {
                onStateChangeListener.onStateChange(State.IDLE);
            }
            mState = State.IDLE;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mIsMoved = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mIsMoved && event.getY() - mOffsetY < 0) {
                setExpanded(false, true);
                mIsMoved = false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mIsMoved = false;
            mOffsetY = event.getY();
        } else {
            mIsMoved = false;
        }
        return true;
    }

    /**
     * Set state changes listener.
     */
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.onStateChangeListener = listener;
    }

    @Override
    public State getLastFullState() {
        return mLastFullState;
    }

    public interface OnStateChangeListener {
        void onStateChange(State toolbarChange);
    }
}
