package org.odk.odknotifications.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.odk.odknotifications.model.Notification;
import org.odk.odknotifications.R;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<Notification> notifications;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView message;
        TextView date;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            message = view.findViewById(R.id.message);
            date = view.findViewById(R.id.date);
        }
    }


    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.date.setText(notification.getStringDate());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}