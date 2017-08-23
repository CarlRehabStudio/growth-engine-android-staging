package com.google.android.apps.miyagi.development.helpers;

import com.google.android.apps.miyagi.development.data.storage.cache.CurrentSessionCache;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.audio.service.AudioDownloadService;

/**
 * Created by lukaszweglinski on 05.01.2017.
 */

public class SignOutUserHelper {

    private final ConfigStorage mConfigStorage;
    private final AudioDownloadService mAudioDownloadService;
    private final CurrentSessionCache mCurrentSessionCache;
    private final AudioDownloadPrefs mAudioDownloadPrefs;

    public SignOutUserHelper(ConfigStorage configStorage,
                             AudioDownloadService audioDownloadService,
                             CurrentSessionCache currentSessionCache,
                             AudioDownloadPrefs audioDownloadPrefs) {

        mConfigStorage = configStorage;
        mAudioDownloadService = audioDownloadService;
        mCurrentSessionCache = currentSessionCache;
        mAudioDownloadPrefs = audioDownloadPrefs;
    }

    /**
     * Clears all cached data.
     */
    public void signOut() {
        mAudioDownloadPrefs.clearData();
        mConfigStorage.clearAllData();
        mCurrentSessionCache.clearAllData();
        mAudioDownloadService.deleteAllAudio();
        new CertificateDownloadHelper().deleteCertificate();
    }
}
