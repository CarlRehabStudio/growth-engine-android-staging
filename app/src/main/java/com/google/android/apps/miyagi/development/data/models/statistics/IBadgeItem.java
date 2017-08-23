package com.google.android.apps.miyagi.development.data.models.statistics;

/**
 * Created by Paweł on 2017-03-22.
 */

public interface IBadgeItem {

    int TYPE_SECTION = 1;
    int TYPE_ITEM = 2;

    int getType();
}
