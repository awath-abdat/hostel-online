package com.hostel_online.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class user_home extends Fragment
{
  private HostelOnlineUser hostelOnlineUser;
  public user_home(HostelOnlineUser hostelOnlineUser)
  {
    this.hostelOnlineUser = hostelOnlineUser;
  }

  public static user_home newInstance(HostelOnlineUser hostelOnlineUser)
  {
    user_home fragment = new user_home(hostelOnlineUser);
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      hostelOnlineUser = getArguments().getParcelable("HostelOnlineUser");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    Notification[] notifications = {new Notification("Important", "Bla bla bla"), new Notification("Normal", "Bla bla bla")};
    View fragment = inflater.inflate(R.layout.fragment_user_home, container, false);
    ((TextView)fragment.findViewById(R.id.user_parent_display_name)).setText(hostelOnlineUser.getUserDisplayName());
    ImageView userProfileImage = fragment.findViewById(R.id.user_profile_image);
    String url = hostelOnlineUser.getUserPhotoUrl();
    if(url != null)
      GlideApp.with(user_home.this).load(url).into(userProfileImage);
    RecyclerView rvNotifications = fragment.findViewById(R.id.user_notification_recycler);
    rvNotifications.setAdapter(new NotificationsAdapter(notifications));
    return fragment;
  }
}