package com.hostel_online.app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class user_notifications extends Fragment
{

  public user_notifications()
  {
  }

  public static user_notifications newInstance(String param1, String param2)
  {
    user_notifications fragment = new user_notifications();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {

    return inflater.inflate(R.layout.fragment_user_notifications, container, false);
  }
}