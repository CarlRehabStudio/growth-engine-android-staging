package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.notifications.NotificationsRequestData;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public interface NotificationsServiceApi {

    @POST("api/v1/student/fb-token/add")
    Observable<ResponseStatus> addNotificationsToken(@Body NotificationsRequestData notificationsRequestData);

    @POST("api/v1/student/fb-token/remove")
    Observable<ResponseStatus> removeNotificationsToken(@Body NotificationsRequestData notificationsRequestData);

}
