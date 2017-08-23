package com.google.android.apps.miyagi.development.ui.audio.player.adapter.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.ExpandableViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Audio Player header list items
 */

public class AudioPlayerListHeaderItem
        extends AbstractAudioPlayerItem<AudioPlayerListHeaderItem.ViewHolder>
        implements IExpandable<AudioPlayerListHeaderItem.ViewHolder, AbstractFlexibleItem>,
        IHeader<AudioPlayerListHeaderItem.ViewHolder> {

    private final String mTitle;
    private final int mSectionBackgroundColor;
    private boolean mExpanded = false;
    private List<AbstractFlexibleItem> mSubItems;

    /**
     * Creates new audio player list header item.
     */
    public AudioPlayerListHeaderItem(String title, int sectionBackgroundColor) {
        mTitle = title;
        mSectionBackgroundColor = sectionBackgroundColor;
        setDraggable(false);
        setHidden(false);
        setExpanded(false);
        setSelectable(false);
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
        return R.layout.audio_player_list_header_item;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }


    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(getLayoutRes(), parent, false);
        return new AudioPlayerListHeaderItem.ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.bind(this);
    }

    static final class ViewHolder extends ExpandableViewHolder {

        private View mRootView;
        private TextView mLabelTitle;
        private AudioPlayerListHeaderItem mHeaderItem;

        /**
         * Constructs view holder for headers in audio player list.
         */
        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            mLabelTitle = (TextView) view.findViewById(R.id.label_title);
            mRootView = view;
        }

        public void bind(AudioPlayerListHeaderItem item) {
            mHeaderItem = item;
            mRootView.setBackgroundColor(mHeaderItem.mSectionBackgroundColor);
            mLabelTitle.setText(mHeaderItem.mTitle);
        }

        @Override
        protected boolean isViewExpandableOnClick() {
            return false;
        }
    }
}
