package com.google.android.apps.miyagi.development.ui.practice.reorder;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jerzyw on 09.12.2016.
 */

public class PositionItemDecorator extends RecyclerView.ItemDecoration {


    private final TextPaint mTextPaint;
    private final int mLabelPaddingPx;

    /**
     * PositionItemDecorator constructor.
     */
    PositionItemDecorator(int textSize, int textColor, int labelXPadding) {
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
        mLabelPaddingPx = labelXPadding;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.ViewHolder viewHolder;
        int childCount = parent.getChildCount();

        int startPosition = 0;
        if (childCount > 0) {
            View firstChild = parent.getChildAt(0);
            viewHolder = parent.getChildViewHolder(firstChild);
            startPosition = viewHolder.getLayoutPosition() + 1;
        }

        String labelText;
        float textWidthHalf;
        float textHeightHalf;
        int top;
        int left;
        for (int i = 0; i < childCount; i++) {
            ViewGroup child = ((ViewGroup) parent.getChildAt(i));
            View childCircleLayout = child.getChildAt(0);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getTop() + params.topMargin;
            left = childCircleLayout.getLeft();

            labelText = String.valueOf(startPosition + i);
            textWidthHalf = mTextPaint.measureText(labelText) * 0.5f;
            textHeightHalf = mTextPaint.ascent() * 0.5f;

            canvas.drawText(
                    labelText,
                    mLabelPaddingPx - textWidthHalf + left,
                    (int) (top + childCircleLayout.getPaddingTop() + ((childCircleLayout.getHeight() - childCircleLayout.getPaddingTop() - childCircleLayout.getPaddingBottom()) * 0.5f) - textHeightHalf),
                    mTextPaint);
        }
    }
}
