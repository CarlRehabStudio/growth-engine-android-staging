package com.google.android.apps.miyagi.development.data.net.services;

import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.MarkLessonAsCompleteResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.MarkLessonAsViewedResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;
import com.google.gson.JsonObject;

import rx.Observable;


/**
 * Created by marcinarciszew on 22.11.2016.
 */

public interface LessonService extends BaseService {

    Observable<LessonResponse> getLessonData(int id);

    Observable<MarkLessonAsViewedResponse> markLessonAsViewed(int id, XsrfToken token);

    Observable<MarkLessonAsCompleteResponse> markLessonAsComplete(int id, XsrfToken token);
}
