package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by jerzyw on 19.12.2016.
 */

public interface AudioServiceApi {

    @GET("api/v1/pages/audio/{id}")
    Observable<AudioResponse> getTopicDataForOfflineMode(@Path("id") int topicId);
}
