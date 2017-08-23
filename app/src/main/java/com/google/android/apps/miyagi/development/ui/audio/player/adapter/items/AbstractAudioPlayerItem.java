package com.google.android.apps.miyagi.development.ui.audio.player.adapter.items;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by marcinarciszew on 11.01.2017.
 */

public abstract class AbstractAudioPlayerItem<T extends FlexibleViewHolder>
        extends AbstractFlexibleItem<T> {

    static int AUDIO_PLAYER_UNIQUE_ID = 0;
    int mId = ++AUDIO_PLAYER_UNIQUE_ID;

    public AbstractAudioPlayerItem() {
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AbstractAudioPlayerItem) {
            AbstractAudioPlayerItem item = (AbstractAudioPlayerItem) object;
            return mId == item.mId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mId;
    }
}
