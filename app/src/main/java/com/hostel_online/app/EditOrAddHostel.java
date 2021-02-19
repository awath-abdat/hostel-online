package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
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
  private Map<String, Object> hostelLocation = new HashMap<>();
  private String singleRoomPrice;
  private String singleRoomBookingFee;
  private String doubleRoomPrice;
  private String doubleRoomBookingFee;
  private String trippleRoomPrice;
  private String trippleRoomBookingFee;
  private String singleRoomImagePath;
  private String doubleRoomImagePath;
  private String trippleRoomImagePath;
  private String hostelImagePath;
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
  private ConstraintLayout clUpdateSingleRoomContainer;
  private ConstraintLayout clUpdateDoubleRoomContainer;
  private ConstraintLayout clUpdateTrippleRoomContainer;
  private static class UploadImage extends AsyncTask<String, Long, String>
  {
    OnFinishListener onFinishListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String docId, field;
    private final WeakReference<Context> contextRef;
    public UploadImage(Context context, String docId, String field)
    {
      this.contextRef = new WeakReference<>(context);
      this.docId = docId;
      this.field = field;
    }
    @Override
    protected String doInBackground(String... url)
    {
      Context context = contextRef.get();
      Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloude_name", "dynyjan4a", "api_key", context.getResources().getString(R.string.cloudinary_key), "api_secret", context.getResources().getString(R.string.cloudinary_secret)));
      try{
        Map uploadResult = cloudinary.uploader().upload(url[0], ObjectUtils.asMap("cloud_name", "dynyjan4a", "folder", "HostelImages", "public_id", docId + "_" + field));
        return (String)uploadResult.get("secure_url");
      }catch(Exception e){
        Timber.tag("Failed to Upload: ").w("Failed to upload a hostel image: %s", e.toString());
        return "Failed";
      }
    }

    @Override
    public void onPostExecute(String result)
    {
      if(!result.equals("Failed)"))
      {
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put(field, result);
        db.collection("Hostels").document(docId).update(tempMap);
        if(onFinishListener != null)
          onFinishListener.onFinish();
      }
    }

    public void setOnFinishListener(OnFinishListener n)
    {
      this.onFinishListener = n;
    }
  }
  public static String currentRoomType = "Single";
  public static Map<String, Map<String, Object>> hostelLevels = new HashMap<>();
  public static Map<String, Map<String, Object>> hostelRooms = new HashMap<>();
  public final static int RC_UPDATE_HOSTEL_IMAGE = 10;
  public final static int RC_UPDATE_SINGLE_ROOM_IMAGE = 20;
  public final static int RC_UPDATE_DOUBLE_ROOM_IMAGE = 30;
  public final static int RC_UPDATE_TRIPPLE_ROOM_IMAGE = 40;
  public final static int RC_UPDATE_CAPTURE_LOCATION = 50;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    String[] genders = {"Mixed", "Single Girls", "Single Boys"};
    Map<String, Object> tempLevel = new HashMap<>();
    tempLevel.put("Label", "Level 1");
    tempLevel.put("NumberOfRooms", 2);
    hostelLevels.put("Level 1", tempLevel);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_or_add_hostel);
    Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
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
    clUpdateSingleRoomContainer = findViewById(R.id.update_single_room_container);
    clUpdateDoubleRoomContainer = findViewById(R.id.update_double_room_container);
    clUpdateTrippleRoomContainer = findViewById(R.id.update_tripple_room_container);
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
        clUpdateSingleRoomContainer.setVisibility(View.VISIBLE);
        clUpdateDoubleRoomContainer.setVisibility(View.GONE);
        clUpdateTrippleRoomContainer.setVisibility(View.GONE);
        btnUpdateDoubleRoomButton.setTextColor(0xFF707070);
        btnUpdateTrippleRoomButton.setTextColor(0xFF707070);
        ((Button)v).setTextColor(0xFF008888);
      }
    });
    btnUpdateDoubleRoomButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        currentRoomType = "Double";
        clUpdateSingleRoomContainer.setVisibility(View.GONE);
        clUpdateDoubleRoomContainer.setVisibility(View.VISIBLE);
        clUpdateTrippleRoomContainer.setVisibility(View.GONE);
        btnUpdateSingleRoomButton.setTextColor(0xFF707070);
        btnUpdateTrippleRoomButton.setTextColor(0xFF707070);
        ((Button)v).setTextColor(0xFF008888);
      }
    });
    btnUpdateTrippleRoomButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        currentRoomType = "Tripple";
        clUpdateSingleRoomContainer.setVisibility(View.GONE);
        clUpdateDoubleRoomContainer.setVisibility(View.GONE);
        clUpdateTrippleRoomContainer.setVisibility(View.VISIBLE);
        btnUpdateDoubleRoomButton.setTextColor(0xFF707070);
        btnUpdateSingleRoomButton.setTextColor(0xFF707070);
        ((Button)v).setTextColor(0xFF008888);
      }
    });

    btnAddHostelImage.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
          Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(pickPhoto, RC_UPDATE_HOSTEL_IMAGE);
        }else{
          ActivityCompat.requestPermissions(EditOrAddHostel.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RC_UPDATE_HOSTEL_IMAGE);
        }
      }
    });

    btnAddSingleRoomImage.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
          Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(pickPhoto, RC_UPDATE_SINGLE_ROOM_IMAGE);
        }else{
          ActivityCompat.requestPermissions(EditOrAddHostel.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RC_UPDATE_SINGLE_ROOM_IMAGE);
        }
      }
    });

    btnAddDoubleRoomImage.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
          Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(pickPhoto, RC_UPDATE_DOUBLE_ROOM_IMAGE);
        }else{
          ActivityCompat.requestPermissions(EditOrAddHostel.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RC_UPDATE_DOUBLE_ROOM_IMAGE);
        }
      }
    });

    btnAddTrippleRoomImage.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
          Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(pickPhoto, RC_UPDATE_TRIPPLE_ROOM_IMAGE);
        }else{
          ActivityCompat.requestPermissions(EditOrAddHostel.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RC_UPDATE_TRIPPLE_ROOM_IMAGE);
        }
      }
    });

    btnCaptureLocation.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        getPlacePickerActivity();
      }
    });

    btnUpdateAddEditButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df = db.collection("Hostels").document();
        hostelName = etHostelName.getText().toString();
        hostelDescription =  etHostelDescription.getText().toString();
        hostelGender = spinnerGender.getSelectedItem().toString();
        singleRoomPrice = etSingleRoomPriceFee.getText().toString();
        singleRoomBookingFee = etSingleRoomBookingFee.getText().toString();
        doubleRoomPrice = etDoubleRoomPriceFee.getText().toString();
        doubleRoomBookingFee = etDoubleRoomBookingFee.getText().toString();
        trippleRoomPrice = etTrippleRoomBookingFee.getText().toString();
        trippleRoomBookingFee = etTrippleRoomBookingFee.getText().toString();
        Map<String, Object> hostelData = new HashMap<>();
        hostelData.put("Name", hostelName);
        hostelData.put("Description", hostelDescription);
        hostelData.put("Gender", hostelGender);
        hostelData.put("Levels", hostelLevels);
        hostelData.put("Rooms", hostelRooms);
        hostelData.put("SingleRoomPrice", singleRoomPrice);
        hostelData.put("SingleRoomBookingFee", singleRoomBookingFee);
        hostelData.put("DoubleRoomPrice", doubleRoomPrice);
        hostelData.put("DoubleRoomBookingFee", doubleRoomBookingFee);
        hostelData.put("TrippleRoomPrice", trippleRoomPrice);
        hostelData.put("TrippleRoomBookingFee", trippleRoomBookingFee);
        hostelData.put("Location", hostelLocation);
        df.set(hostelData).addOnSuccessListener(new OnSuccessListener<Void>()
        {
          @Override
          public void onSuccess(Void aVoid)
          {
            hostelId = df.getId();
            UploadImage uploadHostelImage = new UploadImage(getApplicationContext(), hostelId, "Image");
            uploadHostelImage.execute(hostelImagePath);
            UploadImage uploadSingleRoomImage = new UploadImage(getApplicationContext(), hostelId, "SingleRoomImage");
            uploadSingleRoomImage.execute(singleRoomImagePath);
            UploadImage uploadDoubleRoomImage = new UploadImage(getApplicationContext(), hostelId, "DoubleRoomImage");
            uploadDoubleRoomImage.execute(doubleRoomImagePath);
            UploadImage uploadTrippleRoomImage = new UploadImage(getApplicationContext(), hostelId, "TrippleRoomImage");
            uploadTrippleRoomImage.setOnFinishListener(new OnFinishListener(){
              @Override
              public void onFinish()
              {
                Intent i = new Intent(getApplicationContext(), User.class);
                startActivity(i);
              }
            });
            uploadTrippleRoomImage.execute(trippleRoomImagePath);
          }
        });
      }
    });

  }

  public void getPlacePickerActivity()
  {
    startActivityForResult(new PlacePicker.IntentBuilder()
    .accessToken(getString(R.string.mapbox_access_token))
    .placeOptions(PlacePickerOptions.builder()
    .statingCameraPosition(new CameraPosition.Builder()
    .target(new LatLng(0.3476, 32.5825)).zoom(8).build())
    .build()).build(this), RC_UPDATE_CAPTURE_LOCATION);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
  {
    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    if(requestCode != RC_UPDATE_CAPTURE_LOCATION)
    {
      if(resultCode == RESULT_OK)
      {
        Uri imageUrl = imageReturnedIntent.getData();
        String filepath;
        Cursor cursor = getApplicationContext().getContentResolver().query(imageUrl, null, null, null, null);
        if(cursor == null)
        {
          filepath = imageUrl.getPath();
        }else{
          cursor.moveToFirst();
          int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
          filepath = cursor.getString(idx);
          cursor.close();
        }
        switch(requestCode)
        {
          case RC_UPDATE_HOSTEL_IMAGE:
          {
            hostelImagePath = filepath;
            GlideApp.with(getApplicationContext()).load(filepath).into(ivHostelImage);
          }
          break;
          case RC_UPDATE_SINGLE_ROOM_IMAGE:
          {
            singleRoomImagePath = filepath;
            GlideApp.with(getApplicationContext()).load(filepath).into(ivSingleRoomImage);
          }
          break;
          case RC_UPDATE_DOUBLE_ROOM_IMAGE:
          {
            doubleRoomImagePath = filepath;
            GlideApp.with(getApplicationContext()).load(filepath).into(ivDoubleRoomImage);
          }
          break;
          case RC_UPDATE_TRIPPLE_ROOM_IMAGE:
          {
            trippleRoomImagePath = filepath;
            GlideApp.with(getApplicationContext()).load(filepath).into(ivTrippleRoomImage);
          }
          break;
          default:
            break;
        }
      }
    }else{
      if(resultCode == RESULT_OK)
      {
        CarmenFeature feature = PlaceAutocomplete.getPlace(imageReturnedIntent);
        Timber.tag("HostelLocation").w("Works through here.");
        Timber.tag("HostelLocation").w(feature.toString());
        if(feature.geometry() != null)
        {
          hostelLocation.put("Lat", ((Point)feature.geometry()).latitude());
          hostelLocation.put("Lng", ((Point)feature.geometry()).longitude());
          Timber.tag("HostelLocation").w(hostelLocation.toString());
        }
      }else{
        Timber.tag("Failed:").w("OnResult Activity did not return RESULT_OK");
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == 1){
      if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, requestCode);
      }else{
        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
      }
    }
  }
}