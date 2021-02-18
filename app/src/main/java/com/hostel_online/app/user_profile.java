package com.hostel_online.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link user_profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user_profile extends Fragment
{
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;
  private FirebaseUser user;

  public user_profile()
  {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment user_profile.
   */
  // TODO: Rename and change types and number of parameters
  public static user_profile newInstance(String param1, String param2)
  {
    user_profile fragment = new user_profile();
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
    user = FirebaseAuth.getInstance().getCurrentUser();
    if(user == null)
    {
      Intent sendMainActivityIntent = new Intent(getActivity(), MainActivity.class);
      startActivity(sendMainActivityIntent);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View fragmentView = inflater.inflate(R.layout.fragment_user_profile, container, false);
    ImageView userProfileImage = fragmentView.findViewById(R.id.user_profile_image);
    String url = MainActivity.hostelOnlineUser.userPhotoUrl;
    GlideApp.with(getActivity()).load(url).into(userProfileImage);
    ((TextView)fragmentView.findViewById(R.id.user_profile_text)).setText(MainActivity.hostelOnlineUser.userDisplayName);
    ((TextView)fragmentView.findViewById(R.id.user_email_text)).setText(MainActivity.hostelOnlineUser.userEmail);
    ((TextView)fragmentView.findViewById(R.id.user_phone_number_text)).setText(MainActivity.hostelOnlineUser.userPhoneNumber);
    ((TextView)fragmentView.findViewById(R.id.user_course_text)).setText(MainActivity.hostelOnlineUser.userCourse);
    ((TextView)fragmentView.findViewById(R.id.user_gender_text)).setText(MainActivity.hostelOnlineUser.userGender);
    if(MainActivity.hostelOnlineUser.userHostelName != null)
    {
      ((TextView)fragmentView.findViewById(R.id.user_hostel_text)).setText(MainActivity.hostelOnlineUser.userHostelName);
      ((TextView)fragmentView.findViewById(R.id.user_room_text)).setText(MainActivity.hostelOnlineUser.userRoomLabel);
    }else{
      ((TextView)fragmentView.findViewById(R.id.user_hostel_text)).setVisibility(View.GONE);
      ((TextView)fragmentView.findViewById(R.id.user_room_text)).setVisibility(View.GONE);
      ((ImageView)fragmentView.findViewById(R.id.user_room)).setVisibility(View.GONE);
      ((ImageView)fragmentView.findViewById(R.id.user_hostel)).setVisibility(View.GONE);
    }
    return fragmentView;
  }
}