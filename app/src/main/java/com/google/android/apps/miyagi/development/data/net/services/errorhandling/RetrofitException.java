package com.google.android.apps.miyagi.development.data.net.services.errorhandling;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by lukaszweglinski on 02.01.2017.
 */

public class RetrofitException extends RuntimeException {
    private final String mUrl;
    private final Response mResponse;
    private final Kind mKind;
    private final Retrofit mRetrofit;

    public RetrofitException(String message, String url, Response response, Kind kind, Throwable exception, Retrofit retrofit) {
        super(message, exception);
        this.mUrl = url;
        this.mResponse = response;
        this.mKind = kind;
        this.mRetrofit = retrofit;
    }

    /**
     * Http error retrofit exception.
     *
     */
    public static RetrofitException httpError(String url, Response response, Retrofit retrofit) {
        String message = response.code() + " " + response.message();
        return new RetrofitException(message, url, response, Kind.HTTP, null, retrofit);
    }

    /**
     * Network error retrofit exception.
     *
     */
    public static RetrofitException networkError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.NETWORK, exception, null);
    }

    /**
     * Unexpected error retrofit exception.
     *
     */
    public static RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null);
    }

    /**
     * Unauthorized error retrofit exception.
     *
     */
    public static RetrofitException unauthorizedError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.UNAUTHORIZED, exception, null);
    }

    /**
     * Invalid token error retrofit exception.
     *
     */
    public static RetrofitException invalidTokenError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.INVALID_TOKEN, exception, null);
    }

    /**
     * Deprecated api error retrofit exception.
     *
     */
    public static RetrofitException deprecatedApiError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.DEPRECATED_API, exception, null);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return mResponse;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return mKind;
    }

    /**
     * The Retrofit this request was executed on.
     */
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (mResponse == null || mResponse.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = mRetrofit.responseBodyConverter(type, new Annotation[0]);
        return converter.convert(mResponse.errorBody());
    }


    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED,
        /**
         * An unauthorized error occurred while attempting to execute a request. Logout user and clean data.
         */
        UNAUTHORIZED,
        /**
         * An invalid token error occurred while attempting to execute a request. Try refresh token and retry request.
         */
        INVALID_TOKEN,
        /**
         * The API is deprecated.
         */
        DEPRECATED_API
    }
}