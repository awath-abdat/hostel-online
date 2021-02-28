package com.hostel_online.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View fragment = inflater.inflate(R.layout.fragment_user_home, container, false);
    ((TextView)fragment.findViewById(R.id.user_parent_display_name)).setText(hostelOnlineUser.getUserDisplayName());
    ImageView userProfileImage = fragment.findViewById(R.id.user_profile_image);
    String url = hostelOnlineUser.getUserPhotoUrl();
    if(url != null)
      GlideApp.with(user_home.this).load(url).into(userProfileImage);
    RecyclerView rvNotifications = fragment.findViewById(R.id.user_notification_recycler);
    ArrayList<Map<String, Object>> notifications = new ArrayList<>();
    db.collection("Notifications").whereEqualTo("hostelId", hostelOnlineUser.getUserHostelId()).get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
      if(task.isSuccessful() && task.getResult() != null)
      {
        notifications.clear();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDay = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatIn = new SimpleDateFormat("yyyy/MM/dd");
        for(QueryDocumentSnapshot doc : task.getResult())
        {
          Map<String, Object> notification = new HashMap<>();
          notification.put("title", (String) doc.get("title"));
          notification.put("message", (String) doc.get("message"));
          try{
            Date d = formatDay.parse((String)doc.get("date"));
            notification.put("date", formatDay.format(d));
          }catch(Exception e){
            Timber.tag("Time except:").w(e.getLocalizedMessage());
          }
          notifications.add(notification);
        }
        if(notifications.size() <= 0) {
          Map<String, Object> notification = new HashMap<>();
          notification.put("title", "No Notifications");
          notification.put("message", "There are currently no notifications on your hostel timeline");
          notification.put("date", "Now");
          notifications.add(notification);
        }
        rvNotifications.setAdapter(new NotificationsAdapter(notifications));
      }else{
        Toast.makeText(getActivity(), "Failed to retreive notifications.", Toast.LENGTH_LONG).show();
        Timber.tag("Notifications:").w("Failed to retrieve notificaitions, task failed or is null.");
      }
    }).addOnFailureListener((OnFailureListener) e -> {
      Toast.makeText(getActivity(), "Failed to retreive notifications.", Toast.LENGTH_LONG).show();
      Timber.tag("Notifications:").w(e.getLocalizedMessage());
    });
    if(notifications.size() <= 0) {
      Map<String, Object> notification = new HashMap<>();
      notification.put("title", "No Notifications");
      notification.put("message", "There are currently no notifications on your hostel timeline");
      notification.put("date", "Now");
      notifications.add(notification);
      rvNotifications.setAdapter(new NotificationsAdapter(notifications));
    }
    return fragment;
  }
}