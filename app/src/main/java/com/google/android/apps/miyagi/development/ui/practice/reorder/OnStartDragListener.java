package com.google.android.apps.miyagi.development.ui.practice.reorder;

/**
 * Listener for manual initiation of a drag.
 */
interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(ReorderPracticeViewHolder viewHolder);
}
