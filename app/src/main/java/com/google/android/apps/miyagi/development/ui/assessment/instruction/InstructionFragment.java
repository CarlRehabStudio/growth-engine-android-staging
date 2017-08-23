package com.google.android.apps.miyagi.development.ui.assessment.instruction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.assessment.CopyInstructions;
import com.google.android.apps.miyagi.development.helpers.ImageUrlHelper;
import com.google.android.apps.miyagi.development.ui.assessment.common.AssessmentAbstractFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import org.parceler.Parcels;

/**
 * Created by marcinarciszew on 14.12.2016.
 */

public class InstructionFragment extends AssessmentAbstractFragment {

    @BindView(R.id.assessment_instruction_image) ImageView mImage;
    @BindView(R.id.assessment_instruction_label_header) TextView mLabelHeader;
    @BindView(R.id.assessment_instruction_label_description) TextView mLabelDescription;
    @BindView(R.id.assessment_instruction_label_attempts_number_info) TextView mLabelAttemptsNumberInfo;
    @Nullable @BindView(R.id.assessment_instruction_bottom_space) View mAssessmentInstructionBottomSpace;

    private CopyInstructions mCopyInstructions;

    private Unbinder mUnbinder;

    public InstructionFragment() {
    }

    /**
     * Creates new Assessment Instruction fragment with instruction data.
     */
    public static InstructionFragment newInstance(CopyInstructions copy) {
        Bundle args = new Bundle();
        args.putParcelable(ArgsKey.COPY_INSTRUCTIONS, Parcels.wrap(copy));

        InstructionFragment fragment = new InstructionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCopyInstructions = Parcels.unwrap(args.getParcelable(ArgsKey.COPY_INSTRUCTIONS));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assessment_instruction_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
    }

    private void bindData() {
        if (TextUtils.isEmpty(mCopyInstructions.getTopicTitle())) {
            mLabelHeader.setVisibility(View.GONE);
        } else {
            mLabelHeader.setText(mCopyInstructions.getTopicTitle());
        }
        mLabelDescription.setText(mCopyInstructions.getInstructionsText());

        if (!TextUtils.isEmpty(mCopyInstructions.getInstructionsTitle())) {
            mLabelAttemptsNumberInfo.setVisibility(View.VISIBLE);
            //because of api, error attempts info is on field : instruction_title
            mLabelAttemptsNumberInfo.setText(mCopyInstructions.getInstructionsTitle());
            if (mAssessmentInstructionBottomSpace!= null) {
                mAssessmentInstructionBottomSpace.setVisibility(View.GONE);
            }
        } else {
            mLabelAttemptsNumberInfo.setText(null);
            mLabelAttemptsNumberInfo.setVisibility(View.GONE);
            if (mAssessmentInstructionBottomSpace!= null) {
                mAssessmentInstructionBottomSpace.setVisibility(View.VISIBLE);
            }
        }

        mImage.setBackgroundColor(mCopyInstructions.getImageBackgroundColor());
        Glide.with(this)
                .load(ImageUrlHelper.getUrlFor(getContext(), mCopyInstructions.getImageUrl()))
                .into(mImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
    }

    interface ArgsKey {
        String COPY_INSTRUCTIONS = CopyInstructions.class.getCanonicalName();
    }
}
