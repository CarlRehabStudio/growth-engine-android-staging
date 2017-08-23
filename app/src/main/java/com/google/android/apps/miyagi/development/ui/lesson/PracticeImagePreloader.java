package com.google.android.apps.miyagi.development.ui.lesson;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge.SelectLargePracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge.SelectLargePracticeOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright.SelectRightPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright.SelectRightPracticeOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeOption;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter.TwitterPracticeDetails;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter.TwitterPracticeInfo;
import com.google.android.apps.miyagi.development.data.net.responses.lesson.LessonResponse;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.BaseActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcinarciszew on 02.02.2017.
 */

public class PracticeImagePreloader {

    /**
     * Calls to preload practice's images.
     */
    public Observable<Boolean> call(LessonResponse response, BaseActivity activity) {
        Practice practice = response.getResponseData().getPractice();
        List<String> images = findImagesToLoad(practice, activity);

        if (images.size() == 0) {
            // nothing to preload
            return Observable.just(true);
        }

        return Observable.from(images)
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String image) {
                        return glideObservable(activity, image);
                    }
                }).buffer(images.size())
                .map(objects -> true);
    }

    private Observable<Boolean> glideObservable(BaseActivity activity, String image) {
        return Observable.create(subscriber -> Glide.with(activity)
                .load(image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        subscriber.onError(e);
                        subscriber.onCompleted();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                        return false;
                    }
                })
                .preload());
    }

    private List<String> findImagesToLoad(Practice practice, BaseActivity activity) {
        List<String> images = new ArrayList<>();

        String practiceType = practice.getActivityType();
        if (practiceType.equals(Practice.Type.SWIPE_SELECTOR)) {
            images.addAll(findImagesForSwipeSelector(practice, activity));
        } else if (practiceType.equals(Practice.Type.SELECT_LARGE)) {
            images.addAll(findImagesForSelectLarge(practice, activity));
        } else if (practiceType.equals(Practice.Type.SELECT_RIGHT)) {
            images.addAll(findImagesForSelectRight(practice, activity));
        } else if (practiceType.equals(Practice.Type.TWITTER_DRAGANDDROP)) {
            images.add(findImagesForTwitter(practice, activity));
        }

        return images;
    }

    private String findImagesForTwitter(Practice practice, BaseActivity activity) {
        TwitterPracticeDetails details = practice.getTwitterPracticeDetails();
        TwitterPracticeInfo info = details.getInfo();
        String image = ImageUrlHelper.getUrlFor(activity, info.getImages());
        return image;
    }

    private List<String> findImagesForSelectRight(Practice practice, BaseActivity activity) {
        List<String> images = new ArrayList<>();
        SelectRightPracticeDetails details = practice.getSelectRightPracticeDetails();
        List<SelectRightPracticeOption> options = details.getOptions();
        for (SelectRightPracticeOption opt : options) {
            String image = ImageUrlHelper.getUrlFor(activity, opt.getImages());
            images.add(image);
        }

        return images;
    }

    private List<String> findImagesForSelectLarge(Practice practice, BaseActivity activity) {
        List<String> images = new ArrayList<>();
        SelectLargePracticeDetails details = practice.getSelectLargePracticeDetails();
        List<SelectLargePracticeOption> options = details.getOptions();
        for (SelectLargePracticeOption opt : options) {
            String image = ImageUrlHelper.getUrlFor(activity, opt.getImages());
            images.add(image);
        }

        return images;
    }

    private List<String> findImagesForSwipeSelector(Practice practice, BaseActivity activity) {
        List<String> images = new ArrayList<>();
        SwipePracticeDetails details = practice.getSwipePracticeDetails();
        List<SwipePracticeOption> options = details.getOptions();
        for (SwipePracticeOption opt : options) {
            String image = ImageUrlHelper.getUrlFor(activity, opt.getImages());
            images.add(image);
        }

        return images;
    }
}
