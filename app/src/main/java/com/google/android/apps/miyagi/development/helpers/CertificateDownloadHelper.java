package com.google.android.apps.miyagi.development.helpers;

import android.os.Environment;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.net.api.FileDownloadApi;
import com.google.android.apps.miyagi.development.data.net.services.HeaderInterceptor;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RxErrorHandlingCallAdapterFactory;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 09.02.2017.
 */

public class CertificateDownloadHelper {

    @Inject ConfigStorage mConfigStorage;

    public static final String MIME_TYPE_PDF = "application/pdf";
    private static final String LOOPBACK_BASE_API = "http://127.0.0.1/";
    private static final String CERTIFICATE_FILE_NAME = "certificate.pdf";
    private final FileDownloadApi mDownloadApi;

    /**
     * Instantiates a new CertificateDownloadHelper.
     */
    public CertificateDownloadHelper() {
        GoogleApplication.getInstance().getAppComponent().inject(this);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HeaderInterceptor(mConfigStorage));

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(LOOPBACK_BASE_API)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
        mDownloadApi = retrofit.create(FileDownloadApi.class);
    }

    /**
     * Return save directory.
     * data/data/appname/[directoryName]/.
     */
    private static String getMainStorageDirectoryUrl() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator;
    }

    /**
     * Download certificate.
     *
     * @param url the url of certificate file.
     * @return the Observable String.
     */
    public Observable<String> downloadCertificate(String url) {
        return mDownloadApi.downloadFile(url)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(responseBody -> {
                    File storageDir = new File(getMainStorageDirectoryUrl());
                    if (!storageDir.exists()) {
                        storageDir.mkdirs();
                    }
                    return saveCertificationToFile(storageDir, responseBody);
                });
    }

    private Observable<String> saveCertificationToFile(File storageDir, ResponseBody responseBody) {
        return Observable.create(subscriber -> {
            try {
                File file = new File(storageDir, CERTIFICATE_FILE_NAME);
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(responseBody.source());
                sink.close();
                subscriber.onNext(file.getPath());
            } catch (IOException exception) {
                subscriber.onError(exception);
            } finally {
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Delete certificate if exists.
     */
    void deleteCertificate() {
        File rootDir = new File(getMainStorageDirectoryUrl());
        File file = new File(rootDir, CERTIFICATE_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }
}