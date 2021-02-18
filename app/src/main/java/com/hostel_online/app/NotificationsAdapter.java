package com.hostel_online.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>
{
  Notification[] notifications;
  public NotificationsAdapter(Notification[] notifications)
  {
    this.notifications = notifications;
  }

  public static class NotificationViewHolder extends RecyclerView.ViewHolder
  {
    TextView tvUrgency;
    TextView tvBody;
    public NotificationViewHolder(View skeletonView)
    {
      super(skeletonView);
      tvUrgency = (TextView) skeletonView.findViewById(R.id.notification_urgency);
      tvBody = (TextView) skeletonView.findViewById(R.id.notification_body);
    }
    TextView getNotificationUrgencyTextView()
    {
      return tvUrgency;
    }

    TextView getNotificationBodyTextView()
    {
      return tvBody;
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
    return notifications.length;
  }

  public void onBindViewHolder(NotificationsAdapter.NotificationViewHolder skeletonViewHolder, final int position)
  {
    skeletonViewHolder.getNotificationUrgencyTextView().setText(notifications[position].getUrgency());
    skeletonViewHolder.getNotificationBodyTextView().setText(notifications[position].getBody());
  }
}
