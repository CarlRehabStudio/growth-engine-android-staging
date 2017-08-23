package com.google.android.apps.miyagi.development.ui.practice.reorder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeAnswerOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder.ReorderPracticeColors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukaszweglinski on 16.11.2016.
 */

class ReorderAdapter extends RecyclerView.Adapter<ReorderPracticeViewHolder> implements ItemTouchHelperAdapter {

    private final int mActiveCardColor;
    private final int mActiveTextColor;
    private final int mInactiveCardColor;
    private final int mInactiveTextColor;
    private final int mDragHandleActiveColor;
    private final int mDragHandleInactiveColor;

    private List<ReorderPracticeAnswerOption> mDataItems = new ArrayList<>();

    private Submittable mSubmittableCallback;

    private OnStartDragListener mDragStartListener;

    public ReorderAdapter(ReorderPracticeColors colors, OnStartDragListener dragStartListener) {
        mActiveCardColor = colors.getActiveCardColor();
        mActiveTextColor = colors.getActiveTextColor();
        mInactiveCardColor = colors.getInactiveCardColor();
        mInactiveTextColor = colors.getInactiveTextColor();
        mDragHandleActiveColor = colors.getDragHandleActiveColor();
        mDragHandleInactiveColor = colors.getDragHandleInactiveColor();
        mDragStartListener = dragStartListener;
    }

    public void setData(List<ReorderPracticeAnswerOption> data) {
        mDataItems.clear();
        if (data != null) {
            mDataItems.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<ReorderPracticeAnswerOption> getItems() {
        return mDataItems;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        //not used
    }

    @Override
    public void onItemDrop(int fromPosition, int toPosition) {
        mSubmittableCallback.submit(fromPosition != toPosition);
    }

    @Override
    public ReorderPracticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.practice_reorder_item, parent, false);
        return new ReorderPracticeViewHolder(itemView,
                mActiveCardColor,
                mActiveTextColor,
                mInactiveCardColor,
                mInactiveTextColor,
                mDragHandleActiveColor,
                mDragHandleInactiveColor);
    }

    @Override
    public void onBindViewHolder(ReorderPracticeViewHolder holder, int position) {
        holder.populateWithData(mDataItems.get(position), mDragStartListener);
    }

    @Override
    public int getItemCount() {
        return mDataItems.size();
    }

    public ReorderPracticeAnswerOption getItemData(int position) {
        return mDataItems.get(position);
    }

    public void setSubmittableCallback(Submittable callback) {
        mSubmittableCallback = callback;
    }
}
