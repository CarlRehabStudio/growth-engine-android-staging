package com.google.android.apps.miyagi.development.ui.practice.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.utils.HtmlHelper;

import org.parceler.Parcels;

public abstract class AbstractPracticeFragment<T> extends Fragment implements PracticeNavCallback.Callback {

    protected PracticeNavCallback mNavCallback;

    protected Practice mPracticeData;
    protected boolean mIsSuccessful;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsSuccessful = getArguments().getBoolean(BundleKey.SUCCESS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initPractice();
        if (!ViewUtils.isTablet(getContext())) {
            view.setBackgroundColor(getBackgroundColor());
            showInfoButton();
        }
        return view;
    }

    protected void showInfoButton() {
        mNavCallback.showInfoButton();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setNavigationCallback(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(BundleKey.CURRENT_STATE)) {
            T currentStates = getStatesFromSavedInstance(savedInstanceState);
            recreateAnswers(currentStates);
        } else if (mIsSuccessful) {
            recreateAnswers();
        }
    }

    @Nullable
    protected T getStatesFromSavedInstance(@Nullable Bundle savedInstanceState) {
        Parcelable parcelableStates = savedInstanceState.getParcelable(BundleKey.CURRENT_STATE);
        return Parcels.unwrap(parcelableStates);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PracticeNavCallback) {
            mNavCallback = (PracticeNavCallback) context;
            setNavigationCallback(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PracticeNavCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setNavigationCallback(PracticeNavCallback.Callback.EMPTY);
        mNavCallback = PracticeNavCallback.NULL;
    }

    public abstract int getLayoutRes();

    protected void setNavigationCallback(PracticeNavCallback.Callback callback) {
        mNavCallback.setNavigationCallback(callback);
    }

    protected void initHeader(TextView header, String question) {
        if (ViewUtils.isTablet(getContext())) {
            header.setText(HtmlHelper.fromHtml(mPracticeData.getInstructionDialogText()));
        } else {
            header.setText(HtmlHelper.fromHtml(question));
        }
    }

    protected void initPractice() {
        if (ViewUtils.isTablet(getContext())) {
            mNavCallback.setBackgroundColor(getBackgroundColor());
        }
        mNavCallback.setNextButtonText(mPracticeData.getInstructionSubmitButtonText());
        enableNavBtnPrevIfNotSuccessfulAndNotTablet();
        enableNavBtnSubmitIfSuccessful();
    }

    protected abstract int getBackgroundColor();

    protected void enableNavBtnPrevIfNotSuccessfulAndNotTablet() {
        if (!mIsSuccessful && !ViewUtils.isTablet(getContext())) {
            enableNavBtnPrev();
        } else {
            disableNavBtnPrev();
        }
    }

    private void enableNavBtnSubmitIfSuccessful() {
        if (mIsSuccessful) {
            mNavCallback.setNextEnable(true);
        } else {
            mNavCallback.setNextEnable(false);
        }
    }

    protected void disableNavBtnSubmit() {
        mNavCallback.setNextEnable(false);
    }

    protected void enableNavBtnSubmit() {
        mNavCallback.setNextEnable(true);
    }

    protected void enableNavBtnPrev() {
        mNavCallback.setPrevButtonVisibility(View.VISIBLE);
    }

    protected void disableNavBtnPrev() {
        mNavCallback.setPrevButtonVisibility(View.INVISIBLE);
    }

    protected void recreateAnswers() {
    }

    protected void recreateAnswers(T currentStates) {

    }

    public interface BundleKey {
        String SUCCESS = "SUCCESS";
        String TOPIC_COMPLETED = "TOPIC_COMPLETED";
        String CURRENT_STATE = "CURRENT_STATE";
    }
}
