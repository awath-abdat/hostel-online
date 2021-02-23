package com.hostel_online.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class Dialog extends Activity
{
  public final static int RC_FILTER_CONTROLS_ROOM_TYPE = 60;
  public final static int RC_FILTER_CONTROLS_COURSE_MATE = 70;
  public final static int RC_FILTER_CONTROLS_FILTER_PRICE = 80;
  public final static int RC_FILTER_CONTROLS_FILTER_FREEDOM = 90;
  public final static int RC_FILTER_CONTROLS_FILTER_COMFORT = 100;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Intent receiveDialogIntent = getIntent();
    int requestCode = receiveDialogIntent.getIntExtra("RequestCode", 0);
    switch(requestCode)
    {
      case RC_FILTER_CONTROLS_ROOM_TYPE:
      {
        String currentRoomType = receiveDialogIntent.getStringExtra("RoomType");
        setContentView(R.layout.activity_dialog_room_type);
        TextView tvSingleRoomButton = findViewById(R.id.dialog_single_room_button);
        TextView tvDoubleRoomButton = findViewById(R.id.dialog_double_room_button);
        TextView tvTrippleRoomButton = findViewById(R.id.dialog_tripple_room_button);
        TextView tvAnyRoomButton = findViewById(R.id.dialog_any_room_button);
        if(currentRoomType == null)
          currentRoomType = "Any";
        switch(currentRoomType)
        {
          case "Single":
          {
            tvSingleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvSingleRoomButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvDoubleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvTrippleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvAnyRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
            break;
          case "Double":
          {
            tvSingleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvDoubleRoomButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvDoubleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvTrippleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvAnyRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
            break;
          case "Tripple":
          {
            tvSingleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvDoubleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvTrippleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvTrippleRoomButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvAnyRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
            break;
          default:
          {
            tvSingleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvDoubleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvTrippleRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvAnyRoomButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvAnyRoomButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
          }
            break;
        }
        tvSingleRoomButton.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("RoomType", "Single");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvDoubleRoomButton.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("RoomType", "Double");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvTrippleRoomButton.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("RoomType", "Tripple");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvAnyRoomButton.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("RoomType", "Any");
            setResult(RESULT_OK, data);
            finish();
          }
        });
      }
      break;

      case RC_FILTER_CONTROLS_COURSE_MATE:
      {
        String currentCourseMate = receiveDialogIntent.getStringExtra("CourseMate");
        setContentView(R.layout.activity_dialog_course_mate);
        TextView tvCourseMateYes = findViewById(R.id.dialog_course_mate_yes);
        TextView tvCourseMateNo = findViewById(R.id.dialog_course_mate_no);
        TextView tvCourseMateAny = findViewById(R.id.dialog_course_mate_any);
        if(currentCourseMate == null)
          currentCourseMate = "Any";
        switch(currentCourseMate)
        {
          case "Yes":
          {
            tvCourseMateYes.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvCourseMateYes.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvCourseMateNo.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvCourseMateAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "No":
          {
            tvCourseMateYes.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvCourseMateNo.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvCourseMateNo.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvCourseMateAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Any":
          {
            tvCourseMateYes.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvCourseMateNo.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvCourseMateAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvCourseMateAny.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
          }
          break;
        }
        tvCourseMateYes.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("CourseMate", "Yes");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvCourseMateNo.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("CourseMate", "No");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvCourseMateAny.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("CourseMate", "Any");
            setResult(RESULT_OK, data);
            finish();
          }
        });
      }
      break;

      case RC_FILTER_CONTROLS_FILTER_PRICE:
      {
        String currentFilterPrice = receiveDialogIntent.getStringExtra("FilterPrice");
        setContentView(R.layout.activity_dialog_filter_price);
        TextView tvFilterPriceHigh = findViewById(R.id.dialog_filter_price_high);
        TextView tvFilterPriceLow = findViewById(R.id.dialog_filter_price_low);
        TextView tvFilterPriceAny = findViewById(R.id.dialog_filter_price_any);
        if(currentFilterPrice == null)
          currentFilterPrice = "Any";
        switch(currentFilterPrice)
        {
          case "High":
          {
            tvFilterPriceHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterPriceHigh.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvFilterPriceLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterPriceAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Low":
          {
            tvFilterPriceHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterPriceLow.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvFilterPriceLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterPriceAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Any":
          {
            tvFilterPriceHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterPriceLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterPriceAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterPriceAny.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
          }
          break;
        }
        tvFilterPriceHigh.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterPrice", "High");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvFilterPriceLow.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterPrice", "Low");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvFilterPriceAny.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterPrice", "Any");
            setResult(RESULT_OK, data);
            finish();
          }
        });
      }
      break;

      case RC_FILTER_CONTROLS_FILTER_FREEDOM:
      {
        String currentFilterFreedom = receiveDialogIntent.getStringExtra("FilterFreedom");
        setContentView(R.layout.activity_dialog_filter_freedom);
        TextView tvFilterFreedomHigh = findViewById(R.id.dialog_filter_freedom_high);
        TextView tvFilterFreedomLow = findViewById(R.id.dialog_filter_freedom_low);
        TextView tvFilterFreedomAny = findViewById(R.id.dialog_filter_freedom_any);
        if(currentFilterFreedom == null)
          currentFilterFreedom = "Any";
        switch(currentFilterFreedom)
        {
          case "High":
          {
            tvFilterFreedomHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterFreedomHigh.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvFilterFreedomLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterFreedomAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Low":
          {
            tvFilterFreedomHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterFreedomLow.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvFilterFreedomLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterFreedomAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Any":
          {
            tvFilterFreedomHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterFreedomLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterFreedomAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterFreedomAny.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
          }
          break;
        }
        tvFilterFreedomHigh.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterFreedom", "High");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvFilterFreedomLow.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterFreedom", "Low");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvFilterFreedomAny.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterFreedom", "Any");
            setResult(RESULT_OK, data);
            finish();
          }
        });
      }
      break;

      case RC_FILTER_CONTROLS_FILTER_COMFORT:
      {
        String currentFilterComfort = receiveDialogIntent.getStringExtra("FilterComfort");
        setContentView(R.layout.activity_dialog_filter_comfort);
        TextView tvFilterComfortHigh = findViewById(R.id.dialog_filter_comfort_high);
        TextView tvFilterComfortLow = findViewById(R.id.dialog_filter_comfort_low);
        TextView tvFilterComfortAny = findViewById(R.id.dialog_filter_comfort_any);
        if(currentFilterComfort == null)
          currentFilterComfort = "Any";
        switch(currentFilterComfort)
        {
          case "High":
          {
            tvFilterComfortHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterComfortHigh.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvFilterComfortLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterComfortAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Low":
          {
            tvFilterComfortHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterComfortLow.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            tvFilterComfortLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterComfortAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
          }
          break;
          case "Any":
          {
            tvFilterComfortHigh.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterComfortLow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));
            tvFilterComfortAny.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.uicyan, null));
            tvFilterComfortAny.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
          }
          break;
        }
        tvFilterComfortHigh.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterComfort", "High");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvFilterComfortLow.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterComfort", "Low");
            setResult(RESULT_OK, data);
            finish();
          }
        });
        tvFilterComfortAny.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            Intent data = new Intent();
            data.putExtra("FilterComfort", "Any");
            setResult(RESULT_OK, data);
            finish();
          }
        });
      }
      break;

    }
  }
}