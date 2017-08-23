package com.google.android.apps.miyagi.development.ui.practice.reorder;

/**
 * Created by jerzyw on 06.12.2016.
 */

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onItemDrop(int fromPosition, int toPosition);

}
