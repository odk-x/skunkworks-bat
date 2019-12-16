package org.odk.odknotifications.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.odk.odknotifications.Model.DateCompare;
import org.odk.odknotifications.Model.MessageCompare;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.Model.TitleCompare;
import org.odk.odknotifications.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> implements Filterable {

    private List<Notification> notifications;
    private List<Notification> notificationsFiltered;
    private Context context;

    public void sort(String field) {
        if(field.compareTo(context.getString(R.string.date_old_to_new))==0){
            Collections.sort(notificationsFiltered,new DateCompare());
        }
        else if (field.compareTo(context.getString(R.string.date_new_to_old))==0) {
            Collections.sort(notificationsFiltered, new DateCompare());
            Collections.reverse(notificationsFiltered);
        }
        else if (field.compareTo(context.getString(R.string.title))==0){
            Collections.sort(notificationsFiltered,new TitleCompare());
        }
        else if(field.compareTo(context.getString(R.string.message))==0){
            Collections.sort(notificationsFiltered,new MessageCompare());
        }
        notifyDataSetChanged();
    }

    public void filterByGroup(String group){
        if(group.compareTo("None")==0){
            notificationsFiltered = notifications;
            notifyDataSetChanged();
            return;
        }
        ArrayList<Notification> filteredList = new ArrayList<>();
        for(Notification notification : notifications){
            if(notification.getGroup().compareTo(group)==0){
                filteredList.add(notification);
            }
        }
        notificationsFiltered = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message, date,response;
        public LinearLayout responseLayout;
        public ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            message = (TextView) view.findViewById(R.id.message);
            date = (TextView) view.findViewById(R.id.date);
            response = view.findViewById(R.id.response);
            responseLayout = view.findViewById(R.id.responseLayout);
            imageView=(ImageView)view.findViewById(R.id.notificationImageView);
        }
        void bind(Notification notification){
            title.setText(notification.getTitle());
            message.setText(notification.getMessage());
            date.setText(notification.getStringDate());
            imageView.setImageBitmap(BitmapFactory.decodeFile(notification.getImg_uri()));

            if(notification.getType().compareTo(Notification.INTERACTIVE)==0){
                response.setText(notification.getResponse());
                responseLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
        notificationsFiltered = notifications;
    }

    public NotificationAdapter(List<Notification> notifications,Context context) {
        this.notifications = notifications;
        notificationsFiltered = notifications;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Notification notification = notificationsFiltered.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    notificationsFiltered = notifications;
                } else {
                    List<Notification> filteredList = new ArrayList<>();
                    for (Notification row : notifications) {
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getMessage().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    notificationsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = notificationsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notificationsFiltered = (ArrayList<Notification>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
