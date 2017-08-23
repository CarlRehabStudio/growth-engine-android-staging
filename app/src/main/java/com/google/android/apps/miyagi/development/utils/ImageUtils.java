package com.google.android.apps.miyagi.development.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import rx.Observable;

/**
 * Created by marcin on 27.01.2017.
 */

public class ImageUtils {

    /**
     * Glide preloader observable.
     *
     * @param context the context.
     * @param image   image url.
     * @return Glide preloader observable.
     */
    public static Observable<Boolean> glideObservable(Context context, String image, ImageView imageView) {
        return Observable.create(subscriber -> Glide.with(context)
                .load(image)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        subscriber.onNext(false);
                        subscriber.onError(e);
                        subscriber.onCompleted();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                        return false;
                    }
                }).into(imageView)
        );
    }
}