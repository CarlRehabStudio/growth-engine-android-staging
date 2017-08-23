package com.google.android.apps.miyagi.development.ui.dashboard.navigation.items;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import java.util.List;

/**
 * Created by lukaszweglinski on 05.01.2017.
 */

public class LicencesAdapter extends RecyclerView.Adapter<LicencesAdapter.ViewHolder> {

    private final LayoutInflater mInflater;

    private final List<Pair<String, Integer>> mData;

    public LicencesAdapter(Context context, List<Pair<String, Integer>> items) {
        mInflater = LayoutInflater.from(context);
        mData = items;
    }

    @Nullable
    private OnItemSelectedListener<Pair<String, Integer>> mOnItemSelectedExternalListener;
    private final OnItemSelectedListener<Pair<String, Integer>> mOnItemSelectedInternalListener = new OnItemSelectedListener<Pair<String, Integer>>() {
        @Override
        public void onItemSelected(Pair<String, Integer> item) {
            if (mOnItemSelectedExternalListener != null) {
                mOnItemSelectedExternalListener.onItemSelected(item);
            }
        }
    };

    @Override
    public LicencesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.legal_menu_item, parent, false);
        return new ViewHolder(view, mOnItemSelectedInternalListener);
    }

    @Override
    public void onBindViewHolder(LicencesAdapter.ViewHolder holder, int position) {
        holder.populateWithData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<Pair<String, Integer>> onItemSelectedListener) {
        mOnItemSelectedExternalListener = onItemSelectedListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final OnItemSelectedListener<Pair<String, Integer>> mOnItemSelectedListener;
        private final TextView mTextView;
        private Pair<String, Integer> mItem;

        public ViewHolder(View itemView, OnItemSelectedListener<Pair<String, Integer>> onItemSelectedListener) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.legal_title_item);
            mOnItemSelectedListener = onItemSelectedListener;
            itemView.setOnClickListener(v -> mOnItemSelectedListener.onItemSelected(mItem));
        }

        void populateWithData(Pair<String, Integer> item) {
            this.mItem = item;
            mTextView.setText(item.first);
        }
    }
}
