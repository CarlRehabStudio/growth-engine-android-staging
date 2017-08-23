package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.AudioServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponse;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by jerzyw on 19.12.2016.
 */

public class AudioServiceImpl extends AbsBaseService<AudioServiceApi> implements AudioService {

    public AudioServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, AudioServiceApi.class);
    }

    @Override
    public Observable<AudioResponse> getTopicAudio(int topicId) {
        return mApi.getTopicDataForOfflineMode(topicId).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

}
