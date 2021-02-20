package com.hostel_online.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class user_profile extends Fragment
{
  private HostelOnlineUser hostelOnlineUser;
  public user_profile(HostelOnlineUser hostelOnlineUser)
  {
    this.hostelOnlineUser = hostelOnlineUser;
  }

  public static user_profile newInstance(HostelOnlineUser hostelOnlineUser)
  {
    user_profile fragment = new user_profile(hostelOnlineUser);
    Bundle args = new Bundle();
    args.putParcelable("HostelOnlineUser", (Parcelable) hostelOnlineUser);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      hostelOnlineUser = getArguments().getParcelable("HostelOnlineUser");
    }else{
      Intent sendMainActivityIntent = new Intent(getActivity(), MainActivity.class);
      startActivity(sendMainActivityIntent);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View fragmentView = inflater.inflate(R.layout.fragment_user_profile, container, false);
    ImageView userProfileImage = fragmentView.findViewById(R.id.user_profile_image);
    String url = hostelOnlineUser.getUserPhotoUrl();
    GlideApp.with(user_profile.this).load(url).into(userProfileImage);
    ((TextView)fragmentView.findViewById(R.id.user_profile_text)).setText(hostelOnlineUser.getUserDisplayName());
    ((TextView)fragmentView.findViewById(R.id.user_email_text)).setText(hostelOnlineUser.getUserEmail());
    ((TextView)fragmentView.findViewById(R.id.user_phone_number_text)).setText(hostelOnlineUser.getUserPhoneNumber());
    ((TextView)fragmentView.findViewById(R.id.user_course_text)).setText(hostelOnlineUser.getUserCourse());
    ((TextView)fragmentView.findViewById(R.id.user_gender_text)).setText(hostelOnlineUser.getUserGender());
    if(hostelOnlineUser.getUserHostelId() != null)
    {
      ((TextView)fragmentView.findViewById(R.id.user_hostel_text)).setText(hostelOnlineUser.getUserHostelId());
      ((TextView)fragmentView.findViewById(R.id.user_room_text)).setText(hostelOnlineUser.getUserRoomLabel());
    }else{
      (fragmentView.findViewById(R.id.user_hostel_text)).setVisibility(View.GONE);
      (fragmentView.findViewById(R.id.user_room_text)).setVisibility(View.GONE);
      (fragmentView.findViewById(R.id.user_room)).setVisibility(View.GONE);
      (fragmentView.findViewById(R.id.user_hostel)).setVisibility(View.GONE);
    }
    return fragmentView;
  }
}