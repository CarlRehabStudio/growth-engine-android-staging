package com.google.android.apps.miyagi.development.ui.dashboard.navigation.items;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.menu.LegalPage;
import com.google.android.apps.miyagi.development.data.models.dashboard.LegalItem;
import com.google.android.apps.miyagi.development.data.models.dashboard.LegalItemType;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 05.01.2017.
 */

public class LegalMenuAdapter extends RecyclerView.Adapter<LegalMenuAdapter.ViewHolder> {

    private final LayoutInflater mInflater;

    private final List<LegalItem> mData = new ArrayList<>();

    @Nullable
    private OnItemSelectedListener<LegalItem> mOnItemSelectedExternalListener;
    private final OnItemSelectedListener<LegalItem> mOnItemSelectedInternalListener = new OnItemSelectedListener<LegalItem>() {
        @Override
        public void onItemSelected(LegalItem item) {
            if (mOnItemSelectedExternalListener != null) {
                mOnItemSelectedExternalListener.onItemSelected(item);
            }
        }
    };

    public LegalMenuAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Sets data to adapter.
     *
     * @param data - array of data. Can be null.
     */
    public void setData(@Nullable LegalPage data) {
        mData.clear();
        if (data != null) {
            List<LegalItem> temp = new ArrayList<>();
            if (!TextUtils.isEmpty(data.getPolicyLink()) && !TextUtils.isEmpty(data.getPolicyText())) {
                temp.add(new LegalItem.Builder()
                        .withType(LegalItemType.URL)
                        .withTitle(data.getPolicyText())
                        .withUrl(data.getPolicyLink())
                        .build());
            }
            if (!TextUtils.isEmpty(data.getTermsLink()) && !TextUtils.isEmpty(data.getTermsText())) {
                temp.add(new LegalItem.Builder()
                        .withType(LegalItemType.URL)
                        .withTitle(data.getTermsText())
                        .withUrl(data.getTermsLink())
                        .build());
            }
            if (!TextUtils.isEmpty(data.getImpressumLink()) && !TextUtils.isEmpty(data.getImpressumText())) {
                temp.add(new LegalItem.Builder()
                        .withType(LegalItemType.URL)
                        .withTitle(data.getImpressumText())
                        .withUrl(data.getImpressumLink())
                        .build());
            }
            temp.add(new LegalItem.Builder()
                    .withType(LegalItemType.LICENCE)
                    .withTitle(data.getLicenceText())
                    .build());
            mData.addAll(temp);
        }
        notifyDataSetChanged();
    }

    @Override
    public LegalMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.legal_menu_item, parent, false);
        return new ViewHolder(view, mOnItemSelectedInternalListener);
    }

    @Override
    public void onBindViewHolder(LegalMenuAdapter.ViewHolder holder, int position) {
        holder.populateWithData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<LegalItem> onItemSelectedListener) {
        mOnItemSelectedExternalListener = onItemSelectedListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final OnItemSelectedListener<LegalItem> mOnItemSelectedListener;
        private final TextView mTextView;
        private LegalItem mItem;

        public ViewHolder(View itemView, OnItemSelectedListener<LegalItem> onItemSelectedListener) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.legal_title_item);
            mOnItemSelectedListener = onItemSelectedListener;
            itemView.setOnClickListener(v -> mOnItemSelectedListener.onItemSelected(mItem));
        }

        void populateWithData(LegalItem item) {
            this.mItem = item;
            mTextView.setText(item.getTitle());
        }
    }
}
