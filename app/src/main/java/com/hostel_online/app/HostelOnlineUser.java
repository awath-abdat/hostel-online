package com.hostel_online.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;

public class HostelOnlineUser
{
  private OnFinishListener onFinishListener;
  public String userId;
  public String userDisplayName;
  public String userFirstName;
  public String userLastName;
  public String userEmail;
  public String userPhoneNumber;
  public String userCourse;
  public String userGender;
  public String userHostelName;
  public String userRoomLabel;
  public String userCampus;
  public String userRole;
  public String userPhotoUrl;
  public HostelOnlineUser(Context context)
  {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    if(user != null){
      DocumentReference df = db.collection("Users").document(user.getUid());
      df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
      {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task)
        {
          if (task.isSuccessful()) {
            DocumentSnapshot doc = task.getResult();
            if (doc != null && doc.exists()) {
              userId = user.getUid();
              userDisplayName = user.getDisplayName();
              userFirstName = (String) doc.get("FirstName");
              userLastName = (String) doc.get("LastName");
              userEmail = (String) doc.get("UserEmail");
              userCourse = (String) doc.get("Course");
              userCampus = (String) doc.get("Campus");
              userGender = (String) doc.get("Gender");
              userPhoneNumber = (String) doc.get("PhoneNumber");
              userRole = (String) doc.get("Role");
              userRoomLabel = (String) doc.get("RoomLabel");
              userHostelName = (String) doc.get("Hostel");
              Uri photoUri = user.getPhotoUrl();
              if (photoUri != null) {
                userPhotoUrl = photoUri.toString();
              }
              if(userFirstName == null || userFirstName.length() == 0)
              {
                userId = user.getUid();
                for(UserInfo profile : user.getProviderData())
                {
                  userDisplayName = profile.getDisplayName();
                  if(userDisplayName != null)
                  {
                    userFirstName = userDisplayName.split(" ")[0];
                    userLastName = userDisplayName.split(" ")[1];
                  }
                  userEmail = profile.getEmail();
                  Uri photo = profile.getPhotoUrl();
                  if(photo != null) {
                    userPhotoUrl = photo.toString();
                  }
                }
                Intent intent = new Intent(context, SignUp.class);
                intent.putExtra("SignInProvider", "Google");
                context.startActivity(intent);
              }else{
                if(onFinishListener != null)
                  onFinishListener.onFinish();
              }
            }else{
              if(!doc.exists()) {
                Intent intent = new Intent(context, SignUp.class);
                context.startActivity(intent);
              }
            }
          } else {
            Toast.makeText(context, "Failed to retrieve document.", Toast.LENGTH_LONG).show();
          }
        }
      });
    }else{
      Log.w("Creating User: ", "User is null in creating new HostelOnlineUser");
    }
  }

  public void setOnFinishListener(OnFinishListener e)
  {
    this.onFinishListener = e;
  }
}
