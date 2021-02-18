package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity
{
  FirebaseFirestore firestore;
  String SignInProvider;
  EditText etEmail;
  EditText etPassword;
  EditText etFirstName;
  EditText etLastName;
  EditText etPhoneNumber;
  Spinner genderSpinner;
  Spinner courseSpinner;
  Spinner campusSpinner;
  ImageView signUpProfileImage;
  Uri selectedImage;
  public static class UploadPhotoTask extends AsyncTask<String, Integer, String>{
    Map uploadResult;
    FirebaseUser user;
    Context context;
    public UploadPhotoTask(Context context, FirebaseUser user)
    {
      this.context = context;
      this.user = user;
    }

    @Override
    protected String doInBackground(String... filepath)
    {
      try {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloude_name", "dynyjan4a", "api_key", context.getResources().getString(R.string.cloudinary_key), "api_secret", context.getResources().getString(R.string.cloudinary_secret)));
        uploadResult = cloudinary.uploader().upload(filepath[0], ObjectUtils.asMap("cloud_name", "dynyjan4a", "folder", "Users", "public_id", MainActivity.hostelOnlineUser.userId));
        Log.d("Cloud Upload Result", uploadResult.toString());
      } catch (Exception e) {
        Log.e("Fatal Cloudinery Failed", e.toString());
      }
      return (String)uploadResult.get("secure_url");
    }
    @Override
    public void onPostExecute(String result)
    {
      UserProfileChangeRequest profileUpdates;
      profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse((String)uploadResult.get("secure_url"))).build();
      user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>(){
        @Override
        public void onComplete(@NonNull Task<Void> task)
        {
          if(task.isSuccessful())
          {
            Log.w("Task Complete", "Task Update Profile is Complete");
          }else{
            Toast.makeText(context, "Profile Photo not updated.", Toast.LENGTH_LONG).show();
          }
        }
      });
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    firestore = FirebaseFirestore.getInstance();
    super.onCreate(savedInstanceState);
    Intent signUpIntentReceive = getIntent();
    SignInProvider = signUpIntentReceive.getStringExtra("SignInProvider");
    if(SignInProvider != null)
    {
      setContentView(R.layout.activity_sign_up_with_provider);
      signUpProfileImage = findViewById(R.id.sign_up_profile_image);
      etEmail = (EditText) findViewById(R.id.sign_up_email);
      etFirstName = (EditText) findViewById(R.id.sign_up_first_name);
      etLastName = (EditText) findViewById(R.id.sign_up_last_name);
      ((TextView)(findViewById(R.id.sign_up_fill_in_details))).setText(getResources().getString(R.string.make_changes_where_necessary));
      etEmail.setText(MainActivity.hostelOnlineUser.userEmail);
      etFirstName.setText(MainActivity.hostelOnlineUser.userFirstName);
      etLastName.setText(MainActivity.hostelOnlineUser.userLastName);
      if(MainActivity.hostelOnlineUser.userPhotoUrl != null)
      {
        GlideApp.with(this).load(MainActivity.hostelOnlineUser.userPhotoUrl).into(signUpProfileImage);
      }
    }else{
      setContentView(R.layout.activity_sign_up);
      signUpProfileImage = findViewById(R.id.sign_up_profile_image);
      etEmail = (EditText) findViewById(R.id.sign_up_email);
      etFirstName = (EditText) findViewById(R.id.sign_up_first_name);
      etLastName = (EditText) findViewById(R.id.sign_up_last_name);
    }
    etPassword = (EditText) findViewById(R.id.sign_up_password);
    etPhoneNumber = (EditText) findViewById(R.id.sign_up_phone_number);
    String[] genders = {"Male", "Female"}, courses = {"Computer Science(BS)", "Software Engineering(BS)"}, campuses = {"Makerere University(MUK)", "Kyambogo University"};
    genderSpinner = findViewById(R.id.sign_up_gender_spinner);
    genderSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, genders));
    courseSpinner = findViewById(R.id.sign_up_course_spinner);
    courseSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, courses));
    campusSpinner = findViewById(R.id.sign_up_campus_spinner);
    campusSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, campuses));
    signUpProfileImage.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        if(ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
          Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(pickPhoto, 1);
        }else{
          ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
      }
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == 1){
      if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
      }else{
        Toast.makeText(SignUp.this, "permission denied", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
  {
    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    if (requestCode == 1) {
      if (resultCode == RESULT_OK) {
        selectedImage = imageReturnedIntent.getData();
        signUpProfileImage.setImageURI(selectedImage);
      }
    }
  }

  public void updateUserDatabase(FirebaseUser user)
  {
    DocumentReference df = firestore.collection("Users").document(user.getUid());
    Map<String, Object>userInfo = new HashMap<>();
    userInfo.put("FirstName", etFirstName.getText().toString());
    userInfo.put("LastName", etLastName.getText().toString());
    userInfo.put("UserEmail", etEmail.getText().toString());
    userInfo.put("PhoneNumber", etPhoneNumber.getText().toString());
    userInfo.put("Gender", genderSpinner.getSelectedItem().toString());
    userInfo.put("Course", courseSpinner.getSelectedItem().toString());
    userInfo.put("Campus", campusSpinner.getSelectedItem().toString());
    userInfo.put("Role", "Student");
    String filepath;
    df.set(userInfo);
    MainActivity.hostelOnlineUser.userId = user.getUid();
    MainActivity.hostelOnlineUser.userDisplayName = etFirstName.getText().toString() + " " + etLastName.getText().toString();
    MainActivity.hostelOnlineUser.userEmail = etEmail.getText().toString();
    MainActivity.hostelOnlineUser.userPhoneNumber = etPhoneNumber.getText().toString();
    MainActivity.hostelOnlineUser.userGender = genderSpinner.getSelectedItem().toString();
    MainActivity.hostelOnlineUser.userCourse = courseSpinner.getSelectedItem().toString();
    MainActivity.hostelOnlineUser.userCampus = campusSpinner.getSelectedItem().toString();
    MainActivity.hostelOnlineUser.userRole = "Student";
    Cursor cursor = SignUp.this.getContentResolver().query(selectedImage, null, null, null, null);
    if(cursor == null)
    {
      filepath = selectedImage.getPath();
    }else{
      cursor.moveToFirst();
      int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      filepath = cursor.getString(idx);
      cursor.close();
    }
    UserProfileChangeRequest profileUpdates;
    profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(MainActivity.hostelOnlineUser.userDisplayName).build();
    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>(){
      @Override
      public void onComplete(@NonNull Task<Void> task)
      {
        if(task.isSuccessful())
        {
          Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_SHORT).show();
          Log.w("Task Complete", "Task Update Profile is Complete");
        }
      }
    });
    UploadPhotoTask uploadPhotoTask = new UploadPhotoTask(getApplicationContext(), user);
    uploadPhotoTask.execute(filepath);
  }

  public void signUp(View v)
  {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    if(SignInProvider == null)
    {
      auth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>()
      {
        @Override
        public void onSuccess(AuthResult authResult) {
          if(user != null)
          {
            updateUserDatabase(user);
          }else{
            Toast.makeText(SignUp.this,"Failed to Create Account",Toast.LENGTH_SHORT).show();
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Toast.makeText(SignUp.this,"Failed to Create Account General",Toast.LENGTH_SHORT).show();
        }
      });
    }else{
      if(user != null)
        updateUserDatabase(user);
    }
  }
}