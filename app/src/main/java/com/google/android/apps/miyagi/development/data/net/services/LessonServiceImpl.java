package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.api.LessonServiceApi;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.MarkLessonAsCompleteResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.MarkLessonAsViewedResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;
import com.google.android.apps.miyagi.development.data.net.services.errorhandling.RefreshTokenTransformer;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;

import rx.Observable;


/**
 * Created by marcinarciszew on 22.11.2016.
 */

public class LessonServiceImpl extends AbsBaseService<LessonServiceApi> implements LessonService {

    public LessonServiceImpl(RetrofitProvider retrofitProvider, ConfigStorage configStorage) {
        super(retrofitProvider, configStorage, LessonServiceApi.class);
    }

    public Observable<LessonResponse> getLessonData(int id) {
        return mApi.getLessonData(id).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<MarkLessonAsViewedResponse> markLessonAsViewed(int id, XsrfToken token) {
        return mApi.markLessonAsViewed(id, token).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }

    @Override
    public Observable<MarkLessonAsCompleteResponse> markLessonAsComplete(int id, XsrfToken token) {
        return mApi.markLessonAsComplete(id, token).compose(new RefreshTokenTransformer().refreshTokenOnError());
    }
}
