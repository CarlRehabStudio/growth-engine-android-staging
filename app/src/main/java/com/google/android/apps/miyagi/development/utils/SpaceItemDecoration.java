package com.google.android.apps.miyagi.development.utils;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by Lukasz on 03.03.2017.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mVerticalSpaceSize;
    private int mHorizontalSpaceSize;
    private int mOutRectBottomPadding;
    private int mItemsCount;
    private boolean mIncludeEdge = false;

    public SpaceItemDecoration(int verticalSpaceSize) {
        this(verticalSpaceSize, 0, false, 0);
    }

    public SpaceItemDecoration(int verticalSpaceSize, boolean includeEdge) {
        this(verticalSpaceSize, 0, includeEdge, 0);
    }

    public SpaceItemDecoration(int verticalSpaceSize, int horizontalSpaceSize, boolean includeEdge) {
        this(verticalSpaceSize, horizontalSpaceSize, includeEdge, 0);
    }

    public SpaceItemDecoration(int verticalSpaceSize, int horizontalSpaceSize, boolean includeEdge, int outRectBottomPadding) {
        mVerticalSpaceSize = verticalSpaceSize;
        mHorizontalSpaceSize = horizontalSpaceSize;
        mOutRectBottomPadding = outRectBottomPadding;
        mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        mItemsCount = parent.getAdapter().getItemCount();

        int position = parent.getChildAdapterPosition(view);
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = layoutManager.getSpanCount();
            int column = position % spanCount;
            getGridItemOffsets(outRect, position, column, spanCount);
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            int spanCount = layoutManager.getSpanCount();
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int column = lp.getSpanIndex();
            getGridItemOffsets(outRect, position, column, spanCount);
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == OrientationHelper.VERTICAL) {
                if (mIncludeEdge) {
                    outRect.bottom = mVerticalSpaceSize;
                } else if (position == mItemsCount - 1) {
                    outRect.bottom = mOutRectBottomPadding;
                } else {
                    outRect.bottom = mVerticalSpaceSize;
                }
            } else {
                if (mIncludeEdge) {
                    outRect.right = mHorizontalSpaceSize;
                } else if (position != mItemsCount - 1) {
                    outRect.right = mHorizontalSpaceSize;
                }
            }
        }
    }

    private void getGridItemOffsets(Rect outRect, int position, int column, int spanCount) {
        if (mIncludeEdge) {
            outRect.left = mHorizontalSpaceSize * (spanCount - column) / spanCount;
            outRect.right = mHorizontalSpaceSize * (column + 1) / spanCount;
            if (position < spanCount) {
                outRect.top = mHorizontalSpaceSize;
            }
            outRect.bottom = mVerticalSpaceSize;
        } else {
            outRect.left = mHorizontalSpaceSize * column / spanCount;
            outRect.right = mHorizontalSpaceSize * (spanCount - 1 - column) / spanCount;
            if (position >= spanCount) {
                outRect.top = mVerticalSpaceSize;
            }
            if (itemInLastRow(position, spanCount)) {
                outRect.bottom = mOutRectBottomPadding;
            }
        }
    }

    private boolean itemInLastRow(int position, int spanCount) {
        if (mItemsCount == 0) {
            return false;
        }
        int firstIndexLastRow = mItemsCount - spanCount;
        return position - firstIndexLastRow >= 0;
    }
}
