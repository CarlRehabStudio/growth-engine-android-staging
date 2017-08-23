package com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.ExpandStateListener;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.AbstractDashboardItem;
import com.google.android.apps.miyagi.development.utils.Lh;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.ExpandableViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TopicGroupItem
        extends AbstractDashboardItem<TopicGroupItem.GroupViewHolder>
        implements IExpandable<TopicGroupItem.GroupViewHolder, AbstractFlexibleItem>,
        IHeader<TopicGroupItem.GroupViewHolder> {


    private final String mTopicTitle;
    private final int mSectionBackgroundColor;
    private boolean mExpanded = false;
    private List<AbstractFlexibleItem> mSubItems;
    private ExpandStateListener mExpandStateListener;

    /**
     * Constructs new instance of TopicGroupItem.
     */
    public TopicGroupItem(String topicTitle, int sectionBackgroundColor) {
        mTopicTitle = topicTitle;
        mSectionBackgroundColor = sectionBackgroundColor;
        setDraggable(false);
        setHidden(false);
        setExpanded(true);
        setSelectable(false);
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    @Override
    public int getExpansionLevel() {
        return 0;
    }

    @Override
    public List<AbstractFlexibleItem> getSubItems() {
        return mSubItems;
    }

    public void setExpandListener(ExpandStateListener listener) {
        mExpandStateListener = listener;
    }

    /**
     * Appends the specified item to the end of the sub items list.
     */
    public void addSubItem(AbstractFlexibleItem subItem) {
        if (mSubItems == null) {
            mSubItems = new ArrayList<>();
        }
        mSubItems.add(subItem);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.expandable_header_item;
    }

    @Override
    public GroupViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }


    public GroupViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(getLayoutRes(), parent, false);
        return new TopicGroupItem.GroupViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, GroupViewHolder holder, int position, List payloads) {
        holder.bind(this);
    }

    public String getTitle() {
        return mTopicTitle;
    }

    public int getSectionBackgroundColor() {
        return mSectionBackgroundColor;
    }

    static final class GroupViewHolder extends ExpandableViewHolder {

        View mRootView;
        @BindView(R.id.label_title) TextView mLabelTitle;
        @BindView(R.id.image_expand_collapse) ImageView mIconExpand;
        @BindView(R.id.divider_top) View mDivider;

        private TopicGroupItem mTopicGroupItem;

        /**
         * Constructs view holder for headers on dashboard list.
         */
        GroupViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            ButterKnife.bind(this, view);
            mRootView = view;
        }

        public void bind(TopicGroupItem item) {
            mTopicGroupItem = item;

            mRootView.setBackgroundColor(mTopicGroupItem.getSectionBackgroundColor());
            mLabelTitle.setText(mTopicGroupItem.getTitle());

            if (mTopicGroupItem.isExpanded()) {
                setArrowUpIcon();
            } else {
                setArrowDownIcon();
            }

            Lh.d("TESTDASH", "position: " + getAdapterPosition());
            if (getAdapterPosition() == 0) {
                mDivider.setVisibility(View.INVISIBLE);
            } else {
                mDivider.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected boolean isViewExpandableOnClick() {
            return true;
        }

        @Override
        protected void expandView(int position) {
            super.expandView(position);
            // Let's notify the item has been expanded
            if (mAdapter.isExpanded(position)) {
                setArrowUpIcon();
            }
            if (mTopicGroupItem.mExpandStateListener != null) {
                mTopicGroupItem.mExpandStateListener.onExpanded();
            }
        }

        @Override
        protected void collapseView(int position) {
            super.collapseView(position);

            // Let's notify the item has been collapsed
            if (!mAdapter.isExpanded(position)) {
                setArrowDownIcon();
            }
        }

        private void setArrowUpIcon() {
            mIconExpand.setBackgroundResource(R.drawable.ic_arrow_up);
        }

        private void setArrowDownIcon() {
            mIconExpand.setBackgroundResource(R.drawable.ic_arrow_down);
        }
    }
}
