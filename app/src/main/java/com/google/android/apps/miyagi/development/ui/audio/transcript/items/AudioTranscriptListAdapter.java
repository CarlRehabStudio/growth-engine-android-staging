package com.google.android.apps.miyagi.development.ui.audio.transcript.items;

import android.support.annotation.Nullable;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcin on 21.01.2017.
 */

public class AudioTranscriptListAdapter
        extends FlexibleAdapter<AbstractFlexibleItem> {

    public AudioTranscriptListAdapter(@Nullable List<AbstractFlexibleItem> items, @Nullable OnItemClickListener listeners) {
        super(new ArrayList<>(items), listeners, true);
    }

}
