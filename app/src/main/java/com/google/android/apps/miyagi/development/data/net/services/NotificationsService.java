package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.notifications.NotificationsRequestData;

import rx.Observable;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public interface NotificationsService extends BaseService {

    Observable<ResponseStatus> addNotificationsToken(NotificationsRequestData notificationsRequestData);

    Observable<ResponseStatus> removeNotificationsToken(NotificationsRequestData notificationsRequestData);
}
