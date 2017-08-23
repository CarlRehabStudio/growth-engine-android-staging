package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.NotificationsServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.notifications.NotificationsRequestData;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public class NotificationsServiceImpl extends AbsBaseService<NotificationsServiceApi> implements NotificationsService {

    public NotificationsServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, NotificationsServiceApi.class);
    }

    public Observable<ResponseStatus> addNotificationsToken(NotificationsRequestData notificationsRequestData) {
        return mApi.addNotificationsToken(notificationsRequestData).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    public Observable<ResponseStatus> removeNotificationsToken(NotificationsRequestData notificationsRequestData) {
        return mApi.removeNotificationsToken(notificationsRequestData).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
