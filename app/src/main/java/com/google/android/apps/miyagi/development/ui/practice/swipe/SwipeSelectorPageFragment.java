package com.google.android.apps.miyagi.development.ui.practice.swipe;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe.SwipePracticeOption;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import org.parceler.Parcels;

public class SwipeSelectorPageFragment extends Fragment {

    interface ArgsKey {
        String ARG_OPTION = "PAGE_OPTION";
        String ARG_COLOR = "PAGE_ACCENT_COLOR";
        String ARG_POSITION = "PAGE_POSITION";
    }

    /**
     * Creates new instance of SwipeSelectorPageFragment.
     */
    public static SwipeSelectorPageFragment create(int position, SwipePracticeOption option, int accentColor) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ArgsKey.ARG_OPTION, Parcels.wrap(option));
        bundle.putInt(ArgsKey.ARG_COLOR, accentColor);
        bundle.putInt(ArgsKey.ARG_POSITION, position);

        SwipeSelectorPageFragment fragment = new SwipeSelectorPageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.practice_swipe_selector_page_fragment, container, false);

        final Bundle args = getArguments();
        SwipePracticeOption option = Parcels.unwrap(args.getParcelable(ArgsKey.ARG_OPTION));
        int accentColor = args.getInt(ArgsKey.ARG_COLOR);
        int position = args.getInt(ArgsKey.ARG_POSITION);

        ImageView pageIv = (ImageView) view.findViewById(R.id.page_image);
        Glide.with(this)
                .load(ImageUrlHelper.getUrlFor(getContext(), option.getImages()))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .dontAnimate()
                .into(pageIv);

        ImageView pageIndicatorIv = (ImageView) view.findViewById(R.id.page_indicator_image);
        GradientDrawable oval = (GradientDrawable) pageIndicatorIv.getDrawable();
        oval.setColor(accentColor);

        TextView pageIndicatorTv = (TextView) view.findViewById(R.id.page_indicator_text);
        pageIndicatorTv.setText(String.format("%d", position + 1));

        return view;
    }
}
