package com.google.android.apps.miyagi.development.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final long DEFAULT_STEP_DELAY = 120;

    protected final LayoutInflater mInflater;
    protected Context mContext;
    protected OnItemSelectedListener<T> mOnItemClickListener;
    protected RecyclerView mRecyclerView;

    private List<T> mItems;
    private int mLastAnimatedPosition = -1;
    private boolean mIsEntryDelayed = true;
    private long mStepDelay = DEFAULT_STEP_DELAY;

    public BaseAdapter(Context context, List<T> items, OnItemSelectedListener<T> listener) {
        mContext = context;
        mItems = items;
        mOnItemClickListener = listener;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Set adapter collection.
     *
     * @param items collection
     */
    public void setItems(List<T> items) {
        mItems = items;
    }

    /**
     * Get adapter collection.
     *
     * @return collection
     */
    public List<T> getItems() {
        return mItems;
    }

    /**
     * Add new item to the adapter collection.
     */
    public void addItem(T item) {
        mItems.add(item);
    }

    /**
     * Add new items to the adapter collection.
     */
    public void addItems(List<T> items) {
        for (T item : items) {
            mItems.add(item);
        }
    }

    /**
     * Clear adapter collection.
     */
    public void clear() {
        if (mItems != null) {
            mItems.clear();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Get item at given position.
     *
     * @param position position of adapter collection
     *
     * @return item at given position
     */
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Returns position of item that was last animated.
     *
     * @return position of last animated item
     */
    public int getLastAnimatedPosition() {
        return mLastAnimatedPosition;
    }

    /**
     * Updates position of item that was last animated.
     *
     * @param lastAnimatedPosition position of last animated item
     */
    public void setLastAnimatedPosition(int lastAnimatedPosition) {
        if (lastAnimatedPosition > mLastAnimatedPosition) {
            mLastAnimatedPosition = lastAnimatedPosition;
        }
    }

    /**
     * Returns whether adapter animates items when they appear for the first time.
     */
    public boolean isEntryDelayed() {
        return mIsEntryDelayed;
    }

    /**
     * Set whether adapter should animate items when they appear for the first time.
     */
    public void setEntryDelayed(boolean entryDelayed) {
        mIsEntryDelayed = entryDelayed;
    }

    /**
     * Returns delay for each item animation.
     */
    public long getStepDelay() {
        return mStepDelay;
    }

    /**
     * Set delay for each item animation.
     */
    public void setStepDelay(long stepDelay) {
        mStepDelay = stepDelay;
    }

}
