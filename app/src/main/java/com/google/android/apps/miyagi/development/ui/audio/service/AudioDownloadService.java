package com.google.android.apps.miyagi.development.ui.audio.service;

import android.content.Context;
import android.net.Uri;

import com.google.android.apps.miyagi.development.data.models.audio.AudioMetaData;
import com.google.android.apps.miyagi.development.data.net.api.FileDownloadApi;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseValidator;
import com.google.android.apps.miyagi.development.data.net.services.AudioService;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RxErrorHandlingCallAdapterFactory;
import com.google.android.apps.miyagi.development.data.storage.audio.AudioMetaDataDatabase;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;

/**
 * Created by lukaszweglinski on 31.01.2017.
 */

public class AudioDownloadService {

    private static final String STORAGE_DIRECTORY = "mp3";
    private static final String LOOPBACK_BASE_API = "http://127.0.0.1/";
    private AudioMetaDataDatabase mAudioMetaDataDatabase;
    private AudioService mAudioService;
    private FileDownloadApi mDownloadApi;
    private Context mContext;

    /**
     * Instantiates a new AudioDownloadService.
     *
     * @param audioService          AudioService.
     * @param audioMetaDataDatabase AudioMetaDataDatabase.
     * @param context               Context.
     */
    public AudioDownloadService(AudioService audioService, AudioMetaDataDatabase audioMetaDataDatabase, Context context) {
        mAudioService = audioService;
        mAudioMetaDataDatabase = audioMetaDataDatabase;
        mContext = context;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(LOOPBACK_BASE_API)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
        mDownloadApi = retrofit.create(FileDownloadApi.class);
    }

    /**
     * Return save directory.
     * data/data/appname/[directoryName]/
     */
    private static String getMainStorageDirectoryUrl(Context context) {
        return context.getFilesDir() + File.separator + STORAGE_DIRECTORY + File.separator;
    }

    /**
     * For to Delete the directory inside list of files and inner Directory.
     *
     * @param dir - Direcotry to delete.
     *
     * @return true if success, false otherwise.
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * Download AudioMetaData and mp3 file.
     *
     * @param topicId the topic id
     *
     * @return the observable
     */
    public Observable<Boolean> downloadAudio(final int topicId) {
        return mAudioService.getTopicAudio(topicId)
                //get info data
                .map(AudioResponseValidator::validate)
                .map(topicAudioInfoResponse -> {
                    AudioResponseData info = topicAudioInfoResponse.getResponseData();
                    AudioMetaData audioMetaData = new AudioMetaData();
                    audioMetaData.setAudioResponseData(info);
                    audioMetaData.setTopicId(topicId);
                    return audioMetaData;
                })
                //download and save mp3 file
                .flatMap(new Func1<AudioMetaData, Observable<AudioMetaData>>() {
                    @Override
                    public Observable<AudioMetaData> call(AudioMetaData audioMetaData) {
                        AudioResponseData info = audioMetaData.getAudioResponseData();

                        String audioFileUrl = info.getFileUrl();
                        Uri uri = Uri.parse(audioFileUrl);
                        String audioFileName = uri.getLastPathSegment();

                        File storageDir = new File(getMainStorageDirectoryUrl(mContext));
                        storageDir.mkdirs();

                        String topicDirName = String.valueOf(topicId);
                        final File topicDataDirectory = new File(storageDir, topicDirName);
                        topicDataDirectory.mkdir();

                        return mDownloadApi.downloadFile(audioFileUrl)
                                .observeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .flatMap(responseBody -> saveMp3ToFile(topicDataDirectory, responseBody, audioFileName))
                                .map(filePath -> {
                                    if (filePath == null) {
                                        throw new RuntimeException("Can't save audio file");
                                    }
                                    audioMetaData.setAudioFilePath(filePath);
                                    mAudioMetaDataDatabase.saveAudioMetaData(audioMetaData);
                                    return audioMetaData;
                                })
                                .doOnError(throwable -> {
                                    //clean up
                                    topicDataDirectory.delete();
                                });
                    }
                })
                .map(audioMetaData -> true);
    }

    private Observable<String> saveMp3ToFile(File storageDirectory, ResponseBody body, String fileName) {
        return Observable.create(subscriber -> {
            try {
                File file = new File(storageDirectory, fileName);
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(body.source());
                sink.close();
                subscriber.onNext(file.getPath());
            } catch (IOException ex) {
                subscriber.onError(ex);
            } finally {
                body.close();
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Delete AudioMetaData and mp3file for topic id.
     *
     * @param topicId the topic id
     *
     * @return the observable
     */
    public Observable<Boolean> deleteAudio(int topicId) {
        return Observable.defer(() -> mAudioMetaDataDatabase.getAudioMetaDataForTopicId(topicId)
                .map(audioMetaData -> {
                    File rootDir = new File(getMainStorageDirectoryUrl(mContext));
                    String topicDirName = String.valueOf(topicId);
                    File topicDataDirectory = new File(rootDir, topicDirName);
                    deleteDir(topicDataDirectory);
                    return audioMetaData;
                })
                .flatMap(audioMetaData -> mAudioMetaDataDatabase.deleteAudioMetaData(topicId)));
    }

    /**
     * Delete all audio.
     */
    public void deleteAllAudio() {
        File rootDir = new File(getMainStorageDirectoryUrl(mContext));
        deleteDir(rootDir);
    }
}