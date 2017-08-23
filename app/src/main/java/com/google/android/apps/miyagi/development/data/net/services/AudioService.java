package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponse;

import rx.Observable;

/**
 * Created by jerzyw on 19.12.2016.
 */

public interface AudioService extends BaseService {

    Observable<AudioResponse> getTopicAudio(int topicId);
}
