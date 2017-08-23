package com.google.android.apps.miyagi.development.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

/**
 * Created by lukaszweglinski on 25.11.2016.
 */

public class UriHelper {
    /**
     * Return URI of resource drawable.
     *
     * @param context context.
     * @param resId   drawable resources id.
     */
    public static Uri resourceToUri(Context context, int resId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://"
                + context.getResources().getResourcePackageName(resId) + '/'
                + context.getResources().getResourceTypeName(resId)
                + '/'
                + context.getResources().getResourceEntryName(resId));
    }
}
