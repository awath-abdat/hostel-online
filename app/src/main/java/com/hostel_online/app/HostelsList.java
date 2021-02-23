package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
  private Point origin, destination, from;
  private MapView hostelMapView;
  private DirectionsRoute currentRoute;
  private BottomNavigationView bnvFilterIcon;
  private ArrayList<Map<String, Object>> hostels = new ArrayList<>();
  private Map<String, Map<String, Object>> hostelRooms = new HashMap<>();
  private Map<String, Map<String, Object>> hostelLevels = new HashMap<>();
  private TextView tvHostelName;
  private ImageView ivHostelImage;
  private ImageView ivHostelArrowRight;
  private ImageView ivHostelArrowLeft;
  private RatingBar rbHostelRating;
  private Button btnHostelDetails;
  private ConstraintLayout clHostelDetailsContainer;
  private final String ROUTE_SOURCE_ID = "route-source-id";
  private final String ROUTE_LAYER_ID = "route-layer-id";
  private final String ICON_SOURCE_ID = "icon-source-id";
  private final String RED_PIN_ICON_ID = "red-pin-icon-id";
  private final String ICON_LAYER_ID = "icon-layer-id";
  private HostelOnlineUser hostelOnlineUser;
  private double[] campusLocation;
  private LocationManager locationManager;
  public final static int RC_FILTER_CONTROLS_ROOM_TYPE = 60;
  public final static int RC_FILTER_CONTROLS_COURSE_MATE = 70;
  public final static int RC_FILTER_CONTROLS_FILTER_PRICE = 80;
  public final static int RC_FILTER_CONTROLS_FILTER_FREEDOM = 90;
  public final static int RC_FILTER_CONTROLS_FILTER_COMFORT = 100;
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if((requestCode == RC_FILTER_CONTROLS_ROOM_TYPE || requestCode == RC_FILTER_CONTROLS_COURSE_MATE || requestCode == RC_FILTER_CONTROLS_FILTER_COMFORT || requestCode == RC_FILTER_CONTROLS_FILTER_FREEDOM || requestCode == RC_FILTER_CONTROLS_FILTER_PRICE) && resultCode == RESULT_OK)
    {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      roomType = data.getStringExtra("RoomType") == null ? "Any" : data.getStringExtra("RoomType");
      courseMate = data.getStringExtra("CourseMate") == null ? "Any"  : data.getStringExtra("CourseMate");
      filterPrice = data.getStringExtra("FilterPrice") == null ? "Any" : data.getStringExtra("FilterPrice");
      filterFreedom = data.getStringExtra("FilterFreedom") == null ? "Any" : data.getStringExtra("FilterFreedom");
      filterComfort = data.getStringExtra("FilterComfort") == null ? "Any" : data.getStringExtra("FilterComfort");
      db.collection("Hostels").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
      {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task)
        {
          if(task.isSuccessful())
          {
            hostels.clear();
            for(QueryDocumentSnapshot doc : task.getResult())
            {
              Map<String, Object> tempHostel = new HashMap<>();
              Map<String, Object> hostelLocation = (Map<String, Object>) doc.getData().get("Location");
              Log.w("Hostel Location", hostelLocation.toString());
              if(hostelLocation != null && campusLocation != null && hostelLocation.get("Lat") != null && hostelLocation.get("Lng") != null)
              {
                double fromLat = campusLocation[0];
                double fromLng = campusLocation[1];
                double toLat = (double)hostelLocation.get("Lat");
                double toLng = (double)hostelLocation.get("Lng");
                double R = 6371000;
                double theta1 = fromLat * Math.PI/180;
                double theta2 = toLat * Math.PI/180;
                double changeInTheta = (toLat - fromLat) * Math.PI/180;
                double changeInLambda = (toLng - fromLng) * Math.PI/180;
                double a = Math.sin(changeInTheta / 2) * Math.sin(changeInTheta / 2) + Math.cos(theta1) * Math.cos(theta2) * Math.sin(changeInLambda / 2) * Math.sin(changeInLambda / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double d = R * c;
                if(d > 2000)
                  continue;
              }else{
                continue;
              }
              tempHostel.put("Name", doc.getData().get("Name"));
              tempHostel.put("Location", doc.getData().get("Location"));
              tempHostel.put("Id", doc.getId());
              tempHostel.put("Rating", doc.getData().get("Rating"));
              tempHostel.put("Image", doc.getData().get("Image"));
              float ratingTotal = doc.getData().get("Rating") == null ? 0f : (float)doc.getData().get("Rating");
              float ratingPrice = doc.getData().get("PriceRating") == null ? 0f : (float)doc.getData().get("PriceRating");;
              float ratingFreedom = doc.getData().get("FreedomRating") == null ? 0f : (float)doc.getData().get("FreedomRating");;
              float ratingComfort = doc.getData().get("ComfortRating") == null ? 0f : (float)doc.getData().get("ComfortRating");;
              tempHostel.put("Points", getPoints(filterPrice, filterFreedom, filterComfort, ratingTotal, ratingPrice, ratingFreedom, ratingComfort));
              hostelRooms = (Map<String, Map<String, Object>>)doc.getData().get("Rooms");
              hostelLevels = (Map<String, Map<String, Object>>)(doc.getData().get("Levels"));
              int i = 1;
              String levelNumber = "Level " + i;
              while(hostelLevels != null && hostelLevels.get(levelNumber) != null && hostelRooms != null)
              {
                String levelLabel = (String)hostelLevels.get(levelNumber).get("Label");
                if(levelLabel == null) {
                  break;
                }
                String roomLabel = levelLabel + "-01";
                int added = 0, j = 1;
                while(hostelRooms.get(roomLabel) != null)
                {
                  if(roomType.equals("Any") || hostelRooms.get(roomLabel).get("RoomType").equals(roomType))
                  {
                    ArrayList<Map<String, Object>> students = (ArrayList<Map<String, Object>>)hostelRooms.get(roomLabel).get("Students");
                    if(hostelRooms.get(roomLabel).get("RoomType").equals("Single") || courseMate.equals("Any") || students == null)
                    {
                      tempHostel.put("SuggestedRoom", roomLabel);
                      hostels.add(tempHostel);
                      added = 1;
                      break;
                    }else{
                      if(courseMate.equals("Yes") && students.get(0).get("Course").equals(hostelOnlineUser.getUserCourse()))
                      {
                        tempHostel.put("SuggestedRoom", roomLabel);
                        hostels.add(tempHostel);
                        added = 1;
                        break;
                      }else if(!students.get(0).get("Course").equals(hostelOnlineUser.getUserCourse())){
                        tempHostel.put("SuggestedRoom", roomLabel);
                        hostels.add(tempHostel);
                        added = 1;
                        break;
                      }
                    }
                  }
                  j++;
                  roomLabel = levelLabel + "-" + (j < 10 ? "0" + String.valueOf(j) : String.valueOf(j));
                }
                if(added == 1)
                  break;
                else
                  levelNumber = "Level " + (i++);
              }
            }
            Collections.sort(hostels, new HostelComparator());
            updateHostelDetails(0);
          }
        }
      });
    }
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
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if(ContextCompat.checkSelfPermission(HostelsList.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    {
      locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HostelsList.this);
      Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      origin = Point.fromLngLat(32.5825, 0.3476);
      Log.w("Origin Set", origin.toString());
    }else{
      ActivityCompat.requestPermissions(HostelsList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
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
    ivHostelArrowRight = findViewById(R.id.hostel_arrow_right);
    ivHostelArrowLeft = findViewById(R.id.hostel_arrow_left);
    rbHostelRating = findViewById(R.id.hostel_rating);
    btnHostelDetails = findViewById(R.id.hostel_details_button);
    hostelMapView = findViewById(R.id.hostel_map_view);
    hostelMapView.onCreate(savedInstanceState);
    bnvFilterIcon = findViewById(R.id.filter_icons);
    clHostelDetailsContainer = findViewById(R.id.hostel_details_container);
    tvHostelName.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        hideListDetails(v);
      }
    });
    bnvFilterIcon.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
    {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item)
      {
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
            sendDialogIntent.putExtra("RoomType", roomType);
            startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_COURSE_MATE);
          }
            break;
          case R.id.filter_price:
          {
            Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
            sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_FILTER_PRICE);
            sendDialogIntent.putExtra("Filter", "FilterPrice");
            sendDialogIntent.putExtra("RoomType", roomType);
            startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_FILTER_PRICE);
          }
            break;
          case R.id.filter_freedom:
          {
            Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
            sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_FILTER_FREEDOM);
            sendDialogIntent.putExtra("Filter", "FilterFreedom");
            sendDialogIntent.putExtra("RoomType", roomType);
            startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_FILTER_FREEDOM);
          }
            break;
          case R.id.filter_comfort:
          {
            Intent sendDialogIntent = new Intent(getApplicationContext(), Dialog.class);
            sendDialogIntent.putExtra("RequestCode", RC_FILTER_CONTROLS_FILTER_COMFORT);
            sendDialogIntent.putExtra("Filter", "FilterComfort");
            startActivityForResult(sendDialogIntent, RC_FILTER_CONTROLS_FILTER_COMFORT);
          }
            break;
        }
        return true;
      }
    });

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("Hostels").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
    {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task)
      {
        if(task.isSuccessful())
        {
          hostels.clear();
          for(QueryDocumentSnapshot doc : task.getResult())
          {
            Map<String, Object> hostelLocation = (Map<String, Object>) doc.getData().get("Location");
            if(hostelLocation != null && campusLocation != null && hostelLocation.get("Lat") != null && hostelLocation.get("Lng") != null)
            {
              double fromLat = campusLocation[0];
              double fromLng = campusLocation[1];
              double toLat = (double)hostelLocation.get("Lat");
              double toLng = (double)hostelLocation.get("Lng");
              double R = 6371000;
              double theta1 = fromLat * Math.PI/180;
              double theta2 = toLat * Math.PI/180;
              double changeInTheta = (toLat - fromLat) * Math.PI/180;
              double changeInLambda = (toLng - fromLng) * Math.PI/180;
              double a = Math.sin(changeInTheta / 2) * Math.sin(changeInTheta / 2) + Math.cos(theta1) * Math.cos(theta2) * Math.sin(changeInLambda / 2) * Math.sin(changeInLambda / 2);
              double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
              double d = R * c;
              if(d > 2000)
                continue;
              Log.w("Hostel Name", (String)doc.getData().get("Name"));
              Log.w("Distance", String.valueOf(d));
            }else{
              continue;
            }
            Map<String, Object> tempHostel = new HashMap<>();
            tempHostel.put("Name", doc.getData().get("Name"));
            tempHostel.put("Location", doc.getData().get("Location"));
            tempHostel.put("Id", doc.getId());
            tempHostel.put("Rating", doc.getData().get("Rating"));
            tempHostel.put("Image", doc.getData().get("Image"));
            hostels.add(tempHostel);
          }
          Collections.sort(hostels, new HostelComparator());
          updateHostelDetails(0);
        }
      }
    });

    hostelMapView.getMapAsync(new OnMapReadyCallback(){
      @Override
      public void onMapReady(@NonNull final MapboxMap mapboxMap)
      {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded(){
          @Override
          public void onStyleLoaded(@NonNull Style style)
          {
            if(origin == null)
              origin = Point.fromLngLat(32.7414629, 0.3636528);
            if(destination == null)
              destination = Point.fromLngLat(32.736019, 0.368610);
            getRoute(mapboxMap, origin, destination);
            initSource(style);
            initLayer(style);
          }
        });
      }
    });

    ivHostelArrowLeft.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        position = Math.max(position - 1, 0);
        updateHostelDetails(position);
      }
    });

    ivHostelArrowRight.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        position = Math.min(position + 1, hostels.size() - 1);
        updateHostelDetails(position);
      }
    });

    Intent hostelListIntentReceive = getIntent();
  }

  private void initSource(@NonNull Style loadedMapStyle)
  {
    loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
    GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
      Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())), Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))
    }));
    loadedMapStyle.addSource(iconGeoJsonSource);
  }

  private void initLayer(@NonNull Style loadedMapStyle)
  {
    LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
    routeLayer.setProperties(PropertyFactory.lineCap(Property.LINE_CAP_ROUND), PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND), PropertyFactory.lineWidth(5f), PropertyFactory.lineColor(Color.parseColor("#008888")));
    loadedMapStyle.addLayer(routeLayer);
    loadedMapStyle.addImage(RED_PIN_ICON_ID, ResourcesCompat.getDrawable(getResources(), R.drawable.red_marker, getTheme()));
    loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(iconImage(RED_PIN_ICON_ID), iconIgnorePlacement(true), iconAllowOverlap(false), iconOffset(new Float[]{0.0f, -9.0f})));
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
          mapboxMap.getStyle(new Style.OnStyleLoaded(){
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
              GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
              if(source != null)
              {
                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
              }
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
    if(hostels.size() == 0)
    {
      ivHostelImage.setBackground(getResources().getDrawable(R.mipmap.empty));
      tvHostelName.setText(getResources().getString(R.string.no_available_options));
      rbHostelRating.setRating(0);
    }else{
      tvHostelName.setText((String)hostels.get(position).get("Name"));
      rbHostelRating.setIsIndicator(true);
      rbHostelRating.setRating((float)(hostels.get(position).get("Rating") == null ? 0f : hostels.get(position).get("Rating")));
      if(hostels.get(position).get("Image") != null)
        GlideApp.with(getApplicationContext()).load(hostels.get(position).get("Image")).into(ivHostelImage);
      else
        ivHostelImage.setImageDrawable(getResources().getDrawable(R.mipmap.empty));
      Map<String, Object> location = (Map<String, Object>)hostels.get(position).get("Location");
      if(location != null)
      {
        destination = Point.fromLngLat((double)location.get("Lng"), (double)location.get("Lat"));
        Log.w("Destination", destination.toString());
      }
      if(hostelMapView != null)
      hostelMapView.getMapAsync(new OnMapReadyCallback(){
        @Override
        public void onMapReady(@NonNull final MapboxMap mapboxMap)
        {
          mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded(){
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
              if(origin == null)
                origin = Point.fromLngLat(32.7414629, 0.3636528);
              if(destination == null)
                destination = Point.fromLngLat(32.736019, 0.368610);
              initSource(style);
              initLayer(style);
              getRoute(mapboxMap, origin, destination);
            }
          });
        }
      });
    }
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
    Log.w("Hostel Data", hostels.toString());
  }
  public void showHostelDetails(View view)
  {
    Intent intentHostelDetailsSend = new Intent(this, HostelDetailsAndRoomList.class);
    startActivity(intentHostelDetailsSend);
  }
}