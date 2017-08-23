package com.google.android.apps.miyagi.development.ui.practice.twitter;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter.TwitterPracticeOption;
import com.google.android.apps.miyagi.development.ui.components.widget.AutoResizeTextView;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by marcinarciszew on 18.11.2016.
 */

public class TwitterAdapter extends PagerAdapter {

    private static final int TRANSPARENT_COLOR = Color.TRANSPARENT;

    private List<TwitterPracticeOption> mData;

    /**
     * Constructs page adapter for Twitter exercise.
     *
     * @param answers - list of answers to display.
     */
    public TwitterAdapter(List<TwitterPracticeOption> answers) {
        mData = new ArrayList<>();

        mData.add(new TwitterPracticeOption()); // first element is empty
        mData.addAll(answers);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.practice_twitter_card, container, false);

        CardView cardView = (CardView) view.findViewById(R.id.twitter_card_view);
        if (position > 0) {
            AutoResizeTextView cardText = (AutoResizeTextView) view.findViewById(R.id.twitter_card_text);
            cardText.setText(HtmlHelper.fromHtml(mData.get(position).getText()));
        }

        if (position == 0) { // extra first element for displaying instruction
            cardView.setBackgroundColor(TRANSPARENT_COLOR);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public String getOptionId(int position) {
        return mData.get(position).getId();
    }
}
