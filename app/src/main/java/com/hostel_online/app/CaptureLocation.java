package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
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

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class CaptureLocation extends AppCompatActivity
{
  private MapboxDirections client;
  private Point origin;
  private Point destination;
  private MapView hostelMapView;
  private DirectionsRoute currentRoute;
  private ImageView hoveringMarker;
  private final String ROUTE_SOURCE_ID = "route-source-id";
  private final String ROUTE_LAYER_ID = "route-layer-id";
  private final String ICON_SOURCE_ID = "icon-source-id";
  private final String RED_PIN_ICON_ID = "red-pin-icon-id";
  private final String ICON_LAYER_ID = "icon-layer-id";
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_capture_location);
    Mapbox.getInstance(this, getResources().getString(R.string.mapbox_access_token));
    setContentView(R.layout.activity_hostels_list);
    hostelMapView = findViewById(R.id.hostel_map_view);
    hostelMapView.onCreate(savedInstanceState);
    hostelMapView.getMapAsync(new OnMapReadyCallback(){
      @Override
      public void onMapReady(@NonNull final MapboxMap mapboxMap)
      {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded(){
          @Override
          public void onStyleLoaded(@NonNull Style style)
          {
            origin = Point.fromLngLat(32.7414629, 0.3636528);
            destination = Point.fromLngLat(32.736019, 0.368610);
            initSource(style);
            initLayer(style);
            hoveringMarker = new ImageView(getApplicationContext());
            hoveringMarker.setImageResource(R.drawable.red_marker);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            hoveringMarker.setLayoutParams(params);
            hostelMapView.addView(hoveringMarker);
          }
        });
      }
    });
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
    loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(iconImage(RED_PIN_ICON_ID), iconIgnorePlacement(true), iconAllowOverlap(true), iconOffset(new Float[]{0f, 0f})));
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

}