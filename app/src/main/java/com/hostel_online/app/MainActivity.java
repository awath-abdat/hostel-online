package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
  public static HostelOnlineUser hostelOnlineUser;
  private static final int  RC_SIGN_IN = 123;
  private FirebaseAuth mainFirebaseAuth;
  private EditText etSignInEmail;
  private EditText etSignInPassword;
  GoogleSignInClient googleSignInClient;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    if(hostelOnlineUser != null && hostelOnlineUser.getUserId() != null)
    {
      chooseIntentForUser();
    }
    mainFirebaseAuth = FirebaseAuth.getInstance();
    super.onCreate(savedInstanceState);
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("921715852424-t4ii2on8enqmp4f7hnq430soq9ksg9pi.apps.googleusercontent.com").requestEmail().build();
    googleSignInClient =  GoogleSignIn.getClient(this, gso);
    setContentView(R.layout.activity_main);
    etSignInEmail = (EditText) findViewById(R.id.sign_in_email);
    etSignInPassword = (EditText) findViewById(R.id.sign_in_password);
    Button bSignInButton = findViewById(R.id.sign_in_button);
    bSignInButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        mainFirebaseAuth.signInWithEmailAndPassword(etSignInEmail.getText().toString(), etSignInPassword.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>()
        {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task)
          {
            if(task.isSuccessful())
            {
              FirebaseFirestore db = FirebaseFirestore.getInstance();
              FirebaseUser user = mainFirebaseAuth.getCurrentUser();
              hostelOnlineUser = new HostelOnlineUser();
              if(user != null)
              {
                DocumentReference df = db.collection("Users").document(user.getUid());
                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task)
                  {
                    if(task.isSuccessful())
                    {
                      DocumentSnapshot doc = task.getResult();
                      if(doc != null && doc.exists())
                      {
                        hostelOnlineUser.setUserId(user.getUid());
                        hostelOnlineUser.setUserFirstName((String)doc.get("FirstName"));
                        hostelOnlineUser.setUserLastName((String)doc.get("LastName"));
                        hostelOnlineUser.setUserEmail((String)doc.get("UserEmail"));
                        hostelOnlineUser.setUserCourse((String)doc.get("Course"));
                        hostelOnlineUser.setUserRole((String)doc.get("Role"));
                        hostelOnlineUser.setUserGender((String)doc.get("Gender"));
                        hostelOnlineUser.setUserCampus((String)doc.get("Campus"));
                        hostelOnlineUser.setUserPhoneNumber((String)doc.get("PhoneNumber"));
                        Uri photoUrl = user.getPhotoUrl();
                        if(photoUrl != null)
                          hostelOnlineUser.setUserPhotoUrl(photoUrl.toString());
                        chooseIntentForUser();
                      }else{
                        Intent signUpSendIntent = new Intent(MainActivity.this, SignUp.class);
                        signUpSendIntent.putExtra("SignInProvider", "Email");
                        startActivity(signUpSendIntent);
                      }
                    }else{
                      Toast.makeText(MainActivity.this, "Failed to retrieve user information, please try again.", Toast.LENGTH_LONG).show();
                    }
                  }
                });
              }else{
                Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
              }
            }else{
              Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
            }
          }
        });
      }
    });

    Button bSignInWithGoogle = findViewById(R.id.sign_in_with_google);
    bSignInWithGoogle.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        signInWithGoogle(v);
      }
    });
  }

  private void firebaseAuthWithGoogle(String idToken)
  {
    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
    mainFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task)
      {
        if(task.isSuccessful())
        {
          FirebaseFirestore db = FirebaseFirestore.getInstance();
          FirebaseUser user = mainFirebaseAuth.getCurrentUser();
          hostelOnlineUser = new HostelOnlineUser();
          if(user != null)
          {
            DocumentReference df = db.collection("Users").document(user.getUid());
            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task)
              {
                if(task.isSuccessful())
                {
                  DocumentSnapshot doc = task.getResult();
                  if(doc != null && doc.exists())
                  {
                    hostelOnlineUser.setUserId(user.getUid());
                    hostelOnlineUser.setUserFirstName((String)doc.get("FirstName"));
                    hostelOnlineUser.setUserLastName((String)doc.get("LastName"));
                    hostelOnlineUser.setUserEmail((String)doc.get("UserEmail"));
                    hostelOnlineUser.setUserCourse((String)doc.get("Course"));
                    hostelOnlineUser.setUserRole((String)doc.get("Role"));
                    hostelOnlineUser.setUserGender((String)doc.get("Gender"));
                    hostelOnlineUser.setUserCampus((String)doc.get("Campus"));
                    hostelOnlineUser.setUserPhoneNumber((String)doc.get("PhoneNumber"));
                    Uri photoUrl = user.getPhotoUrl();
                    if(photoUrl != null)
                      hostelOnlineUser.setUserPhotoUrl(photoUrl.toString());
                    chooseIntentForUser();
                  }else{
                    for(UserInfo profile : user.getProviderData())
                    {
                      String displayName = profile.getDisplayName();
                      if(displayName != null)
                      {
                        hostelOnlineUser.setUserFirstName(displayName.split(" ")[0]);
                        hostelOnlineUser.setUserLastName(displayName.split(" ")[1]);
                      }
                      hostelOnlineUser.setUserEmail(profile.getEmail());
                    }
                    Intent signUpSendIntent = new Intent(MainActivity.this, SignUp.class);
                    signUpSendIntent.putExtra("SignInProvider", "Google");
                    startActivity(signUpSendIntent);
                  }
                }else{
                  Toast.makeText(MainActivity.this, "Failed to retrieve user information, please try again.", Toast.LENGTH_LONG).show();
                }
              }
            });
          }else{
            Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
          }
        }else{
          Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try{
        GoogleSignInAccount account = task.getResult(ApiException.class);
        if(account != null)
        {
          firebaseAuthWithGoogle(account.getIdToken());
        }
      }catch(ApiException e){
        Log.w("Google Sign In", "FirebaseAuthWithGoogle failed. " + e.toString());
        Toast.makeText(getApplicationContext(), "Sign in unsuccessful, check your network connection or try again later.", Toast.LENGTH_LONG).show();
      }
    }
  }

  public void chooseIntentForUser()
  {
    if(hostelOnlineUser == null)
    {
      Toast.makeText(MainActivity.this, "Please Login or Sign Up.", Toast.LENGTH_LONG).show();
    }
    if(hostelOnlineUser.getUserRole() != null && (hostelOnlineUser.getUserRoomLabel() == null || hostelOnlineUser.getUserHostelId() == null || hostelOnlineUser.getUserRoomLabel().length() == 0 || hostelOnlineUser.getUserHostelId().length() == 0))
    {
      if(hostelOnlineUser.getUserRole().equals("Student"))
      {
        Intent sendHostelsListIntent = new Intent(getApplicationContext(), HostelsList.class);
        sendHostelsListIntent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
        startActivity(sendHostelsListIntent);
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

  public void signInWithGoogle(View v)
  {
    Intent signInIntent = googleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  public void startSignUpActivity(View v)
  {
    if(hostelOnlineUser == null) {
      Intent sendSignUpIntent = new Intent(this, SignUp.class);
      startActivity(sendSignUpIntent);
    }else{
      chooseIntentForUser();
    }
  }
}