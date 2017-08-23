package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lukaszweglinski on 01.03.2017.
 */

public class AudioDownloadPrefs {

    private static final String PREF_KEY = "AudioDownload";
    private static final String PREF_VALUE = "audio_download";
    private Context mContext;

    public AudioDownloadPrefs(Context context) {
        mContext = context;
    }

    /**
     * Is first audio download return true if yes.
     *
     * @return true if first audio download, false otherwise.
     */
    public boolean isFirstAudioDownload() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(PREF_VALUE, true);
        if (value) {
            sharedPreferences.edit().putBoolean(PREF_VALUE, false).apply();
        }
        return value;
    }

    /**
     * Clear AudioDownload preferences data.
     */
    public void clearData() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
