package com.google.android.apps.miyagi.development.helpers;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.google.android.apps.miyagi.development.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paweł on 2017-02-17.
 */

public class ScreenAnimationHelper {

    private final Context mContext;
    private ArrayList<View> mFlatViews;
    private Map<String, ImageView> mAsyncImageViews;
    private ArrayList<View> mCtaViews;

    /**
     * Constructs ScreenAnimationHelper.
     */
    public ScreenAnimationHelper(Context context) {
        mContext = context;
        initContainers();
    }

    private void initContainers() {
        mFlatViews = new ArrayList<>();
        mCtaViews = new ArrayList<>();
        mAsyncImageViews = new HashMap<>();
    }

    public void addFlatView(View view) {
        mFlatViews.add(view);
    }

    public void resetAnimations() {
        initContainers();
    }

    public void addCtaView(View view) {
        mCtaViews.add(view);
    }

    public void addAsyncImageView(String url, ImageView view) {
        mAsyncImageViews.put(url, view);
    }

    /**
     * Starts animation of the screen.
     */
    public void animateScreen() {
        animateFlatViews();
        animateFlatAsyncViews();
        animateCtaViews();
    }

    private void animateFlatViews() {
        int animationOffset = mContext.getResources().getInteger(R.integer.animation_cta_start_offset);
        for (View view : mFlatViews) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            animation.setInterpolator(new AccelerateInterpolator(2f));
            animation.setStartOffset(animationOffset);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }
    }

    private void animateFlatAsyncViews() {
        for (Map.Entry<String, ImageView> entry : mAsyncImageViews.entrySet()) {
            String url = entry.getKey();
            final ImageView view = entry.getValue();
            Glide.with(view.getContext())
                    .load(url)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Animation imageAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                            imageAnimation.setInterpolator(new AccelerateInterpolator(2f));
                            int animationOffset = mContext.getResources().getInteger(R.integer.animation_cta_start_offset);
                            imageAnimation.setStartOffset(animationOffset);
                            imageAnimation.setFillBefore(true);
                            imageAnimation.setFillAfter(true);
                            imageAnimation.setFillEnabled(true);
                            view.setImageDrawable(resource);
                            view.startAnimation(imageAnimation);
                            return true;
                        }
                    })
                    .into(view);
        }
    }

    private void animateCtaViews() {
        int viewOffset = mContext.getResources().getInteger(R.integer.animation_cta_offset);
        int animationOffset = mContext.getResources().getInteger(R.integer.animation_cta_start_offset);
        for (View view : mCtaViews) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_and_enter_from_bottom);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setStartOffset(animationOffset);
            view.startAnimation(animation);
            animationOffset += viewOffset;
        }
    }
}