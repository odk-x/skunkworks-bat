package org.odk.odknotifications.Fragments;

import android.app.Dialog;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.odk.odknotifications.Listeners.FilterNotificationListener;
import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.R;

import java.util.ArrayList;

import static android.R.layout.simple_spinner_item;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterNotificationDialogFragment extends BottomSheetDialogFragment {

    private ArrayList<Group> groupArrayList;
    private Spinner spinner;
    private String filteredGrp;
    private FilterNotificationListener listener;
    private Context context;

    public FilterNotificationDialogFragment(ArrayList<Group> groupArrayList, String filteredGrp, FilterNotificationListener listener, Context context){
        this.groupArrayList = groupArrayList;
        this.filteredGrp = filteredGrp;
        this.listener = listener;
        this.context = context;
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
        View contentView = View.inflate(getContext(), R.layout.fragment_filter_notification_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        ArrayList<String> groupNameList = new ArrayList<>();
        groupNameList.add("None");
        for(Group group: groupArrayList){
            groupNameList.add(group.getName());
        }
        spinner = contentView.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, simple_spinner_item, groupNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(groupNameList.indexOf(filteredGrp));

        contentView.findViewById(R.id.btn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.filterByGroup(spinner.getSelectedItem().toString());
                dismiss();
            }
        });
    }
}
