package com.google.android.apps.miyagi.development.ui.audio.transcript.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.ExpandableViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Audio Transcript list Class
 */

public class AudioTranscriptListHeaderItem
        extends AbstractAudioTranscriptItem<AudioTranscriptListHeaderItem.ViewHolder>
        implements IExpandable<AudioTranscriptListHeaderItem.ViewHolder, AbstractFlexibleItem>,
        IHeader<AudioTranscriptListHeaderItem.ViewHolder> {

    private final String mTitle;
    private final int mSectionBackgroundColor;
    private boolean mExpanded = false;
    private List<AbstractFlexibleItem> mSubItems;

    /**
     * Creates new audio transcript list header item.
     */
    public AudioTranscriptListHeaderItem(String title, int sectionBackgroundColor) {
        mTitle = title;
        mSectionBackgroundColor = sectionBackgroundColor;
        setDraggable(false);
        setHidden(false);
        setExpanded(false);
    }

    @Override
    public int getExpansionLevel() {
        return 0;
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
    public List<AbstractFlexibleItem> getSubItems() {
        return mSubItems;
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
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }


    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(getLayoutRes(), parent, false);
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.bind(this);
    }

    static final class ViewHolder extends ExpandableViewHolder {

        View mRootView;
        @BindView(R.id.label_title) TextView mLabelTitle;
        @BindView(R.id.image_expand_collapse) ImageView mImageExpandCollapse;

        private AudioTranscriptListHeaderItem mHeaderItem;

        /**
         * Constructs view holder for headers in audio transcript list.
         */
        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            ButterKnife.bind(this, view);
            mRootView = view;
        }

        public void bind(AudioTranscriptListHeaderItem item) {
            mHeaderItem = item;

            mRootView.setBackgroundColor(mHeaderItem.mSectionBackgroundColor);
            mLabelTitle.setText(mHeaderItem.mTitle);

            if (mHeaderItem.isExpanded()) {
                setArrowUpIcon();
            } else {
                setArrowDownIcon();
            }
        }

        @Override
        protected boolean isViewExpandableOnClick() {
            return true;
        }

        @Override
        protected boolean shouldNotifyParentOnClick() {
            return true;
        }

        private void setArrowUpIcon() {
            mImageExpandCollapse.setBackgroundResource(R.drawable.ic_arrow_up);
        }

        private void setArrowDownIcon() {
            mImageExpandCollapse.setBackgroundResource(R.drawable.ic_arrow_down);
        }
    }
}
