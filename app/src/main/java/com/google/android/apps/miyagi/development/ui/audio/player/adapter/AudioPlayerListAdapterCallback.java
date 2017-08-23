package com.google.android.apps.miyagi.development.ui.audio.player.adapter;

import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;

/**
 * Created by marcin on 20.01.2017.
 */

public interface AudioPlayerListAdapterCallback {

    void onLessonItemClick(AudioLesson audioLesson);

    void onNextLessonChange(int position);

}
