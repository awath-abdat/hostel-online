package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class User extends AppCompatActivity
{

  private HostelOnlineUser hostelOnlineUser;
  @Override
  public void onLowMemory()
  {
    super.onLowMemory();
    Log.w("App Memory", "App going low on memory");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user);
    Intent userIntentReceive = getIntent();
    hostelOnlineUser = (HostelOnlineUser)userIntentReceive.getParcelableExtra("HostelOnlineUser");
    BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, new FragmentNotifications(hostelOnlineUser));
    transaction.addToBackStack(null);
    transaction.commit();
    if(hostelOnlineUser != null && hostelOnlineUser.getUserRole() != null)
    {
      switch(hostelOnlineUser.getUserRole())
      {
        case "Admin":
          bottomAppBar.replaceMenu(R.menu.user_bottom_nav_admin_menu);
          break;
        case "Manager":
          bottomAppBar.replaceMenu(R.menu.user_bottom_nav_manager_menu);
          break;
        default:
          bottomAppBar.replaceMenu(R.menu.user_bottom_nav_student_menu);
          break;
      }
    }else{
      Intent loginIntentSend = new Intent(this, MainActivity.class);
      startActivity(loginIntentSend);
      Timber.tag("userRole").w("Is Null");
    }
    bottomAppBar.setNavigationOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new FragmentNotifications(hostelOnlineUser));
        transaction.addToBackStack(null);
        transaction.commit();
      }
    });
    bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.login_info: {
            Intent UpdateIntent= new Intent(getApplicationContext(), edit_login_info.class);
            UpdateIntent.putExtra("hostelOnlineUser",hostelOnlineUser);
            startActivity(UpdateIntent);
           // FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          //  transaction.replace(R.id.fragment_container, new user_home(hostelOnlineUser));
           // transaction.addToBackStack(null);
          //  item.setChecked(true);
           // transaction.commit();
          }
          break;
          case R.id.menu_profile: {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new user_profile(hostelOnlineUser));
            transaction.addToBackStack(null);
            item.setChecked(true);
            transaction.commit();
          }
          break;
          case R.id.menu_add_hostel:
          {
            Intent sendAddHostelIntent = new Intent(getApplicationContext(), EditOrAddHostel.class);
            sendAddHostelIntent.putExtra("AddOrEdit", "Add");
            startActivity(sendAddHostelIntent);
          }
          break;
          case R.id.notifications:
          {
            Intent sendHostelIdIntent = new Intent(getApplicationContext(), FragmentNotifications.class);
            sendHostelIdIntent.putExtra("hostelOnlineUser", hostelOnlineUser.getUserHostelId());
            startActivity(sendHostelIdIntent);
          }
          case R.id.logout:
          {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent sendMainActivityIntent = new Intent(User.this, MainActivity.class);
            sendMainActivityIntent.putExtra("LogOut", true);
            startActivity(sendMainActivityIntent);
          }
          break;
          case R.id.add_notification:
          {
            DialogNotification dn = new DialogNotification(hostelOnlineUser);
            dn.show(getSupportFragmentManager(), "Notification");
          }
        }
        return false;
      }
    });


  }

  public void chooseIntentForUser()
  {
    if(hostelOnlineUser == null)
    {
      Intent loginIntentSend = new Intent(this, MainActivity.class);
      startActivity(loginIntentSend);
      Timber.tag("userRole").w("Is Null");
    }
    if(hostelOnlineUser.getUserRole() != null && (hostelOnlineUser.getUserRoomLabel() == null || hostelOnlineUser.getUserHostelId() == null || hostelOnlineUser.getUserRoomLabel().length() == 0 || hostelOnlineUser.getUserHostelId().length() == 0))
    {
      if(hostelOnlineUser.getUserRole().equals("Student"))
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

  }







