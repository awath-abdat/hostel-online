package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    if(hostelOnlineUser != null && hostelOnlineUser.userId != null)
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
            hostelOnlineUser = new HostelOnlineUser(getApplicationContext());
            hostelOnlineUser.setOnFinishListener(new OnFinishListener(){
              @Override
              public void onFinish()
              {
                chooseIntentForUser();
              }
            });
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
      public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful())
        {
          hostelOnlineUser = new HostelOnlineUser(getApplicationContext());
          hostelOnlineUser.setOnFinishListener(new OnFinishListener(){
            @Override
            public void onFinish()
            {
              chooseIntentForUser();
            }
          });
        }else{
          Log.w("Google Sign In", "FirebaseAuthWithGoogle failed.");
          Toast.makeText(getApplicationContext(), "Sign in unsuccessful, check your network connection or try again later.", Toast.LENGTH_LONG).show();
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
      hostelOnlineUser = new HostelOnlineUser(getApplicationContext());
    if(hostelOnlineUser.userRole != null && (hostelOnlineUser.userRoomLabel == null || hostelOnlineUser.userHostelName == null || hostelOnlineUser.userRoomLabel.length() == 0 || hostelOnlineUser.userHostelName.length() == 0))
    {
      if(hostelOnlineUser.userRole.equals("Student"))
      {
        Intent sendHostelsListIntent = new Intent(getApplicationContext(), HostelsList.class);
        startActivity(sendHostelsListIntent);
      }else{
        Intent sendUserIntent = new Intent(getApplicationContext(), User.class);
        startActivity(sendUserIntent);
      }
    }else{
      Intent intent = new Intent(getApplicationContext(), User.class);
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
    if (mainFirebaseAuth.getCurrentUser() == null) {
      Intent sendSignUpIntent = new Intent(this, SignUp.class);
      startActivity(sendSignUpIntent);
    }
  }
}