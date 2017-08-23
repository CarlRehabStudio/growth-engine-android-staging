package com.google.android.apps.miyagi.development.data.storage.audio;

import com.google.android.apps.miyagi.development.data.models.audio.AudioMetaData;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.helpers.realm.RealmObservable;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

import java.io.File;
import java.util.List;

/**
 * Created by lukaszweglinski on 03.02.2017.
 */

public class AudioMetaDataDatabase {

    private static final String TOPIC_KEY = "mTopicId";
    private final RealmConfiguration mConfig;

    /**
     * Instantiates a new AudioMetaDataDatabase.
     */
    public AudioMetaDataDatabase() {
        mConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /**
     * Save AudioMetaData in realm database.
     *
     * @param audioMetaData AudioMetaData to save.
     */
    public void saveAudioMetaData(AudioMetaData audioMetaData) {
        Realm realmInstance = Realm.getInstance(mConfig);
        realmInstance.executeTransaction(realm -> {
            RealmResults<AudioMetaData> audioMetaDataRealmResults = realm
                    .where(AudioMetaData.class)
                    .equalTo(TOPIC_KEY, audioMetaData.getTopicId())
                    .findAll();

            for (AudioMetaData audioResult : audioMetaDataRealmResults) {
                //cascade delete old AudioMetaData object
                AudioResponseData audioResponseData = audioResult.getAudioResponseData();
                audioResponseData.getLessons().deleteAllFromRealm();
                audioResponseData.getIcon().deleteFromRealm();
                audioResponseData.deleteFromRealm();
                audioResult.deleteFromRealm();
            }

            realm.copyToRealmOrUpdate(audioMetaData);
        });
        realmInstance.close();
    }

    /**
     * Gets saved AudioMetaData topics id list.
     *
     * @return the saved topics id list.
     */
    public Observable<List<Integer>> getSavedTopicsIdList() {
        return RealmObservable.list(realm -> {
            RealmResults<AudioMetaData> result = realm
                    .where(AudioMetaData.class)
                    .findAllSorted(TOPIC_KEY);
            return realm.copyFromRealm(result);
        })
                .flatMapIterable(a -> a)
                .filter(audioMetaData -> new FileValidator().call(audioMetaData.getAudioFilePath()))
                .map(AudioMetaData::getTopicId)
                .toList();
    }

    /**
     * Gets all saved topics.
     *
     * @return List of saved topics.
     */
    public Observable<List<AudioMetaData>> getSavedTopics() {
        return RealmObservable.object(realm -> {
            RealmResults<AudioMetaData> result = realm
                    .where(AudioMetaData.class)
                    .findAllSorted(TOPIC_KEY);
            return realm.copyFromRealm(result);
        })
                .flatMapIterable(a -> a)
                .filter(audioMetaData -> new FileValidator().call(audioMetaData.getAudioFilePath()))
                .toList();
    }

    /**
     * Gets saved AudioMetaData for topic id.
     *
     * @param topicId the topic id.
     * @return the AudioMetaData for topic id.
     */
    public Observable<AudioMetaData> getAudioMetaDataForTopicId(int topicId) {
        return RealmObservable.object(realm -> {
            AudioMetaData result = realm
                    .where(AudioMetaData.class)
                    .equalTo(TOPIC_KEY, topicId)
                    .findFirst();
            if (result != null) {
                return realm.copyFromRealm(result);
            } else {
                return null;
            }
        });
    }

    /**
     * Delete AudioMetaData with topic id.
     */
    public Observable<Boolean> deleteAudioMetaData(int topicId) {
        return RealmObservable.object(realm -> {
            RealmResults<AudioMetaData> audioMetaDataRealmResults = realm
                    .where(AudioMetaData.class)
                    .equalTo(TOPIC_KEY, topicId)
                    .findAll();

            for (AudioMetaData audioResult : audioMetaDataRealmResults) {
                //cascade delete old AudioMetaData object
                AudioResponseData audioResponseData = audioResult.getAudioResponseData();
                audioResponseData.getLessons().deleteAllFromRealm();
                audioResponseData.getIcon().deleteFromRealm();
                audioResponseData.deleteFromRealm();
                audioResult.deleteFromRealm();
            }
            return true;
        });
    }

    /**
     * Find next lesson audio response data and validate file exist.
     *
     * @param topicId the topic id.
     * @return the AudioResponseData.
     */
    public Observable<AudioResponseData> findNextLesson(int topicId) {
        return RealmObservable.object(realm -> {
            RealmResults<AudioMetaData> realmResults = realm
                    .where(AudioMetaData.class)
                    .findAllSorted(TOPIC_KEY);

            for (AudioMetaData result : realmResults) {
                if (result.getTopicId() > topicId) {
                    if (new File(result.getAudioFilePath()).exists()) {
                        return realm.copyFromRealm(result.getAudioResponseData());
                    }
                }
            }
            return null;
        });
    }

    private class FileValidator implements Func1<String, Boolean> {

        @Override
        public Boolean call(String path) {
            return new File(path).exists();
        }
    }
}
