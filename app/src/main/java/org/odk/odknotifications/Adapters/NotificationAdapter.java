package org.odk.odknotifications.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView title, message, date, response;
        public LinearLayout responseLayout;
        public ImageView imageView;
        public String image_uri = null;
        public String id = null;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            message = (TextView) view.findViewById(R.id.message);
            date = (TextView) view.findViewById(R.id.date);
            response = view.findViewById(R.id.response);
            responseLayout = view.findViewById(R.id.responseLayout);
            imageView = (ImageView) view.findViewById(R.id.notificationImageView);
            view.setOnLongClickListener(this);
        }

        void bind(Notification notification) {
            title.setText(notification.getTitle());
            message.setText(notification.getMessage());
            date.setText(notification.getStringDate());
            imageView.setImageBitmap(BitmapFactory.decodeFile(notification.getImg_uri()));
            image_uri = notification.getImg_uri();
            id=notification.getId();
            if (notification.getType().compareTo(Notification.INTERACTIVE) == 0) {
                response.setText(notification.getResponse());
                responseLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            final PopupMenu popup = new PopupMenu(context, view);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu);
            if(image_uri==null)
            {
                popup.getMenu().findItem(R.id.menu1).setEnabled(false); // if image is not there in notification disable the save to gallery button
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu1:
                            addImageToGallery(image_uri,context);
                            break;
                        case R.id.menu2:
                            DBHandler dbHandler = dbHandler = new DBHandler(context,null,null,1);
                            dbHandler.removeNotification(id);
                            notifyItemRemoved(getAdapterPosition());
                            break;
                    }
                    return false;
                }
            });
            //displaying the popup
            popup.show();
            return true;
        }
        public void addImageToGallery(final String filePath, final Context context) {

            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, filePath);

            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
