package com.google.android.apps.miyagi.development.ui.audio.player;

import android.content.Context;
import android.content.Intent;

import org.parceler.Parcels;

/**
 * Created by lukaszweglinski on 24.01.2017.
 */
public class AudioPlayerActivity extends AudioPlayerBaseActivity {

    /**
     * Creates calling to AudioPlayerActivity.
     *
     * @param context - the context.
     * @param mode    -  the internet mode.
     * @return the AudioPlayerActivity calling intent.
     */
    public static Intent getCallingIntent(Context context, Mode mode) {
        Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.putExtra(ArgKey.MODE, Parcels.wrap(mode));
        return intent;
    }

    @Override
    protected void onInitUi() {

    }

    @Override
    public void injectSelf(Context context) {
        getApplicationComponent().inject(this);
    }
}