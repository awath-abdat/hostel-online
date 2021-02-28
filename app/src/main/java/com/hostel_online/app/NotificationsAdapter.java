package com.hostel_online.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>
{
  ArrayList<Map<String, Object>> notifications;
  public NotificationsAdapter(ArrayList<Map<String, Object>> notifications)
  {
    this.notifications = notifications;
  }

  public static class NotificationViewHolder extends RecyclerView.ViewHolder
  {
    TextView tvTitle;
    TextView tvMessage;
    TextView tvDate;
    public NotificationViewHolder(View skeletonView)
    {
      super(skeletonView);
      tvTitle = (TextView) skeletonView.findViewById(R.id.notification_title);
      tvMessage = (TextView) skeletonView.findViewById(R.id.notification_message);
      tvDate = (TextView) skeletonView.findViewById(R.id.notification_date);
    }
    TextView getNotificationTitleTextView()
    {
      return tvTitle;
    }

    TextView getNotificationMessageTextView()
    {
      return tvMessage;
    }

    TextView getNotificationDateTextView()
    {
      return tvDate;
    }
  }

  @Override
  public @NonNull NotificationsAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parentView, int viewType)
  {
    View skeletonView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.user_notifications_record, parentView, false);
    return new NotificationsAdapter.NotificationViewHolder(skeletonView);
  }

  @Override
  public int getItemCount()
  {
    return notifications.size();
  }

  public void onBindViewHolder(NotificationsAdapter.NotificationViewHolder skeletonViewHolder, final int position)
  {
    skeletonViewHolder.getNotificationTitleTextView().setText((String)notifications.get(position).get("title"));
    skeletonViewHolder.getNotificationMessageTextView().setText((String)notifications.get(position).get("message"));
    skeletonViewHolder.getNotificationDateTextView().setText((String)notifications.get(position).get("date"));
  }
}
