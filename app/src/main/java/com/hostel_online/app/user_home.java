package com.hostel_online.app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link user_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user_home extends Fragment
{

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public user_home()
  {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment user_home.
   */
  // TODO: Rename and change types and number of parameters
  public static user_home newInstance(String param1, String param2)
  {
    user_home fragment = new user_home();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    Notification[] notifications = {new Notification("Important", "Bla bla bla"), new Notification("Normal", "Bla bla bla")};
    View fragment = inflater.inflate(R.layout.fragment_user_home, container, false);
    ((TextView)fragment.findViewById(R.id.user_parent_display_name)).setText(MainActivity.hostelOnlineUser.userDisplayName);
    ImageView userProfileImage = fragment.findViewById(R.id.user_profile_image);
    String url = MainActivity.hostelOnlineUser.userPhotoUrl;
    GlideApp.with(getActivity()).load(url).into(userProfileImage);
    RecyclerView rvNotifications = fragment.findViewById(R.id.user_notification_recycler);
    rvNotifications.setAdapter(new NotificationsAdapter(notifications));
    return fragment;
  }
}