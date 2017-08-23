package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepTwo;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.LearningType;
import com.google.android.apps.miyagi.development.ui.register.common.OnItemSelectedListener;
import com.google.android.apps.miyagi.development.utils.SpaceItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcinarciszew on 08.02.2017.
 */

public class SelectLearningTypeFragment extends Fragment {

    @BindView(R.id.diagnostics_select_learning_type_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.diagnostics_select_learning_type_header) TextView mLabelHeader;
    @BindView(R.id.diagnostics_select_learning_type_sub_header) TextView mLabelSubHeader;

    @Inject AnalyticsHelper mAnalyticsHelper;

    private SelectLearningTypeAdapter mAdapter;

    private DiagnosticsResponseData mDiagnosticsData;
    private OnItemSelectedListener<LearningType> mOnSelectionChangeListener;
    private Unbinder mUnbinder;
    private int mSelectedPosition = -1;

    public SelectLearningTypeFragment() {
    }

    /**
     * New instance of DiagnosticStepOne fragment.
     *
     * @param mDiagnosticsData - DiagnosticsResponseData data.
     *
     * @return DiagnosticStepOneFragment.
     */
    public static SelectLearningTypeFragment newInstance(DiagnosticsResponseData mDiagnosticsData) {
        SelectLearningTypeFragment fragment = new SelectLearningTypeFragment();
        Bundle args = new Bundle();
        args.putParcelable(BundleKey.DIAGNOSTICS_DATA, Parcels.wrap(mDiagnosticsData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GoogleApplication) getActivity().getApplication()).getAppComponent().inject(this);
        mDiagnosticsData = Parcels.unwrap(getArguments().getParcelable(BundleKey.DIAGNOSTICS_DATA));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mRecyclerView.setAdapter(mAdapter);
            if (mSelectedPosition >= 0) {
                mRecyclerView.smoothScrollToPosition(mSelectedPosition);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.diagnostics_select_learning_type_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_create_plan));

        if (savedInstanceState != null) {
            int savedPosition = savedInstanceState.getInt(BundleKey.SELECTED_POSITION, -1);
            if (savedPosition >= 0) {
                mSelectedPosition = savedPosition;
            }
        }

        initUi();
        initRecycler();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null && mSelectedPosition >= 0) {
            outState.putInt(BundleKey.SELECTED_POSITION, mSelectedPosition);
        }
    }

    void initUi() {
        StepTwo stepTwo = mDiagnosticsData.getStepTwo();
        mLabelHeader.setText(stepTwo.getHeaderText());
        mLabelSubHeader.setText(stepTwo.getSubHeaderText());
    }

    private void initRecycler() {
        List<SelectLearningTypeItem> items = new ArrayList<>();

        StepTwo stepTwo = mDiagnosticsData.getStepTwo();

        SelectLearningTypeItem certificationItem = new SelectLearningTypeItem(LearningType.CERTIFICATION, stepTwo.getCertificationTitle(), stepTwo.getCertificationText());
        SelectLearningTypeItem planItem = new SelectLearningTypeItem(LearningType.PLAN, stepTwo.getPlanTitle(), stepTwo.getPlanText());

        if (mSelectedPosition >= 0) {
            certificationItem.setState(mSelectedPosition == 0);
            planItem.setState(mSelectedPosition == 1);
        }

        items.add(certificationItem);
        items.add(planItem);

        mAdapter = new SelectLearningTypeAdapter(getContext(), items, this::onItemSelected);

        RecyclerView.LayoutManager layoutManager;
        if (ViewUtils.isLandTablet(getContext())) {
            layoutManager = new GridLayoutManager(getContext(), 2);
            mRecyclerView.addItemDecoration(
                    new SpaceItemDecoration(
                            0,
                            (int) getResources().getDimension(R.dimen.padding_22),
                            false,
                            (int) getResources().getDimension(R.dimen.small_margin)));
        } else {
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void onItemSelected(SelectLearningTypeItem selectLearningTypeItem) {
        int position = mAdapter.getItems().indexOf(selectLearningTypeItem);
        mSelectedPosition = position;
        mAnalyticsHelper.trackEvent(getString(R.string.screen_create_plan), getString(R.string.event_category_diagnostics), getString(R.string.event_action_select_option), selectLearningTypeItem.getLearningType().getType());

        if (mOnSelectionChangeListener != null) {
            mOnSelectionChangeListener.onItemSelected(selectLearningTypeItem.getLearningType());
        }

        for (SelectLearningTypeItem item : mAdapter.getItems()) {
            item.setState(false);
        }
        selectLearningTypeItem.setState(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    public void setOnSelectionChangeListener(OnItemSelectedListener<LearningType> listener) {
        mOnSelectionChangeListener = listener;
    }

    interface BundleKey {
        String SELECTED_POSITION = "SELECTED_POSITION";
        String DIAGNOSTICS_DATA = "DIAGNOSTICS_DATA";
    }
}
