package com.google.android.apps.miyagi.development.ui.diagnostics.pages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepThreeGoals;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepThreeOptions;
import com.google.android.apps.miyagi.development.data.net.responses.diagnostics.DiagnosticsResponseData;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.diagnostics.common.LearningPlanType;
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

public class SelectGoalsFragment extends Fragment {

    @BindView(R.id.diagnostics_step_three_header) TextView mLabelHeader;
    @BindView(R.id.diagnostics_step_three_sub_header) TextView mLabelSubHeader;
    @BindView(R.id.diagnostics_step_three_recycler) RecyclerView mDiagnosticsRecycler;

    @Inject AnalyticsHelper mAnalyticsHelper;

    private Unbinder mUnbinder;
    private SelectGoalsAdapter mAdapter;
    private DiagnosticsResponseData mDiagnosticsData;
    private OnItemSelectedListener<List<Integer>> mOnSelectionChangeListener;
    private String mPersona;
    private StepThreeGoals mGoals;
    private List<Integer> mSelectedPositions;

    public SelectGoalsFragment() {
    }

    /**
     * New instance of DiagnosticsThree fragment.
     */
    public static SelectGoalsFragment newInstance(DiagnosticsResponseData data) {
        Bundle args = new Bundle();
        args.putParcelable(BundleKey.DIAGNOSTICS_DATA, Parcels.wrap(data));

        SelectGoalsFragment fragment = new SelectGoalsFragment();
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
            mDiagnosticsRecycler.setAdapter(mAdapter);
            mDiagnosticsRecycler.scrollToPosition(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diagnostics_select_goals_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mAnalyticsHelper.trackScreen(getString(R.string.screen_create_plan_goals));

        if (savedInstanceState != null) {
            mSelectedPositions = Parcels.unwrap(savedInstanceState.getParcelable(BundleKey.SELECTED_POSITION));
            mPersona = savedInstanceState.getString(BundleKey.PERSONA);
            setPersona(mPersona);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            List<Integer> selectedPositions = getSelectedPositions();
            outState.putParcelable(BundleKey.SELECTED_POSITION, Parcels.wrap(selectedPositions));
        }
        if (mPersona != null) {
            outState.putString(BundleKey.PERSONA, mPersona);
        }
    }

    @NonNull
    private List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            SelectGoalsItem item = mAdapter.getItem(i);
            if (item.isSelected()) {
                selectedPositions.add(i);
            }
        }
        return selectedPositions;
    }

    void initUi() {
        mLabelHeader.setText(mGoals.getHeaderText());
        mLabelSubHeader.setText(mGoals.getSubHeaderText());
    }

    void initRecycler() {
        List<SelectGoalsItem> items = new ArrayList<>();
        for (int i = 0; i < mGoals.getOptions().size(); i++) {
            StepThreeOptions options = mGoals.getOptions().get(i);
            SelectGoalsItem item = new SelectGoalsItem(options);
            if (mSelectedPositions != null && mSelectedPositions.contains(i)) {
                item.setSelected(true);
            }
            items.add(item);
        }
        mAdapter = new SelectGoalsAdapter(getContext(), items, this::onItemSelected);

        RecyclerView.LayoutManager layoutManager;
        if (ViewUtils.isLandTablet(getContext())) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        }

        if (ViewUtils.isTablet(getContext())) {
            mDiagnosticsRecycler.addItemDecoration(
                    new SpaceItemDecoration(
                            0,
                            (int) getResources().getDimension(R.dimen.padding_22),
                            false,
                            (int) getResources().getDimension(R.dimen.small_margin)));
        }

        mDiagnosticsRecycler.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = mDiagnosticsRecycler.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void onItemSelected(SelectGoalsItem selectGoalsItem) {
        mAnalyticsHelper.trackEvent(getString(R.string.screen_create_plan_goals), getString(R.string.event_category_diagnostics), getString(R.string.event_action_select_option), selectGoalsItem.getStepThreeOptions().getId());
        selectGoalsItem.setSelected(!selectGoalsItem.isSelected());
        mAdapter.notifyItemChanged(mAdapter.getItems().indexOf(selectGoalsItem));
        mOnSelectionChangeListener.onItemSelected(getSelectedPositions());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    public void setOnSelectionChangeListener(OnItemSelectedListener<List<Integer>> onSelectionChangeListener) {
        this.mOnSelectionChangeListener = onSelectionChangeListener;
    }

    /**
     * Setting persona selected by user.
     */
    public void setPersona(String persona) {
        mPersona = persona;
        if (mPersona != null && mDiagnosticsData != null) {
            if (mPersona.equals(LearningPlanType.NO_BUSSINES.getType())) {
                mGoals = mDiagnosticsData.getStepThree().getNoBusinessGoals();
            } else if (mPersona.equals(LearningPlanType.PRE_BUSSINES.getType())) {
                mGoals = mDiagnosticsData.getStepThree().getPreBusinessGoals();
            } else if (mPersona.equals(LearningPlanType.IN_BUSSINES.getType())) {
                mGoals = mDiagnosticsData.getStepThree().getInBusinessGoals();
            }
            initUi();
            initRecycler();
        }
    }

    interface BundleKey {
        String SELECTED_POSITION = "SELECTED_POSITION";
        String PERSONA = "PERSONA";
        String DIAGNOSTICS_DATA = "DIAGNOSTICS_DATA";
    }
}
