package com.google.android.apps.miyagi.development.ui.result;

import android.support.annotation.Nullable;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.AdditionalLink;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcinarciszew on 07.12.2016.
 */

public class ResultLinkAdapter extends FlexibleAdapter<ResultLinkItem> {

    public ResultLinkAdapter(@Nullable List<ResultLinkItem> items, @Nullable Object listeners) {
        super(items, listeners);
    }

    /**
     * Creates adapter with links for result screen section: additional links.
     */
    public static ResultLinkAdapter create(List<AdditionalLink> links) {
        List<ResultLinkItem> items = new ArrayList<>();
        for (int i = 0; i < links.size(); i++) {
            items.add(new ResultLinkItem(links.get(i)));
        }
        return new ResultLinkAdapter(items, null);
    }
}
