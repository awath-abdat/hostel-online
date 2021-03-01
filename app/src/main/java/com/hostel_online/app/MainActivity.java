package com.hostel_online.app;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    Intent receiveMainActivityIntent = getIntent();
    if(hostelOnlineUser != null && hostelOnlineUser.getUserId() != null && receiveMainActivityIntent != null && !receiveMainActivityIntent.getBooleanExtra("LogOut", false))
    {
      chooseIntentForUser();
    }
    Timber.plant(new Timber.DebugTree());
    mainFirebaseAuth = FirebaseAuth.getInstance();
    super.onCreate(savedInstanceState);
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("921715852424-t4ii2on8enqmp4f7hnq430soq9ksg9pi.apps.googleusercontent.com").requestEmail().build();
    googleSignInClient =  GoogleSignIn.getClient(this, gso);
    setContentView(R.layout.activity_main);
    etSignInEmail = (EditText) findViewById(R.id.sign_in_email);
    etSignInPassword = (EditText) findViewById(R.id.sign_in_password);
    Button bSignInButton = findViewById(R.id.sign_in_button);
    bSignInButton.setOnClickListener(v -> mainFirebaseAuth.signInWithEmailAndPassword(etSignInEmail.getText().toString(), etSignInPassword.getText().toString()).addOnCompleteListener(MainActivity.this, task -> {
      if(task.isSuccessful())
      {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mainFirebaseAuth.getCurrentUser();
        hostelOnlineUser = new HostelOnlineUser();
        if(user != null)
        {
          DocumentReference df = db.collection("Users").document(user.getUid());
          df.get().addOnCompleteListener(task1 -> {
            if(task1.isSuccessful())
            {
              DocumentSnapshot doc = task1.getResult();
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
                hostelOnlineUser.setUserHostelId((String)doc.get("HostelId"));
                hostelOnlineUser.setUserRoomLabel((String)doc.get("RoomLabel"));
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
          });
        }else{
          Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
        }
      }else{
        Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
      }
    }));

    Button bSignInWithGoogle = findViewById(R.id.sign_in_with_google);
    bSignInWithGoogle.setOnClickListener(this::signInWithGoogle);
  }

  private void firebaseAuthWithGoogle(String idToken)
  {
    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
    mainFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
      if(task.isSuccessful())
      {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mainFirebaseAuth.getCurrentUser();
        hostelOnlineUser = new HostelOnlineUser();
        if(user != null)
        {
          DocumentReference df = db.collection("Users").document(user.getUid());
          df.get().addOnCompleteListener(task1 -> {
            if(task1.isSuccessful())
            {
              DocumentSnapshot doc = task1.getResult();
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
                hostelOnlineUser.setUserHostelId((String)doc.get("HostelId"));
                hostelOnlineUser.setUserRoomLabel((String)doc.get("RoomLabel"));
                Uri photoUrl = user.getPhotoUrl();
                if(photoUrl != null)
                  hostelOnlineUser.setUserPhotoUrl(photoUrl.toString());
                Log.w("User Hostel", doc.getData().toString());
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
                signUpSendIntent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
                startActivity(signUpSendIntent);
              }
            }else{
              Toast.makeText(MainActivity.this, "Failed to retrieve user information, please try again.", Toast.LENGTH_LONG).show();
            }
          });
        }else{
          Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
        }
      }else{
        Toast.makeText(MainActivity.this, "Sign In failed, if you have no account please sign up.", Toast.LENGTH_LONG).show();
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
        Timber.tag("Google Sign In").w("FirebaseAuthWithGoogle failed. %s", e.toString());
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df = db.collection("Campuses").document(hostelOnlineUser.getUserCampus());
        df.get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
          if(task.isSuccessful())
          {
            DocumentSnapshot doc = task.getResult();
            if(doc != null && doc.exists())
            {
              Timber.tag("Campus Info").w((String) doc.get("Name"));
              Timber.tag("User").w(hostelOnlineUser.toString());
              @SuppressWarnings("ConstantConditions") double[] campusLocation = {doc.get("Lat") != null ? (double)doc.get("Lat") : 0.347596f, doc.get("Lng") != null ? (double)doc.get("Lng") : 32.582520};
              Intent sendHostelsListIntent = new Intent(getApplicationContext(), HostelsList.class);
              sendHostelsListIntent.putExtra("HostelOnlineUser", (Parcelable) hostelOnlineUser);
              sendHostelsListIntent.putExtra("CampusLocation", campusLocation);
              startActivity(sendHostelsListIntent);
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