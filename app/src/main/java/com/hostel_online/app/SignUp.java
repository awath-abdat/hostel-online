package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity
{
  private FirebaseAuth signUpAuth;
  private FirebaseFirestore firestore;
  private String SignInProvider;
  private EditText etEmail;
  private EditText etPassword;
  private EditText etFirstName;
  private EditText etLastName;
  private EditText etPhoneNumber;
  private Spinner genderSpinner;
  private Spinner courseSpinner;
  private Spinner campusSpinner;
  private ImageView signUpProfileImage;
  private Uri selectedImage;
  private HostelOnlineUser hostelOnlineUser;
  static class UploadPhotoTask extends AsyncTask<String, Integer, String>{
    Map uploadResult;
    FirebaseUser user;
    HostelOnlineUser hostelOnlineUser;
    private final WeakReference<Context> contextRef;
    public UploadPhotoTask(Context context, FirebaseUser user, HostelOnlineUser hostelOnlineUser)
    {
      this.contextRef = new WeakReference<>(context);
      this.user = user;
      this.hostelOnlineUser = hostelOnlineUser;
    }

    @Override
    protected String doInBackground(String... filepath)
    {
      Context context = contextRef.get();
      try {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloude_name", "dynyjan4a", "api_key", context.getResources().getString(R.string.cloudinary_key), "api_secret", context.getResources().getString(R.string.cloudinary_secret)));
        uploadResult = cloudinary.uploader().upload(filepath[0], ObjectUtils.asMap("cloud_name", "dynyjan4a", "folder", "Users", "public_id", hostelOnlineUser.getUserId()));
        Log.d("Cloud Upload Result", uploadResult.toString());
      } catch (Exception e) {
        Log.e("Fatal Cloudinery Failed", e.toString());
      }
      return (String)uploadResult.get("secure_url");
    }
    @Override
    public void onPostExecute(String result)
    {
      Context context = contextRef.get();
      UserProfileChangeRequest profileUpdates;
      profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse((String)uploadResult.get("secure_url"))).build();
      user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>(){
        @Override
        public void onComplete(@NonNull Task<Void> task)
        {
          if(task.isSuccessful())
          {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference df = db.collection("Campuses").document(hostelOnlineUser.getUserCampus());
            df.get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task1 -> {
              if(task1.isSuccessful())
              {
                DocumentSnapshot doc = task1.getResult();
                if(doc != null && doc.exists())
                {
                  Timber.tag("Campus Info").w((String) doc.get("Name"));
                  Timber.tag("User").w(hostelOnlineUser.toString());
                  @SuppressWarnings("ConstantConditions") double[] campusLocation = {doc.get("Lat") != null ? (double)doc.get("Lat") : 0.347596f, doc.get("Lng") != null ? (double)doc.get("Lat") : 32.582520};
                  Intent sendHostelsListIntent = new Intent(context.getApplicationContext(), HostelsList.class);
                  sendHostelsListIntent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
                  sendHostelsListIntent.putExtra("CampusLocation", campusLocation);
                  context.startActivity(sendHostelsListIntent);
                }
              }
            });
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
    hostelOnlineUser = (HostelOnlineUser)signUpIntentReceive.getParcelableExtra("HostelOnlineUser");
    if(SignInProvider != null && SignInProvider.equals("Google") && hostelOnlineUser != null)
    {
      setContentView(R.layout.activity_sign_up_with_provider);
      signUpProfileImage = findViewById(R.id.sign_up_profile_image);
      etFirstName = (EditText) findViewById(R.id.sign_up_first_name);
      etLastName = (EditText) findViewById(R.id.sign_up_last_name);
      ((TextView)(findViewById(R.id.sign_up_fill_in_details))).setText(getResources().getString(R.string.make_changes_where_necessary));
      etFirstName.setText(hostelOnlineUser.getUserFirstName());
      etLastName.setText(hostelOnlineUser.getUserLastName());
      if(hostelOnlineUser.getUserPhotoUrl() != null)
      {
        GlideApp.with(this).load(hostelOnlineUser.getUserPhotoUrl()).into(signUpProfileImage);
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
    
    etEmail.addTextChangedListener(new TextChangedListener(etEmail));
    etPassword.addTextChangedListener(new TextChangedListener(etPassword));
    etPhoneNumber.addTextChangedListener(new TextChangedListener(etPhoneNumber));
    etLastName.addTextChangedListener(new TextChangedListener(etLastName));
    etFirstName.addTextChangedListener(new TextChangedListener(etFirstName));

    ArrayList<String> genders = new ArrayList<>();
    genders.add("Male");
    genders.add("Female");
    ArrayList<String> courses = new ArrayList<>();
    courses.add("Computer Science(BS)");
    courses.add("Software Engineering(BS)");
    ArrayList<String> campuses = new ArrayList<>();
    campuses.add("Makerere University(MUK)");
    courses.add("Kyambogo University");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    genderSpinner = findViewById(R.id.sign_up_gender_spinner);
    genderSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, genders));
    courseSpinner = findViewById(R.id.sign_up_course_spinner);
    courseSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, courses));
    campusSpinner = findViewById(R.id.sign_up_campus_spinner);
    campusSpinner.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, campuses));
    db.collection("Campuses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
    {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task)
      {
        if(task.isSuccessful())
        {
          ArrayList<String> lcampuses = new ArrayList<>();
          for(QueryDocumentSnapshot doc : task.getResult())
          {
            lcampuses.add((String)doc.get("Name"));
          }
          campusSpinner.setAdapter(new SpinnerArrayAdapter(SignUp.this, R.layout.spinner_layout, lcampuses));
        }
      }
    });
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
        Toast.makeText(SignUp.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("FirstName", etFirstName.getText().toString());
    userInfo.put("LastName", etLastName.getText().toString());
    if(etEmail != null || SignInProvider == null)
    {
      userInfo.put("UserEmail", etEmail.getText().toString());
      if(hostelOnlineUser == null)
        hostelOnlineUser = new HostelOnlineUser();
      hostelOnlineUser.setUserEmail(etEmail.getText().toString());
    }else{
      userInfo.put("UserEmail", hostelOnlineUser.getUserEmail());
    }
    userInfo.put("PhoneNumber", etPhoneNumber.getText().toString());
    userInfo.put("Gender", genderSpinner.getSelectedItem().toString());
    userInfo.put("Course", courseSpinner.getSelectedItem().toString());
    userInfo.put("Campus", campusSpinner.getSelectedItem().toString());
    userInfo.put("Role", "Student");
    String filepath;
    df.set(userInfo);
    Log.w("User Info", userInfo.toString());
    hostelOnlineUser.setUserId(user.getUid());
    hostelOnlineUser.setUserFirstName(etFirstName.getText().toString());
    hostelOnlineUser.setUserLastName(etLastName.getText().toString());
    hostelOnlineUser.setUserPhoneNumber(etPhoneNumber.getText().toString());
    hostelOnlineUser.setUserGender(genderSpinner.getSelectedItem().toString());
    hostelOnlineUser.setUserCourse(courseSpinner.getSelectedItem().toString());
    hostelOnlineUser.setUserCampus(campusSpinner.getSelectedItem().toString());
    hostelOnlineUser.setUserRole("Student");
    if(selectedImage != null)
    {
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
      UploadPhotoTask uploadPhotoTask = new UploadPhotoTask(SignUp.this, user, hostelOnlineUser);
      uploadPhotoTask.execute(filepath);
    }
    UserProfileChangeRequest profileUpdates;
    profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(etFirstName.getText().toString() + " " + etLastName.getText().toString()).build();
    user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
      if(task.isSuccessful())
      {
        Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_SHORT).show();
        Log.w("Task Complete", "Task Update Profile is Complete");
      }
    });
  }

  public void signUp(View v)
  { if (!validateFName()) {
    return;
  }if (!validateLName()) {
    return;
  }
    if (!validateEmail()){
      return;
    }if (!validatePhoneNumber()){
    return;
  }if (!validatePassword()) {
    return;
  }
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if(SignInProvider == null)
    {
      auth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>()
      {
        @Override
        public void onSuccess(AuthResult authResult) {
          FirebaseUser user = auth.getCurrentUser();
          if(user != null)
          {
            updateUserDatabase(user);
            chooseIntentForUser();
          }else{
            Toast.makeText(SignUp.this,"Failed to Create Account, try again later.",Toast.LENGTH_SHORT).show();
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Toast.makeText(SignUp.this, e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
      });
    }else{
      FirebaseUser user = auth.getCurrentUser();
      if(user != null) {
        updateUserDatabase(user);
      }
    }
  }

  public void chooseIntentForUser()
  {
    if(hostelOnlineUser == null)
    {
      Toast.makeText(SignUp.this, "Please Login or Sign Up.", Toast.LENGTH_LONG).show();
    }
    if(hostelOnlineUser.getUserRole() != null && (hostelOnlineUser.getUserRoomLabel() == null || hostelOnlineUser.getUserHostelId() == null || hostelOnlineUser.getUserRoomLabel().length() == 0 || hostelOnlineUser.getUserHostelId().length() == 0))
    {
      if(hostelOnlineUser.getUserRole().equals("Student"))
      {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df = db.collection("Campuses").document(hostelOnlineUser.getUserCampus());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task)
          {
            if(task.isSuccessful())
            {
              DocumentSnapshot doc = task.getResult();
              if(doc.exists())
              {
                Log.w("Campus Info", (String)doc.get("Name"));
                double[] campusLocation = {(double)doc.get("Lat"), (double)doc.get("Lng")};
                Intent sendHostelsListIntent = new Intent(getApplicationContext(), HostelsList.class);
                sendHostelsListIntent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
                sendHostelsListIntent.putExtra("CampusLocation", campusLocation);
                startActivity(sendHostelsListIntent);
              }
            }
          }
        });
      }else{
        Intent sendUserIntent = new Intent(getApplicationContext(), User.class);
        sendUserIntent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
        startActivity(sendUserIntent);
      }
    }else{
      Intent intent = new Intent(getApplicationContext(), User.class);
      intent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
      startActivity(intent);
    }
  }

  public boolean validatePassword() {
    if (etPassword.getText().toString().trim().isEmpty()) {
      etPassword.setError("Password is required");
      etPassword.requestFocus();
      return false;
    } else if (etPassword.getText().toString().length() < 6) {
      etPassword.setError("Password can't be less than 6 digit");
      etPassword.requestFocus();
      return false;
    } else
      // password.setErrorEnabled(false);

      return true;
  }
  public boolean validatePhoneNumber() {
    String PhoneNumber = etPhoneNumber.getText().toString();
    String regex = "^(\\d{3}[- .]?){2}\\d{4}$";
    if (etPhoneNumber.getText().toString().trim().isEmpty()) {
      etPhoneNumber.setError("Please Enter Last Name");
      etPhoneNumber.requestFocus();
    } else if (!(PhoneNumber.matches(regex))) {
      etPhoneNumber.setError("Please enter valid Phone number like 077 202 4843");
    }

    return true;
  }

  public boolean validateLName() {
    if (etLastName.getText().toString().trim().isEmpty()) {
      etLastName.setError("Please Enter Last Name");
      etLastName.requestFocus();
    }
    return true;
  }

  public boolean validateFName() {
    if (etFirstName.getText().toString().trim().isEmpty()) {
      etFirstName.setError("Please Enter First Name");
      etFirstName.requestFocus();
    }
    return true;
  }

  public boolean validateEmail() {
    if (etEmail.getText().toString().trim().isEmpty()) {
      etEmail.setError("Please enter email");
    } else {
      String emailId = etEmail.getText().toString();
      boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
      if (!isValid) {
        etEmail.setError("Invalid Email address, ex: abc@example.com");
        etEmail.requestFocus();
        return false;
      }
      // email.setErrorEnabled(false);

    }
    return true;

  }

  private class TextChangedListener implements TextWatcher {
    private final View view;

    public TextChangedListener(View view) {
      this.view = view;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void afterTextChanged(Editable s) {
      switch (view.getId()) {
        case R.id.sign_up_email:
          validateEmail();
          break;
        case R.id.sign_up_password:
          validatePassword();
          break;
        case R.id.sign_up_first_name:
          validateFName();
          break;
        case R.id.sign_up_phone_number:
          validatePhoneNumber();
          break;
        case R.id.sign_up_last_name:
          validateLName();
          break;
      }
    }

  }}
