package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class HostelsList extends AppCompatActivity implements LocationListener
{
  private int position = 0;
  private String roomType = "Any", courseMate = "Any", filterPrice = "Any", filterFreedom = "Any", filterComfort = "Any";
  private MapboxDirections client;
  private Point origin, destination, userCurrentLocation;
  private MapView hostelMapView;
  private DirectionsRoute currentRoute;
  private ArrayList<Map<String, Object>> hostels = new ArrayList<>();
  private TextView tvHostelName;
  private ImageView ivHostelImage;
  private RatingBar rbHostelRating;
  private ConstraintLayout clHostelDetailsContainer;
  private final String ROUTE_SOURCE_ID = "route-source-id";
  private final String ICON_SOURCE_ID = "icon-source-id";
  private final String USER_ICON_SOURCE_ID = "user-icon-source-id";
  private HostelOnlineUser hostelOnlineUser;
  private double[] campusLocation;
  public final static int RC_FILTER_CONTROLS_ROOM_TYPE = 60;
  public final static int RC_FILTER_CONTROLS_COURSE_MATE = 70;
  public final static int RC_FILTER_CONTROLS_FILTER_PRICE = 80;
  public final static int RC_FILTER_CONTROLS_FILTER_FREEDOM = 90;
  public final static int RC_FILTER_CONTROLS_FILTER_COMFORT = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Mapbox.getInstance(this, getResources().getString(R.string.mapbox_access_token));
    setContentView(R.layout.activity_hostels_list);
    Intent receiveHostelsListIntent = getIntent();
    hostelOnlineUser = (HostelOnlineUser)receiveHostelsListIntent.getParcelableExtra("HostelOnlineUser");
    campusLocation = receiveHostelsListIntent.getDoubleArrayExtra("CampusLocation");
    if(hostelOnlineUser == null)
    {
      Intent sendSignInIntent = new Intent(HostelsList.this, MainActivity.class);
      startActivity(sendSignInIntent);
    }
    tvHostelName = findViewById(R.id.hostel_name);
    ivHostelImage = findViewById(R.id.hostel_dp);
    ImageView ivHostelArrowRight = findViewById(R.id.hostel_arrow_right);
    ImageView ivHostelArrowLeft = findViewById(R.id.hostel_arrow_left);
    rbHostelRating = findViewById(R.id.hostel_rating);
    Button btnHostelDetails = findViewById(R.id.hostel_details_button);
    hostelMapView = findViewById(R.id.hostel_map_view);
    hostelMapView.onCreate(savedInstanceState);
    BottomNavigationView bnvFilterIcon = findViewById(R.id.filter_icons);
    clHostelDetailsContainer = findViewById(R.id.hostel_details_container);
    tvHostelName.setOnClickListener(this::hideListDetails);
    bnvFilterIcon.setOnNavigationItemSelectedListener(item -> {
      switch(item.getItemId())
      {
        case R.id.room_type:
        {
          Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
          sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_ROOM_TYPE);
          sendDialogIntent.putExtra("Filter", "RoomType");
          sendDialogIntent.putExtra("RoomType", roomType);
          startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_ROOM_TYPE);
        }
          break;
        case R.id.course_mate:
        {
          Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
          sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_COURSE_MATE);
          sendDialogIntent.putExtra("Filter", "CourseMate");
          sendDialogIntent.putExtra("CourseMate", courseMate);
          startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_COURSE_MATE);
        }
          break;
        case R.id.filter_price:
        {
          Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
          sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_FILTER_PRICE);
          sendDialogIntent.putExtra("Filter", "FilterPrice");
          sendDialogIntent.putExtra("RoomType", roomType);
          sendDialogIntent.putExtra("FilterPrice", filterPrice);
          startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_FILTER_PRICE);
        }
          break;
        case R.id.filter_freedom:
        {
          Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
          sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_FILTER_FREEDOM);
          sendDialogIntent.putExtra("Filter", "FilterFreedom");
          sendDialogIntent.putExtra("RoomType", roomType);
          sendDialogIntent.putExtra("FilterFreedom", filterFreedom);
          startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_FILTER_FREEDOM);
        }
          break;
        case R.id.filter_comfort:
        {
          Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
          sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_FILTER_COMFORT);
          sendDialogIntent.putExtra("Filter", "FilterComfort");
          sendDialogIntent.putExtra("FilterComfort", filterComfort);
          startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_FILTER_COMFORT);
        }
          break;
      }
      return true;
    });
    fillHostelList();
    hostelMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
      if(origin == null)
        origin = userCurrentLocation;
      if(destination == null)
        destination = Point.fromLngLat(32.736019, 0.368610);
      getRoute(mapboxMap, origin, destination);
      initSource(style);
      initLayer(style);
    }));

    ivHostelArrowLeft.setOnClickListener(v -> {
      position = Math.max(position - 1, 0);
      updateHostelDetails(position);
    });

    ivHostelArrowRight.setOnClickListener(v -> {
      position = Math.min(position + 1, hostels.size() - 1);
      updateHostelDetails(position);
    });

    btnHostelDetails.setOnClickListener(v -> {
      Intent intentHostelDetailsSend = new Intent(getApplicationContext(), HostelDetailsAndRoomList.class);
      intentHostelDetailsSend.putExtra("HostelOnlineUser", hostelOnlineUser);
      intentHostelDetailsSend.putExtra("HostelId", (String)hostels.get(position).get("Id"));
      //noinspection unchecked
      intentHostelDetailsSend.putStringArrayListExtra("SuggestedRooms", (ArrayList<String>)hostels.get(position).get("SuggestedRooms"));
      startActivity(intentHostelDetailsSend);
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if((requestCode == RC_FILTER_CONTROLS_ROOM_TYPE || requestCode == RC_FILTER_CONTROLS_COURSE_MATE || requestCode == RC_FILTER_CONTROLS_FILTER_COMFORT || requestCode == RC_FILTER_CONTROLS_FILTER_FREEDOM || requestCode == RC_FILTER_CONTROLS_FILTER_PRICE) && resultCode == RESULT_OK)
    {
      roomType = data.getStringExtra("RoomType") == null ? "Any" : data.getStringExtra("RoomType");
      courseMate = data.getStringExtra("CourseMate") == null ? "Any"  : data.getStringExtra("CourseMate");
      filterPrice = data.getStringExtra("FilterPrice") == null ? "Any" : data.getStringExtra("FilterPrice");
      filterFreedom = data.getStringExtra("FilterFreedom") == null ? "Any" : data.getStringExtra("FilterFreedom");
      filterComfort = data.getStringExtra("FilterComfort") == null ? "Any" : data.getStringExtra("FilterComfort");
      fillHostelList();
    }
  }

  private void initSource(@NonNull Style loadedMapStyle)
  {
    loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
    GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
    loadedMapStyle.addSource(iconGeoJsonSource);
    GeoJsonSource userIconGeoJsonSource = new GeoJsonSource(USER_ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
    Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude()))}));
    loadedMapStyle.addSource(userIconGeoJsonSource);
  }

  private void initLayer(@NonNull Style loadedMapStyle)
  {
    String ROUTE_LAYER_ID = "route-layer-id";
    LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
    routeLayer.setProperties(PropertyFactory.lineCap(Property.LINE_CAP_ROUND), PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND), PropertyFactory.lineWidth(5f), PropertyFactory.lineColor(Color.parseColor("#008888")));
    loadedMapStyle.addLayer(routeLayer);
    String RED_PIN_ICON_ID = "red-pin-icon-id";
    loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.red_marker, null)));
    String ICON_LAYER_ID = "icon-layer-id";
    loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(iconImage(RED_PIN_ICON_ID), iconIgnorePlacement(true), iconAllowOverlap(false), iconOffset(new Float[]{0.0f, -9.0f})));
    String RED_USER_ICON_ID = "red-user-icon-id";
    loadedMapStyle.addImage(RED_USER_ICON_ID, Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.user_red, null)));
    String USER_ICON_LAYER_ID = "user-icon-layer-id";
    loadedMapStyle.addLayer(new SymbolLayer(USER_ICON_LAYER_ID, USER_ICON_SOURCE_ID).withProperties(iconImage(RED_USER_ICON_ID), iconIgnorePlacement(true), iconAllowOverlap(false), iconOffset(new Float[]{0.0f, -9.0f})));
  }

  private void getRoute(MapboxMap mapboxMap, Point origin, Point destination)
  {
    client = MapboxDirections.builder().origin(origin).destination(destination).overview(DirectionsCriteria.OVERVIEW_FULL).profile(DirectionsCriteria.PROFILE_DRIVING).accessToken(getResources().getString(R.string.mapbox_access_token)).build();
    client.enqueueCall(new Callback<DirectionsResponse>() {
      @EverythingIsNonNull
      @Override public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        if (response.body() == null) {
          Timber.e("No routes found, make sure you set the right user and access token.");
          return;
        } else if (response.body().routes().size() < 1) {
          Timber.e("No routes found");
          return;
        }
        currentRoute = response.body().routes().get(0);
        if(mapboxMap != null)
        {
          mapboxMap.getStyle(style -> {
            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
            if(source != null)
            {
              LineString lineString = LineString.fromPolyline(Objects.requireNonNull(currentRoute.geometry()), PRECISION_6);
              source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
              mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(origin.latitude(), origin.longitude())).build());
            }
          });
        }
      }
      @EverythingIsNonNull
      @Override public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        Timber.e("Error: %s", throwable.getMessage());
      }
    });
  }

  public void updateHostelDetails(int position)
  {
    getCurrentLocation();
    if(hostels.size() == 0)
    {
      ivHostelImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.empty, null));
      tvHostelName.setText(getResources().getString(R.string.no_available_options));
      rbHostelRating.setRating(0);
    }else{
      tvHostelName.setText((String)hostels.get(position).get("Name"));
      rbHostelRating.setIsIndicator(true);
      //noinspection ConstantConditions
      rbHostelRating.setRating((float)(hostels.get(position).get("Rating") == null ? 0f : hostels.get(position).get("Rating")));
      if(hostels.get(position).get("Image") != null)
        GlideApp.with(getApplicationContext()).load(hostels.get(position).get("Image")).into(ivHostelImage);
      else
        ivHostelImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.empty, null));
      @SuppressWarnings("unchecked") Map<String, Object> location = (Map<String, Object>)hostels.get(position).get("Location");
      if(location != null)
      {
        //noinspection ConstantConditions
        destination = Point.fromLngLat((double)location.get("Lng"), (double)location.get("Lat"));
      }
      if(hostelMapView != null)
      hostelMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
        if(origin == null)
          origin = userCurrentLocation;
        if(destination == null)
          destination = Point.fromLngLat(32.736019, 0.368610);
        initSource(style);
        initLayer(style);
        getRoute(mapboxMap, origin, destination);
      }));
    }
  }

  public void getCurrentLocation()
  {
    if(ContextCompat.checkSelfPermission(HostelsList.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    {
      LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HostelsList.this);
      if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
      {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 3);
      }
      Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if(loc != null)
      {
        userCurrentLocation = Point.fromLngLat(loc.getLongitude(), loc.getLatitude());
      }else{
        userCurrentLocation = Point.fromLngLat(32.5825, 0.3476);
      }
    }else{
      ActivityCompat.requestPermissions(HostelsList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2000);
    }
  }

  public void fillHostelList()
  {
    getCurrentLocation();
    if(userCurrentLocation != null)
    {
      origin = userCurrentLocation;
    }else{
      if(campusLocation != null)
        origin = Point.fromLngLat(campusLocation[0], campusLocation[1]);
      else
        origin = Point.fromLngLat(32.5825, 0.3476);
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("Hostels").get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
      if(task.isSuccessful() && task.getResult() != null)
      {
        hostels.clear();
        for(QueryDocumentSnapshot doc : task.getResult())
        {
          Map<String, Object> tempHostel = new HashMap<>();
          ArrayList<String> suggestedRooms = new ArrayList<>();
          @SuppressWarnings("unchecked") Map<String, Object> hostelLocation = (Map<String, Object>)doc.getData().get("Location");
          if(hostelLocation != null && campusLocation != null && hostelLocation.get("Lat") != null && hostelLocation.get("Lng") != null)
          {
            double fromLat = campusLocation[0];
            double fromLng = campusLocation[1];
            double toLat = (double)hostelLocation.get("Lat");
            double toLng = (double)hostelLocation.get("Lat");
            double R = 6371000;
            double theta1 = fromLat * Math.PI/180;
            double theta2 = toLat * Math.PI/180;
            double changeInTheta = (toLat - fromLat) * Math.PI/180;
            double changeInLambda = (toLng - fromLng) * Math.PI/180;
            double a = Math.sin(changeInTheta / 2) * Math.sin(changeInTheta / 2) + Math.cos(theta1) * Math.cos(theta2) * Math.sin(changeInLambda / 2) * Math.sin(changeInLambda / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c;
            Log.w("Distance", String.valueOf(d));
            if(d > 1000)
              continue;
          }else
            continue;
          tempHostel.put("Name", doc.getData().get("Name"));
          tempHostel.put("Location", doc.getData().get("Location"));
          tempHostel.put("Id", doc.getId());
          tempHostel.put("Rating", doc.getData().get("Rating"));
          tempHostel.put("Image", doc.getData().get("Image"));
          @SuppressWarnings("ConstantConditions") float ratingTotal = doc.getData().get("Rating") == null ? 0f : (float)doc.getData().get("Rating");
          @SuppressWarnings("ConstantConditions") float ratingPrice = doc.getData().get("PriceRating") == null ? 0f : (float)doc.getData().get("PriceRating");
          @SuppressWarnings("ConstantConditions") float ratingFreedom = doc.getData().get("FreedomRating") == null ? 0f : (float)doc.getData().get("FreedomRating");
          @SuppressWarnings("ConstantConditions") float ratingComfort = doc.getData().get("ComfortRating") == null ? 0f : (float)doc.getData().get("ComfortRating");
          tempHostel.put("Points", getPoints(filterPrice, filterFreedom, filterComfort, ratingTotal, ratingPrice, ratingFreedom, ratingComfort));
          //noinspection unchecked
          Map<String, Map<String, Object>> hostelRooms = (Map<String, Map<String, Object>>)doc.getData().get("Rooms");
          //noinspection unchecked
          Map<String, Map<String, Object>> hostelLevels = (Map<String, Map<String, Object>>)(doc.getData().get("Levels"));
          int i = 1;
          String levelNumber = "Level " + i;
          while(hostelLevels != null && hostelLevels.get(levelNumber) != null && hostelRooms != null)
          {
            @SuppressWarnings("ConstantConditions") String levelLabel = (String)hostelLevels.get(levelNumber).get("Label");
            if(levelLabel == null) {
              break;
            }
            String roomLabel = levelLabel + "-01";
            int j = 1;
            while(hostelRooms.get(roomLabel) != null)
            {
              @SuppressWarnings({"unchecked", "ConstantConditions"}) ArrayList<Map<String, Object>> students = (ArrayList<Map<String, Object>>)hostelRooms.get(roomLabel).get("Students");
              if(roomType.equals("Any") && courseMate.equals("Any")) {
                suggestedRooms.add(roomLabel);
              }else if(!roomType.equals("Any") && courseMate.equals(("Any"))){
                //noinspection ConstantConditions
                if(hostelRooms.get(roomLabel).get("RoomType") != null && hostelRooms.get(roomLabel).get("RoomType").equals(roomType))
                {
                  suggestedRooms.add(roomLabel);
                }
              }else if(roomType.equals("Any")) {
                if(students != null) {
                  for(int k = 0; k < students.size(); k++) {
                    //noinspection ConstantConditions
                    if(students.get(i).get("Course") != null && students.get(k).get("Course").equals(hostelOnlineUser.getUserCampus())) {
                      suggestedRooms.add(roomLabel);
                    }
                  }
                }
              }else {
                if(students != null) {
                  for(int k = 0; k < students.size(); k++) {
                    //noinspection ConstantConditions
                    if(students.get(k).get("Course") != null && students.get(k).get("Course").equals(hostelOnlineUser.getUserCampus()) && hostelRooms.get(roomLabel).get("RoomType") != null && hostelRooms.get(roomLabel).get("RoomType").equals(roomType)) {
                      suggestedRooms.add(roomLabel);
                    }
                  }
                }
              }
              j++;
              roomLabel = levelLabel + "-" + (j < 10 ? "0" + j : String.valueOf(j));
            }
            levelNumber = "Level " + (i++);
          }
          if(suggestedRooms.size() > 0)
          {
            tempHostel.put("SuggestedRooms", suggestedRooms);
            hostels.add(tempHostel);
          }
        }
        Collections.sort(hostels, new HostelComparator());
        updateHostelDetails(0);
      }
    });
  }

  int getPoints(String filterPrice, String filterFreedom, String filterComfort, float ratingTotal, float ratingPrice, float ratingFreedom, float ratingComfort)
  {
    if(!filterPrice.equals("Any") && filterPrice.equals("High"))
    {
      return (100 - (int)((ratingPrice/5.0) * 100));
    }else if(!filterPrice.equals("Any") && filterPrice.equals("Low")){
      return (int)((ratingPrice/5.0) * 100);
    }

    if(!filterFreedom.equals("Any") && filterFreedom.equals("High"))
    {
      return (100 - (int)((ratingFreedom/5.0) * 100));
    }else if(!filterFreedom.equals("Any") && filterFreedom.equals("Low")){
      return (int)((ratingFreedom/5.0) * 100);
    }

    if(!filterComfort.equals("Any") && filterComfort.equals("High"))
    {
      return (100 - (int)((ratingComfort/5.0) * 100));
    }else if(!filterComfort.equals("Any") && filterComfort.equals("Low")){
      return (int)((ratingComfort/5.0) * 100);
    }

    return (100 - (int)((ratingTotal/5.0) * 100));
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == 1){
      if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(HostelsList.this, "Permission Granted", Toast.LENGTH_SHORT).show();
      }else{
        Toast.makeText(HostelsList.this, "Permission Denied", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  @Override
  public void onLocationChanged(Location location)
  {

  }

  @Override
  public void onResume()
  {
    super.onResume();
    hostelMapView.onResume();
  }

  @Override
  protected void onStart()
  {
    super.onStart();
    hostelMapView.onStart();
  }

  @Override
  protected void onStop()
  {
    super.onStop();
    hostelMapView.onStop();
  }

  @Override
  public void onPause()
  {
    super.onPause();
    hostelMapView.onPause();
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState)
  {
    super.onSaveInstanceState(outState);
    hostelMapView.onResume();
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    if(client != null)
      client.cancelCall();
    hostelMapView.onDestroy();
  }

  @Override
  public void onLowMemory()
  {
    super.onLowMemory();
    hostelMapView.onLowMemory();
  }

  public void hideListDetails(View view)
  {
    if(clHostelDetailsContainer.getVisibility() == View.VISIBLE)
    {
      clHostelDetailsContainer.setVisibility(View.GONE);
    }else{
      clHostelDetailsContainer.setVisibility(View.VISIBLE);
    }
  }

}