package com.hostel_online.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomGridAdapter extends RecyclerView.Adapter<RoomGridAdapter.HostelViewHolder>
{
  String levelLabel;
  int numberOfRooms;
  public RoomGridAdapter(String levelLabel, int numberOfRooms)
  {
    this.levelLabel = levelLabel;
    this.numberOfRooms = numberOfRooms;
  }

  public static class HostelViewHolder extends RecyclerView.ViewHolder
  {
    TextView tvUpdateLevelGridButtonText;
    public HostelViewHolder(View skeletonView)
    {
      super(skeletonView);
      tvUpdateLevelGridButtonText = skeletonView.findViewById(R.id.update_level_grid_button_text);
    }
    TextView getHostelNameTextView()
    {
      return tvUpdateLevelGridButtonText;
    }
  }

  @Override
  public @NonNull HostelViewHolder onCreateViewHolder(ViewGroup parentView, int viewType)
  {
    View skeletonView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.update_level_grid_button_layout, parentView, false);
    return new HostelViewHolder(skeletonView);
  }

  @Override
  public int getItemCount()
  {
    return numberOfRooms;
  }

  public void onBindViewHolder(HostelViewHolder skeletonViewHolder, final int position)
  {
    String roomNumber = (position + 1) < 10 ? ("0" + (position + 1)) : String.valueOf(position + 1);
    skeletonViewHolder.getHostelNameTextView().setText(roomNumber);
    if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType") != null)
    {
      switch((String)EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType"))
      {
        case "Single":
        {
          skeletonViewHolder.getHostelNameTextView().setBackgroundResource(R.drawable.red_button_background);
          skeletonViewHolder.getHostelNameTextView().setTextColor(0xFFFFFFFF);
        }
          break;
        case "Double":
        {
          skeletonViewHolder.getHostelNameTextView().setBackgroundResource(R.drawable.buttonbackground);
          skeletonViewHolder.getHostelNameTextView().setTextColor(0xFFFFFFFF);
        }
          break;
        case "Tripple":
        {
          skeletonViewHolder.getHostelNameTextView().setBackgroundResource(R.drawable.green_button_background);
          skeletonViewHolder.getHostelNameTextView().setTextColor(0xFFFFFFFF);
        }
          break;
        default:
        {
          skeletonViewHolder.getHostelNameTextView().setBackgroundResource(R.drawable.field_background);
          skeletonViewHolder.getHostelNameTextView().setTextColor(0xFF707070);
        }
        break;
      }
    }
    skeletonViewHolder.getHostelNameTextView().setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        switch(EditOrAddHostel.currentRoomType)
        {
          case "Single":
          {
            if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType") != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType").equals("Single"))
            {
              v.setBackgroundResource(R.drawable.field_background);
              ((TextView)v).setTextColor(0xFF707070);
              EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).remove("RoomType");
            }else{
              if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null)
              {
                EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).put("RoomType", "Single");
                v.setBackgroundResource(R.drawable.red_button_background);
                ((TextView)v).setTextColor(0xFFFFFFFF);
              }
            }
          }
          break;
          case "Double":
          {
            if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType") != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType").equals("Double"))
            {
              v.setBackgroundResource(R.drawable.field_background);
              ((TextView)v).setTextColor(0xFF707070);
              EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).remove("RoomType");
            }else{
              if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null)
              {
                EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).put("RoomType", "Double");
                v.setBackgroundResource(R.drawable.buttonbackground);
                ((TextView)v).setTextColor(0xFFFFFFFF);
              }
            }
          }
          break;
          case "Tripple":
          {
            if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType") != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).get("RoomType").equals("Tripple"))
            {
              v.setBackgroundResource(R.drawable.field_background);
              ((TextView)v).setTextColor(0xFF707070);
              EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).remove("RoomType");
            }else{
              if(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber) != null)
              {
                EditOrAddHostel.hostelRooms.get(levelLabel + " - " + roomNumber).put("RoomType", "Tripple");
                v.setBackgroundResource(R.drawable.green_button_background);
                ((TextView)v).setTextColor(0xFFFFFFFF);
              }
            }
          }
          break;
          default:
          {

          }
        }
      }
    });
  }

}