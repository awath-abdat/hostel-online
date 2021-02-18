package com.hostel_online.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HostelDetailsAndRoomList extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_hostel_details_and_room_list);
    Room[] rooms = {new Room("Room A1", "Single", R.mipmap.rooma, 1, 1500000, 500000), new Room("Room A2", "Double", R.mipmap.roomb, 1, 1200000, 500000)};
    RecyclerView roomsList = findViewById(R.id.rooms_list);
    roomsList.setAdapter(new RoomListAdapter(rooms));
  }

  public void hideListDetails(View view)
  {
    View detailsParentView = ((View)view.getParent()).findViewById(R.id.room_details);
    if(detailsParentView.getVisibility() == View.VISIBLE)
    {
      detailsParentView.setVisibility(View.GONE);
      ((ImageView)view).setImageResource(R.drawable.arrow_down);
    }else{
      detailsParentView.setVisibility(View.VISIBLE);
      ((ImageView)view).setImageResource(R.drawable.arrow_up);
    }
  }
}