package com.google.android.apps.miyagi.development.ui.audio.player.adapter.items;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.ui.offline.adapter.item.OfflineListTopicItem;

/**
 * Created by lukaszweglinski on 05.04.2017.
 */

public class AudioOfflineListTopicItem extends OfflineListTopicItem {
    public AudioOfflineListTopicItem(AudioResponseData audioMetaData, int mainCtaColor) {
        super(audioMetaData, mainCtaColor);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.audio_player_offline_list_topic_item;
    }
}
