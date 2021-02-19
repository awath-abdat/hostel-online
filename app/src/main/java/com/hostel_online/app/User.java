package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User extends AppCompatActivity
{
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
    BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, new user_home());
    transaction.addToBackStack(null);
    transaction.commit();
    if(MainActivity.hostelOnlineUser != null && MainActivity.hostelOnlineUser.userRole != null)
    {
      switch (MainActivity.hostelOnlineUser.userRole) {
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
    }
    else{
      MainActivity.hostelOnlineUser = new HostelOnlineUser(getApplicationContext());
      MainActivity.hostelOnlineUser.setOnFinishListener(new OnFinishListener(){
        @Override
        public void onFinish()
        {
          chooseIntentForUser();
        }
      });
      Timber.tag("userRole").w("Is Null");
    }
    bottomAppBar.setNavigationOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new user_home());
        transaction.addToBackStack(null);
        transaction.commit();
      }
    });
    bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.edit_profile: {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new user_home());
            transaction.addToBackStack(null);
            item.setChecked(true);
            transaction.commit();
          }
          break;
          case R.id.menu_profile: {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new user_profile());
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
        }
        return false;
      }
    });
  }

  public void chooseIntentForUser()
  {
    if(MainActivity.hostelOnlineUser == null)
      MainActivity.hostelOnlineUser = new HostelOnlineUser(getApplicationContext());
    if(MainActivity.hostelOnlineUser.userRole != null && (MainActivity.hostelOnlineUser.userRoomLabel == null || MainActivity.hostelOnlineUser.userHostelName == null || MainActivity.hostelOnlineUser.userRoomLabel.length() == 0 || MainActivity.hostelOnlineUser.userHostelName.length() == 0))
    {
      if(MainActivity.hostelOnlineUser.userRole.equals("Student"))
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