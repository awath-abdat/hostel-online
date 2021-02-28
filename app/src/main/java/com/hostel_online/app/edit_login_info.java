package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.hostel_online.app.MainActivity.hostelOnlineUser;


public class edit_login_info extends AppCompatActivity
{
  private FirebaseUser user;
  private FirebaseAuth auth;
  private EditText tEmail;
  private EditText tPassword;
  private EditText tFirstName;
  private Button bUpdate;
  private EditText tLastName;
  private EditText tPhoneNumber;
  private Spinner genderSpinner;
  private Spinner courseSpinner;
  private Spinner campusSpinner;
  private ImageView tsignUpProfileImage;
  private Uri selectedImage;
  private FirebaseFirestore firestore;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_login_info);
    Intent UpdateIntent = getIntent();
    hostelOnlineUser = UpdateIntent.getParcelableExtra("hostelOnlineUser");
    auth=FirebaseAuth.getInstance();
    user = auth.getCurrentUser();

    tsignUpProfileImage = findViewById(R.id.newsign_up_profile_image);
    tFirstName = (EditText) findViewById(R.id.newsign_up_first_name);
    tLastName = (EditText) findViewById(R.id.newsign_up_last_name);
    tPhoneNumber=(EditText)findViewById(R.id.newsign_up_phone_number);
    bUpdate=findViewById(R.id.newsign_up_button);


    ArrayList<String> genders = new ArrayList<>();
    genders.add("Male");
    genders.add("Female");
    ArrayList<String> courses = new ArrayList<>();
    courses.add("Computer Science(BS)");
    courses.add("Software Engineering(BS)");
    ArrayList<String> campuses = new ArrayList<>();
    campuses.add("Makerere University(MUK)");
    courses.add("Kyambogo University");
    genderSpinner = findViewById(R.id.newsign_up_gender_spinner);
    genderSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, genders));
    courseSpinner = findViewById(R.id.newsign_up_course_spinner);
    courseSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, courses));
    campusSpinner = findViewById(R.id.newsign_up_campus_spinner);
    campusSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, campuses));

    firestore = FirebaseFirestore.getInstance();

    bUpdate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateUserDatabase(user);
      }
    });



  }
  public void updateUserDatabase(FirebaseUser user)

  {
    DocumentReference df = firestore.collection("Users").document(user.getUid());
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("FirstName", tFirstName.getText().toString());
    userInfo.put("LastName", tLastName.getText().toString());
    userInfo.put("PhoneNumber", tPhoneNumber.getText().toString());
    userInfo.put("Gender", genderSpinner.getSelectedItem().toString());
    userInfo.put("Course", courseSpinner.getSelectedItem().toString());
    userInfo.put("Campus", campusSpinner.getSelectedItem().toString());
    userInfo.put("Role", hostelOnlineUser.getUserRole());
    userInfo.put("UserEmail", hostelOnlineUser.getUserEmail());

    String filepath;
    df.set(userInfo);
    Log.w("User Info", userInfo.toString());
    hostelOnlineUser.setUserId(user.getUid());
    hostelOnlineUser.setUserFirstName(tFirstName.getText().toString());
    hostelOnlineUser.setUserLastName(tLastName.getText().toString());
    hostelOnlineUser.setUserPhoneNumber(tPhoneNumber.getText().toString());
    hostelOnlineUser.setUserGender(genderSpinner.getSelectedItem().toString());
    hostelOnlineUser.setUserCourse(courseSpinner.getSelectedItem().toString());
    hostelOnlineUser.setUserCampus(campusSpinner.getSelectedItem().toString());
    hostelOnlineUser.setUserRole(hostelOnlineUser.getUserRole());
    if(selectedImage != null)
    {
      Cursor cursor = edit_login_info.this.getContentResolver().query(selectedImage, null, null, null, null);
      if(cursor == null)
      {
        filepath = selectedImage.getPath();
      }else{
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        filepath = cursor.getString(idx);
        cursor.close();
      }
      SignUp.UploadPhotoTask uploadPhotoTask = new SignUp.UploadPhotoTask(edit_login_info.this, user, hostelOnlineUser);
      uploadPhotoTask.execute(filepath);
    }
    UserProfileChangeRequest profileUpdates;
    profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(tFirstName.getText().toString() + " " + tLastName.getText().toString()).build();
    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>(){
      @Override
      public void onComplete(@NonNull Task<Void> task)
      {
        if(task.isSuccessful())
        {
          Toast.makeText(edit_login_info.this, "Account Created", Toast.LENGTH_SHORT).show();
          Log.w("Task Complete", "Task Update Profile is Complete");
        }
      }
    });
  }
}

