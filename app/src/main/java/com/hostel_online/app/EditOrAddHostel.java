package com.hostel_online.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditOrAddHostel extends AppCompatActivity
{
  private String hostelId;
  private String action;
  private String hostelName;
  private String hostelDescription;
  private String hostelGender;
  private ImageView ivHostelImage;
  private ImageView ivSingleRoomImage;
  private ImageView ivDoubleRoomImage;
  private ImageView ivTrippleRoomImage;
  private TextView btnUpdateSingleRoomButton;
  private TextView btnUpdateDoubleRoomButton;
  private TextView btnUpdateTrippleRoomButton;
  private EditText etHostelName;
  private EditText etHostelDescription;
  private EditText etUpdateLevelNumber;
  private EditText etSingleRoomBookingFee;
  private EditText etSingleRoomPriceFee;
  private EditText etDoubleRoomBookingFee;
  private EditText etDoubleRoomPriceFee;
  private EditText etTrippleRoomBookingFee;
  private EditText etTrippleRoomPriceFee;
  private Spinner spinnerGender;
  private Button btnCaptureLocation;
  private Button btnUpdateLevelPlus;
  private Button btnUpdateLevelMinus;
  private Button btnUpdateAddEditButton;
  private Button btnUpdateCancelButton;
  private FloatingActionButton btnAddSingleRoomImage;
  private FloatingActionButton btnAddDoubleRoomImage;
  private FloatingActionButton btnAddTrippleRoomImage;
  private FloatingActionButton btnAddHostelImage;

  public Map<String, Object> hostelLocation;
  public static String currentRoomType = "Single";
  public static Map<String, Map<String, Object>> hostelLevels = new HashMap<>();
  public static Map<String, Map<String, Object>> hostelRooms = new HashMap<>();
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    String[] genders = {"Mixed", "Single Girls", "Single Boys"};
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_or_add_hostel);
    ivHostelImage = findViewById(R.id.update_image_view);
    ivSingleRoomImage = findViewById(R.id.update_single_room_image);
    ivDoubleRoomImage = findViewById(R.id.update_double_room_image);
    ivTrippleRoomImage = findViewById(R.id.update_tripple_room_image);
    etHostelName = findViewById(R.id.update_hostel_name);
    etHostelDescription = findViewById(R.id.update_hostel_description);
    etUpdateLevelNumber = findViewById(R.id.update_level_number);
    etSingleRoomBookingFee = findViewById(R.id.update_single_room_booking_fee);
    etSingleRoomPriceFee = findViewById(R.id.update_single_room_price);
    etDoubleRoomBookingFee = findViewById(R.id.update_double_room_booking_fee);
    etDoubleRoomPriceFee = findViewById(R.id.update_double_room_price);
    etTrippleRoomBookingFee = findViewById(R.id.update_tripple_room_booking_fee);
    etTrippleRoomPriceFee = findViewById(R.id.update_tripple_room_price);
    spinnerGender = findViewById(R.id.update_hostel_gender_spinner);
    btnCaptureLocation = findViewById(R.id.update_capture_location);
    btnUpdateLevelPlus = findViewById(R.id.update_levels_plus);
    btnUpdateLevelMinus = findViewById(R.id.update_levels_minus);
    btnUpdateAddEditButton = findViewById(R.id.update_add_edit_button);
    btnUpdateCancelButton = findViewById(R.id.update_cancel_button);
    btnUpdateSingleRoomButton = findViewById(R.id.update_single_room_button);
    btnUpdateDoubleRoomButton = findViewById(R.id.update_double_room_button);
    btnUpdateTrippleRoomButton = findViewById(R.id.update_tripple_room_button);
    btnAddSingleRoomImage = findViewById(R.id.update_floating_update_add_single_room_image);
    btnAddDoubleRoomImage = findViewById(R.id.update_floating_update_add_double_room_image);
    btnAddTrippleRoomImage = findViewById(R.id.update_floating_update_add_tripple_room_image);
    btnAddHostelImage = findViewById(R.id.update_floating_update_add_image);
    Intent receiveEditOrAddHostelIntent = getIntent();
    action = receiveEditOrAddHostelIntent.getStringExtra("EditOrAdd");
    spinnerGender.setAdapter(new SpinnerArrayAdapter(this, R.layout.spinner_layout, genders));
    CustomViewPager updateLevelViewPager = findViewById(R.id.update_level_view_pager);
    TabLayout updateTabLayout = findViewById(R.id.update_level_tab_layout);
    UpdateLevelFragmentPagerAdapter updateLevelFragmentPagerAdapter = new UpdateLevelFragmentPagerAdapter(getSupportFragmentManager());
    updateLevelViewPager.setAdapter(updateLevelFragmentPagerAdapter);
    updateLevelViewPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    updateTabLayout.setupWithViewPager(updateLevelViewPager);
    if(action != null && action.equals("Edit"))
    {
      Log.w("Action", "Edit");
    }
    btnUpdateLevelMinus.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        int levelNumber;
        if(etUpdateLevelNumber.getText() != null && etUpdateLevelNumber.getText().toString().length() > 0)
          levelNumber = Integer.parseInt(etUpdateLevelNumber.getText().toString()) - 1;
        else
          levelNumber = 1;
        if(levelNumber < 1)
          levelNumber = 1;
        etUpdateLevelNumber.setText(String.valueOf(levelNumber));
      }
    });
    etUpdateLevelNumber.addTextChangedListener(new TextWatcher(){
      @Override
      public void afterTextChanged(Editable s){}

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after){}

      @Override
      @SuppressWarnings({"all"})
      public void onTextChanged(CharSequence s, int start, int before, int count)
      {
        int n;
        Map<String, Map<String, Object>> tempLevels = new HashMap<>();
        if(s.toString().length() > 0) {
          n = Integer.parseInt(s.toString());
        }else{
          n = 1;
        }
        if(n < 1)
        {
          etUpdateLevelNumber.setText("1");
          n = 1;
        }
        for(int i = 1; i <= n; i++)
        {
          String levelLabel = "Level " + i;
          Map<String, Object> levelInformation = new HashMap<>();
          levelInformation.put("Label", levelLabel);
          if(hostelLevels != null && hostelLevels.get(levelLabel) != null &&  hostelLevels.get(levelLabel).get("NumberOfRooms") != null && (int)hostelLevels.get(levelLabel).get("NumberOfRooms") > 0)
          {
            levelInformation.put("NumberOfRooms", (int)hostelLevels.get(levelLabel).get("NumberOfRooms"));
          }else{
            levelInformation.put("NumberOfRooms", 2);
          }
          tempLevels.put(levelLabel, levelInformation);
        }
        hostelLevels = tempLevels;
        updateLevelFragmentPagerAdapter.notifyDataSetChanged();
      }
    });
    btnUpdateLevelPlus.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        int levelNumber;
        if(etUpdateLevelNumber.getText() != null && etUpdateLevelNumber.getText().toString().length() > 0)
          levelNumber = Integer.parseInt(etUpdateLevelNumber.getText().toString()) + 1;
        else
          levelNumber = 1;
        if(levelNumber > 9)
          levelNumber = 9;
        etUpdateLevelNumber.setText(String.valueOf(levelNumber));
      }
    });

    btnUpdateLevelMinus.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        int levelNumber;
        if(etUpdateLevelNumber.getText() != null && etUpdateLevelNumber.getText().toString().length() > 0)
          levelNumber = Integer.parseInt(etUpdateLevelNumber.getText().toString()) - 1;
        else
          levelNumber = 1;
        if(levelNumber < 1)
          levelNumber = 1;
        etUpdateLevelNumber.setText(String.valueOf(levelNumber));
      }
    });

    etUpdateLevelNumber.addTextChangedListener(new TextWatcher(){
      @Override
      public void afterTextChanged(Editable s){}

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after){}

      @Override
      @SuppressWarnings({"all"})
      public void onTextChanged(CharSequence s, int start, int before, int count)
      {
        int n;
        Map<String, Map<String, Object>> tempLevels = new HashMap<>();
        if(s.toString().length() > 0) {
          n = Integer.parseInt(s.toString());
        }else{
          n = 1;
        }
        if(n < 1)
        {
          etUpdateLevelNumber.setText("1");
          n = 1;
        }
        for(int i = 1; i <= n; i++)
        {
          String levelLabel = "Level " + i;
          Map<String, Object> levelInformation = new HashMap<>();
          levelInformation.put("Label", levelLabel);
          if(hostelLevels != null && hostelLevels.get(levelLabel) != null &&  hostelLevels.get(levelLabel).get("NumberOfRooms") != null && (int)hostelLevels.get(levelLabel).get("NumberOfRooms") > 0)
          {
            levelInformation.put("NumberOfRooms", (int)hostelLevels.get(levelLabel).get("NumberOfRooms"));
          }else{
            levelInformation.put("NumberOfRooms", 2);
          }
          tempLevels.put(levelLabel, levelInformation);
        }
        hostelLevels = tempLevels;
        updateLevelFragmentPagerAdapter.notifyDataSetChanged();
      }
    });

    btnUpdateSingleRoomButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        currentRoomType = "Single";
        btnUpdateDoubleRoomButton.setTextColor(0xFF707070);
        btnUpdateTrippleRoomButton.setTextColor(0xFF707070);
        ((Button)v).setTextColor(0xFF008888);
      }
    });
    btnUpdateDoubleRoomButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        currentRoomType = "Single";
        btnUpdateSingleRoomButton.setTextColor(0xFF707070);
        btnUpdateTrippleRoomButton.setTextColor(0xFF707070);
        ((Button)v).setTextColor(0xFF008888);
      }
    });
    btnUpdateTrippleRoomButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        currentRoomType = "Single";
        btnUpdateDoubleRoomButton.setTextColor(0xFF707070);
        btnUpdateSingleRoomButton.setTextColor(0xFF707070);
        ((Button)v).setTextColor(0xFF008888);
      }
    });
  }


}