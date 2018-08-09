package android.notifications.odk.org.odknotifications.Fragments;

import android.notifications.odk.org.odknotifications.Adapters.NotificationAdapter;
import android.notifications.odk.org.odknotifications.DatabaseCommunicator.DBHandler;
import android.notifications.odk.org.odknotifications.Model.Notification;
import android.notifications.odk.org.odknotifications.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class NotificationGroupFragment extends Fragment {

    public static final String ARG_GROUP_NAME = "name";
    private String groupName;
    private RecyclerView recyclerView;
    ArrayList<Notification> notificationArrayList;
    NotificationAdapter notificationAdapter;
    public NotificationGroupFragment() {
    }


    public static NotificationGroupFragment newInstance(String groupName) {
        NotificationGroupFragment fragment = new NotificationGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_NAME, groupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupName = getArguments().getString(ARG_GROUP_NAME);
        }
        notificationArrayList = new ArrayList<>();
        if(groupName!=null) {
            DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
            notificationArrayList = dbHandler.getNotifications(groupName);
        }else{
            System.out.println("NULL");
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
