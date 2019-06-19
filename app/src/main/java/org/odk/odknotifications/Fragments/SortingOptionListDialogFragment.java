package org.odk.odknotifications.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.odk.odknotifications.Listeners.SortingOptionListener;
import org.odk.odknotifications.R;

public class SortingOptionListDialogFragment extends BottomSheetDialogFragment {
    private static final String ARG_SORTED_ORDER = "sorted_order";
    private SortingOptionListener listener;
    private String sortedOrder;
    private RadioGroup sortingOptions;

    public static SortingOptionListDialogFragment newInstance(String sortedOrder) {
        SortingOptionListDialogFragment fragment = new SortingOptionListDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SORTED_ORDER, sortedOrder);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (SortingOptionListener) context;
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        final View contentView = View.inflate(getContext(), R.layout.fragment_sorting_option_list_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        if(getArguments()!=null){
            sortedOrder = getArguments().getString(ARG_SORTED_ORDER);
        }
        sortingOptions = contentView.findViewById(R.id.radioGroup);
        sortingOptions.check(contentView.findViewWithTag(sortedOrder).getId());
        sortingOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String optionSelected = ((RadioButton)contentView.findViewById(group.getCheckedRadioButtonId())).getText().toString();
                listener.sort(optionSelected);
                dismiss();
            }
        });
    }

}
