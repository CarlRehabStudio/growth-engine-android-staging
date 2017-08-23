package com.google.android.apps.miyagi.development.ui.practice.reorder;

import android.graphics.Canvas;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.google.android.apps.miyagi.development.utils.Lh;

import java.util.List;

/**
 * Created by jerzyw on 06.12.2016.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    private ReorderPracticeViewHolder mDropZoneViewHolder;

    private Integer mFromPosition = null;
    private Integer mToPosition = null;

    private NestedScrollView mNestedScrollView;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source,
                          RecyclerView.ViewHolder target) {
        // save positions: from, to
        if (mFromPosition == null) {
            mFromPosition = source.getAdapterPosition();
        }
        mToPosition = target.getAdapterPosition();

        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return super.canDropOver(recyclerView, current, target);
    }

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        return super.chooseDropTarget(selected, dropTargets, curX, curY);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        ReorderPracticeViewHolder reorderPracticeViewHolder = (ReorderPracticeViewHolder) viewHolder;
        View itemView = reorderPracticeViewHolder.itemView;
        //draw drop zone
        c.save();
        c.translate(0, itemView.getTop());

        if (mDropZoneViewHolder == null) {
            ReorderAdapter reorderAdapter = (ReorderAdapter) recyclerView.getAdapter();
            mDropZoneViewHolder = reorderAdapter.onCreateViewHolder(recyclerView, 0);
            mDropZoneViewHolder.setState(ReorderPracticeViewHolder.State.DROP_ZONE);
        }

        int widthSpec = View.MeasureSpec.makeMeasureSpec(itemView.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(itemView.getMeasuredHeight(), View.MeasureSpec.EXACTLY);
        mDropZoneViewHolder.itemView.measure(widthSpec, heightSpec);
        mDropZoneViewHolder.itemView.layout(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mDropZoneViewHolder.itemView.draw(c);

        c.restore();
        if (isCurrentlyActive) {
            reorderPracticeViewHolder.setState(ReorderPracticeViewHolder.State.DRAGGED);
            mNestedScrollView.scrollBy(0, (int)dY / 15);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        ReorderPracticeViewHolder reorderPracticeViewHolder = (ReorderPracticeViewHolder) viewHolder;
        reorderPracticeViewHolder.setState(ReorderPracticeViewHolder.State.NORMAL);
        super.clearView(recyclerView, viewHolder);

        if (mFromPosition != null && mToPosition != null) {
            mAdapter.onItemDrop(mFromPosition, mToPosition);
        }

        // clear positions: from, to
        mFromPosition = null;
        mToPosition = null;
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public void setNestedScrollView(NestedScrollView nestedScrollView) {
        mNestedScrollView = nestedScrollView;
    }

    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return .2f;
    }
}
