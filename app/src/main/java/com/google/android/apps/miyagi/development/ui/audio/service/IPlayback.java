package com.google.android.apps.miyagi.development.ui.audio.service;

import android.support.v4.media.session.MediaSessionCompat;

/**
 * Created by lukaszweglinski on 24.01.2017.
 */

public interface IPlayback {

    IPlayback EMPTY = new IPlayback() {

        @Override
        public void prepare(String file, boolean playAfterPrepare) {

        }

        @Override
        public void play() {

        }

        @Override
        public void pause() {

        }

        @Override
        public void togglePlayPause() {

        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public int getCurrentStreamPosition() {
            return 0;
        }

        @Override
        public int getCurrentStreamDuration() {
            return 0;
        }

        @Override
        public void updateLastKnownStreamPosition() {

        }

        @Override
        public void seekTo(int progress) {

        }

        @Override
        public void forward() {

        }

        @Override
        public void backward() {

        }

        @Override
        public void addCallback(Callback callback) {

        }

        @Override
        public void removeCallback() {

        }

        @Override
        public int getState() {
            return 0;
        }

        @Override
        public MediaSessionCompat getMediaSession() {
            return null;
        }
    };

    void prepare(String file, boolean playAfterPrepare);

    void play();

    void pause();

    void togglePlayPause();

    boolean isPlaying();

    int getCurrentStreamPosition();

    int getCurrentStreamDuration();

    void updateLastKnownStreamPosition();

    void seekTo(int progress);

    void forward();

    void backward();

    void addCallback(Callback callback);

    void removeCallback();

    int getState();

    MediaSessionCompat getMediaSession();

    interface Callback {
        Callback EMPTY = new Callback() {
            @Override
            public void onPlaybackStatusChanged(int state) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onPrepared() {

            }

            @Override
            public void onError() {

            }
        };

        void onPlaybackStatusChanged(int state);

        void onComplete();

        void onPrepared();

        void onError();
    }
}
