package com.google.android.apps.miyagi.development.ui.practice.reorder;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeAnswerOption;
import com.google.android.apps.miyagi.development.ui.practice.reorder.widget.TransparentCircleLayout;

/**
 * Created by jerzyw on 08.12.2016.
 */

public class ReorderPracticeViewHolder extends RecyclerView.ViewHolder {

    private final View mItemView;
    private final TextView mItemTextTv;
    private final TransparentCircleLayout mHoleLayout;
    private final LinearLayout mDragHandleLayout;

    private final int mActiveCardColor;
    private final int mActiveTextColor;
    private final int mInactiveCardColor;
    private final int mInactiveTextColor;
    private final ImageView mDragHandle;
    private final int mDragHandleActiveColor;
    private final int mDragHandleInactiveColor;

    /**
     * Item view holder constructor.
     */
    public ReorderPracticeViewHolder(View itemView,
                                     int activeCardColor,
                                     int activeTextColor,
                                     int inactiveCardColor,
                                     int inactiveTextColor,
                                     int dragHandleActiveColor,
                                     int dragHandleInactiveColor
    ) {
        super(itemView);
        mItemView = itemView;
        mItemTextTv = (TextView) itemView.findViewById(R.id.practice_reorder_item_text);
        mHoleLayout = (TransparentCircleLayout) itemView.findViewById(R.id.practice_reorder_hole_layout);
        mDragHandle = (ImageView) itemView.findViewById(R.id.practice_reorder_item_drag_handle);
        mActiveCardColor = activeCardColor;
        mActiveTextColor = activeTextColor;
        mInactiveCardColor = inactiveCardColor;
        mInactiveTextColor = inactiveTextColor;
        mDragHandleActiveColor = dragHandleActiveColor;
        mDragHandleInactiveColor = dragHandleInactiveColor;
        mDragHandleLayout = (LinearLayout) itemView.findViewById(R.id.practice_reorder_drag_layout);

        setState(State.NORMAL);
    }

    public void populateWithData(ReorderPracticeAnswerOption dataItem, OnStartDragListener dragStartListener) {
        mItemTextTv.setText(dataItem.getText());
        mDragHandleLayout.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(this);
            }
            return false;
        });
    }

    private void setDraggedState() {
        mItemTextTv.setTextColor(mActiveTextColor);
        mHoleLayout.setBgColor(mActiveCardColor);

        mDragHandle.setVisibility(View.VISIBLE);
        mDragHandle.setColorFilter(mDragHandleActiveColor);

        mItemTextTv.setVisibility(View.VISIBLE);
        mHoleLayout.setBgStyle(TransparentCircleLayout.BgStyle.DRAGGED);
    }

    private void setNormalState() {
        mItemTextTv.setTextColor(mInactiveTextColor);
        mHoleLayout.setBgColor(mInactiveCardColor);

        mDragHandle.setVisibility(View.VISIBLE);
        mDragHandle.setColorFilter(mDragHandleInactiveColor);

        mItemTextTv.setVisibility(View.VISIBLE);
        mHoleLayout.setBgStyle(TransparentCircleLayout.BgStyle.SOLID);
    }

    private void setDropZoneState() {
        mDragHandle.setVisibility(View.GONE);
        mItemTextTv.setVisibility(View.GONE);
        mHoleLayout.setBgStyle(TransparentCircleLayout.BgStyle.DASH);
    }

    /**
     * Changes view state.
     */
    public void setState(State state) {
        switch (state) {
            case DRAGGED:
                setDraggedState();
                break;
            case DROP_ZONE:
                setDropZoneState();
                break;
            case NORMAL:
            default:
                setNormalState();
        }
    }

    public enum State {
        NORMAL, DRAGGED, DROP_ZONE
    }
}
