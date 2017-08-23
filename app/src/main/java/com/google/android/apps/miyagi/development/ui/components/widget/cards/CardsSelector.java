package com.google.android.apps.miyagi.development.ui.components.widget.cards;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;

/**
 * Created by lukaszweglinski on 06.12.2016.
 */

public class CardsSelector extends BaseMeasureSpecAdapterView implements CardCallback {

    public static final int CARD_ANIMATION_DURATION = 400;
    public static final int CARD_POSITION_ANIMATION_DURATION = 200;
    public static final float DEFAULT_CARD_SPACING_DP = 16;
    public static final float DEFAULT_X_OFFSET_DP = 60;
    public static final int DEFAULT_ROTATION_DEGREES = 10;
    private static final int NUMBER_OF_SIMULTANEOUS_CARDS = 6;
    private final int mMaxContentWidth;

    private Adapter mAdapter;
    private DataSetObserver mObserver;
    private CardStack<CardContainer> mCardStack;

    private int mAdapterIndex = 0;
    private float mCardOffset = 0f;
    private float mCardSpacing;
    private int mXoffset;
    private int mRotate;
    private int mMaxHeight;
    private int mFirstItemIndex = 0;
    private boolean mInLayout;

    /**
     * Instantiates a new Cards selector, who push card from front to back.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CardsSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray att = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardsSelector, 0, 0);
        try {
            mMaxContentWidth = (int) att.getDimension(R.styleable.CardsSelector_max_width, 0);
            mCardSpacing = att.getDimension(R.styleable.CardsSelector_card_spacing, ViewUtils.dp2px(context, DEFAULT_CARD_SPACING_DP));
            mXoffset = (int) att.getDimension(R.styleable.CardsSelector_card_spacing, ViewUtils.dp2px(context, DEFAULT_X_OFFSET_DP));
            mRotate = att.getInt(R.styleable.CardsSelector_rotation_degrees, DEFAULT_ROTATION_DEGREES);
        } finally {
            att.recycle();
        }

        mCardStack = new CardStack<>(item -> refreshDeck(false));

        //set clipping of view parent to false so cards render outside their view boundary
        //make sure not to clip to padding
        setClipToPadding(false);
        setClipChildren(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int itemCount = mAdapter.getCount();
        if (itemCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED)) {
            final int paddingLeft = getPaddingLeft();
            final int paddingRight = getPaddingRight();

            for (int i = 0; i < itemCount; i++) {
                View view = mAdapter.getView(i, null, this);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                int childWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                        (paddingLeft + paddingRight + params.leftMargin + params.rightMargin + (int) mCardSpacing),
                        params.width);

                view.measure(childWidthSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                if (mMaxHeight < view.getMeasuredHeight()) {
                    mMaxHeight = view.getMeasuredHeight();
                }
            }
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = getPaddingTop() + getPaddingBottom() + mMaxHeight +
                    getVerticalFadingEdgeLength() * 2;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mMaxHeight;
        }

        heightSize += mCardSpacing;

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightSize);
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isInEditMode()) {
            return;
        }

        mInLayout = true;

        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null || mAdapter.getCount() == 0) {
            removeAllViewsInLayout();
            return;
        }

        int deckSize = mCardStack.size();
        int currentPos = mFirstItemIndex;
        for (int i = deckSize; i < NUMBER_OF_SIMULTANEOUS_CARDS; ++i) {
            if (currentPos >= mAdapter.getCount()) {
                currentPos = 0;
            }

            addNextView(currentPos);
            currentPos++;
        }
        renderDeck();

        mInLayout = false;
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
        int contentWidth = getMeasuredWidth();
        return (int) ((contentWidth - mMaxContentWidth) / 2f);
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }

    private void addNextView(int currentPos) {
        if (mAdapterIndex < mAdapter.getCount()) {
            View newBottomChild = mAdapter.getView(currentPos, null, this);
            CardContainer card = new CardContainer(newBottomChild, this, mCardSpacing - mCardOffset, mXoffset, mRotate, currentPos);

            mCardStack.addBack(card);
            mAdapterIndex++;
        }
    }

    private void renderDeck() {
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth();
        int cardWidth;
        int childWidthSpec;

        if (mCardStack.size() > 0) {
            CardContainer container = mCardStack.get(0);
            View card = container.getCard();

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();

            childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    paddingLeft + paddingRight + params.leftMargin + params.rightMargin + (int) mCardSpacing,
                    params.width);

            card.measure(childWidthSpec, MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY));

            cardWidth = card.getMeasuredWidth();
            int cardHeight = mMaxHeight;

            for (int i = mCardStack.size() - 1; i >= 0; --i) {
                View cardItem = mCardStack.get(i).getCard();

                cardItem.measure(childWidthSpec, MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY));

                int childLeft = ((width + paddingLeft + (int) mCardSpacing - paddingRight - cardWidth
                        - (int) mCardOffset) / 2
                        + params.leftMargin - params.rightMargin);

                int childTop = paddingTop + params.topMargin;
                int childBottom = childTop + cardHeight + paddingBottom + params.bottomMargin;

                cardItem.layout(childLeft, childTop, childLeft + cardWidth, childBottom);
            }
        }
        refreshDeck(false);
    }

    private void refreshDeck(boolean animate) {
        removeAllViewsInLayout();
        for (int i = mCardStack.size() - 1; i >= 0; --i) {
            CardContainer container = mCardStack.get(i);
            View card = container.getCard();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
            addViewInLayout(card, -1, params, false);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            requestLayout();
        }
        positionCards(animate);
    }

    private void positionCards(boolean animate) {
        for (int i = 0; i < mCardStack.size(); ++i) {
            animateCardPosition(mCardStack.get(i), mCardStack.get(i).getPositionWithinViewGroup(), animate);
        }
    }

    private void animateCardPosition(CardContainer card, int position, boolean animate) {
        if (!card.isAnimationRunning()) {
            float offset = mCardOffset * position;
            card.getCard().animate()
                    .setDuration(animate ? CARD_POSITION_ANIMATION_DURATION : 0)
                    .translationX(-offset)
                    .translationY(offset)
                    .start();
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    /**
     * Send front stack view to back.
     */
    public void sendFrontToBack() {
        if (mCardStack.size() > 0) {
            CardContainer frontCard = mCardStack.get(0);
            if (!frontCard.getLock().get()) {
                frontCard.swipeCardTop();
                mCardStack.sendFrontToBack();
            }
        }
    }

    @Override
    public void onCardAnimationStart() {

    }

    @Override
    public void onCardAnimationUp() {
        refreshDeck(true);
    }

    @Override
    public void onCardAnimationEnd() {
        refreshDeck(true);
    }

    public int getAdapterPositionOfItem(int stackPosition) {
        return mCardStack.get(stackPosition).getAdapterPosition();
    }

    /**
     * Return current view adapter.
     */
    public Adapter getAdapter() {
        return mAdapter;
    }

    /**
     * Setting adapter to view.
     *
     * @param adapter - Adapter with cards for view
     */
    public void setAdapter(final Adapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        mCardOffset = mCardSpacing / mAdapter.getCount();
        mObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mCardStack.clear();
                mAdapterIndex = 0;
                mCardOffset = mCardSpacing / mAdapter.getCount();
                requestLayout();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                //reset state, remove views and request layout
                mCardStack.clear();
                removeAllViewsInLayout();
                requestLayout();
            }
        };
        adapter.registerDataSetObserver(mObserver);
        removeAllViewsInLayout();
        requestLayout();
    }

    public int getAdapterIndex() {
        return mCardStack.get(0).getAdapterPosition();
    }

    public void setFirstIndex(int adapterIndex) {
        if (adapterIndex > 0) {
            mFirstItemIndex = adapterIndex;
            mAdapterIndex = 0;
        }
    }
}
