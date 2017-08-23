package com.google.android.apps.miyagi.development.ui.audio.transcript.items;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by marcin on 21.01.2017.
 */

public abstract class AbstractAudioTranscriptItem<T extends FlexibleViewHolder>
        extends AbstractFlexibleItem<T> {

    static int AUDIO_TRANSCRIPT_UNIQUE_ID = 0;
    int mId = ++AUDIO_TRANSCRIPT_UNIQUE_ID;

    public AbstractAudioTranscriptItem() {
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AbstractAudioTranscriptItem) {
            AbstractAudioTranscriptItem item = (AbstractAudioTranscriptItem) object;
            return mId == item.mId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mId;
    }
}
