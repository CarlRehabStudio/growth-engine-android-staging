package com.google.android.apps.miyagi.development.ui.components.widget.cards;

import java.util.LinkedList;

/**
 * Created by lukaszweglinski on 05.12.2016.
 */

class CardStack<T extends CardContainer> {

    private LinkedList<T> mInternal = new LinkedList<>();
    private DeckEventListener mListener;

    CardStack(DeckEventListener listener) {
        this.mListener = listener;
    }

    void addBack(T t) {
        this.addLast(t);
    }

    /**
     * Send front item to back.
     *
     * @return moved item.
     */
    T sendFrontToBack() {
        T toRemove = mInternal.removeFirst();
        mInternal.addLast(toRemove);
        updateItemPositions();
        return toRemove;
    }

    public T get(int pos) {
        if (size() > 0) {
            return mInternal.get(pos);
        } else {
            return null;
        }
    }

    /**
     * Removes cards progressively from the front.
     */
    public void clear() {
        while (size() > 0) {
            sendFrontToBack();
        }
    }

    /**
     * Removes cards silently.
     */
    public void clearSilent() {
        while (size() > 0) {
            mInternal.clear();
        }
    }

    private void updateItemPositions() {
        for (int i = 0; i < mInternal.size(); i++) {
            mInternal.get(i).setPositionWithinViewGroup(i);
        }
    }

    private void addLast(T t) {
        mInternal.addLast(t);
        updateItemPositions();
        mListener.itemAddedBack(t);
    }

    void addLastSilent(T card) {
        mInternal.addLast(card);
        updateItemPositions();
    }

    public int size() {
        return mInternal.size();
    }

    interface DeckEventListener {
        void itemAddedBack(Object item);
    }
}
