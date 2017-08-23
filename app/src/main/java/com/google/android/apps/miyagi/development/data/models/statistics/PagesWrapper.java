package com.google.android.apps.miyagi.development.data.models.statistics;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

@Parcel(converter = ItemListParcelConverter.class)
public class PagesWrapper extends ArrayList<Page> {

    /**
     * Returns page based on type: PLAN or CERTIFICATION.
     */
    public Page getPageFromType(StatisticsPageType type) {
        for (Page page : this) {
            if (page.getType().equals(type.getType())) {
                return page;
            }
        }
        return null;
    }
}
