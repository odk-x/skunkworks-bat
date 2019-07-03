package org.odk.odknotifications.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.odknotifications.Adapters.NotificationAdapter;
import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.R;

import java.util.ArrayList;


public class NotificationGroupFragment extends Fragment {

    private static final String ARG_GROUP_ID = "id";
    private String groupId;
    private RecyclerView recyclerView;
    ArrayList<Notification> notificationArrayList;
    NotificationAdapter notificationAdapter;
    public NotificationGroupFragment() {
    }


    public static NotificationGroupFragment newInstance(String groupId) {
        NotificationGroupFragment fragment = new NotificationGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
        }
        notificationArrayList = new ArrayList<>();
        if(groupId!=null) {
            DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
            notificationArrayList = dbHandler.getNotifications(groupId);
        }
        notificationAdapter = new NotificationAdapter(notificationArrayList);
        System.out.println("Fragment:"+notificationArrayList.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification_group, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.list_view);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

}
