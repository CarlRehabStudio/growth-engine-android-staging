package com.google.android.apps.miyagi.development.data.net.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by jerzyw on 16.12.2016.
 */

public interface FileDownloadApi {
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
