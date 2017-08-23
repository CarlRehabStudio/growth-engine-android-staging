package com.google.android.apps.miyagi.development.ui.onboarding;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.google.android.apps.miyagi.development.data.models.onboarding.OnboardingStep;

import java.util.List;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */
class OnboardingAdapter extends FragmentStatePagerAdapter {

    private final List<OnboardingStep> mSteps;
    private SparseArray<OnboardingBaseFragment> mPageReferenceMap = new SparseArray<>();

    /**
     * Constructs adapter for onboarding viewpager.
     */
    OnboardingAdapter(FragmentManager fm, List<OnboardingStep> steps) {
        super(fm);
        mSteps = steps;
    }

    @Override
    public OnboardingBaseFragment getItem(int position) {
        return OnboardingBaseFragment.newInstance(mSteps.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        OnboardingBaseFragment fragment = (OnboardingBaseFragment) super.instantiateItem(container, position);
        mPageReferenceMap.put(position, fragment);
        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        mPageReferenceMap.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mSteps.size();
    }

    /**
     * Gets last item position.
     *
     * @return the last item position.
     */
    int getLastItemPosition() {
        return getCount() - 1;
    }

    /**
     * Gets fragment reference.
     *
     * @param position the position of fragment
     * @return OnboardingBaseFragment instance.
     */
    OnboardingBaseFragment getFragment(int position) {
        return mPageReferenceMap.get(position);
    }

    /**
     * Hide invisible item content.
     *
     * @param position the fragment position.
     */
    void hideInvisibleItemContent(int position) {
        for (int i = 0; i < mPageReferenceMap.size(); i++) {
            if (position != i) {
                mPageReferenceMap.get(i).hideContent();
            }
        }
    }

    /**
     * Animate fragment.
     *
     * @param position the fragment position.
     */
    void animateFragment(int position) {
        OnboardingBaseFragment fragment = getFragment(position);
        if (fragment != null) {
            fragment.animateScreen();
        }
    }
}
