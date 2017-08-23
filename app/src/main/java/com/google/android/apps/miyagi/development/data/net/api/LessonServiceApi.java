package com.google.android.apps.miyagi.development.data.net.api;

import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.MarkLessonAsCompleteResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.MarkLessonAsViewedResponse;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.XsrfToken;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

public interface LessonServiceApi {

    @GET("api/v1/pages/lesson/{id}")
    Observable<LessonResponse> getLessonData(@Path("id") int id);

    @POST("api/v1/lesson/{id}/viewed")
    Observable<MarkLessonAsViewedResponse> markLessonAsViewed(@Path("id") int id, @Body XsrfToken token);

    @POST("api/v1/lesson/{id}/complete")
    Observable<MarkLessonAsCompleteResponse> markLessonAsComplete(@Path("id") int id, @Body XsrfToken token);
}
