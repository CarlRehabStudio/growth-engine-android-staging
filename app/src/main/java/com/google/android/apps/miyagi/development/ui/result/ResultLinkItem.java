package com.google.android.apps.miyagi.development.ui.result;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.AdditionalLink;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

/**
 * Result Link functionality class
 */

class ResultLinkItem extends AbstractFlexibleItem<ResultLinkItem.ViewHolder> {

    private final AdditionalLink mAdditionalLink;

    ResultLinkItem(AdditionalLink additionalLink) {
        mAdditionalLink = additionalLink;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.result_link_item;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }


    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.bind(mAdditionalLink);
    }

    public class ViewHolder extends FlexibleViewHolder {
        private TextView mLink;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            mLink = (TextView) view.findViewById(R.id.result_label_link);
        }

        public void bind(AdditionalLink link) {
            mLink.setText(HtmlHelper.fromHtml(link.getLinkText()));
            mLink.setOnClickListener(view -> {
                try {
                    Uri uri = Uri.parse(link.getLinkUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Context context = mLink.getContext();
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                } catch (Exception ex) {
                    // ignore
                }
            });
        }
    }
}
