package com.hostel_online.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.hostel_online.app.HostelsList.RC_FILTER_CONTROLS;

public class Dialog extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Intent receiveDialogIntent = getIntent();
    int requestCode = Integer.parseInt(receiveDialogIntent.getStringExtra("RequestCode"));
    switch(requestCode)
    {
      case RC_FILTER_CONTROLS:
      {
        setContentView(R.layout.activity_dialog_room_type);
        TextView tvSingleRoomButton = findViewById(R.id.dialog_single_room_button);
        TextView tvDoubleRoomButton = findViewById(R.id.dialog_double_room_button);
        TextView tvTrippleRoomButton = findViewById(R.id.dialog_tripple_room_button);
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
      }
      break;
    }
  }
}