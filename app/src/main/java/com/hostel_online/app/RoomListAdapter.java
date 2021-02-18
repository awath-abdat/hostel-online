package com.hostel_online.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>
{
  Room[] rooms;
  public RoomListAdapter(Room[] rooms)
  {
    this.rooms = rooms;
  }

  public static class RoomViewHolder extends RecyclerView.ViewHolder
  {
    final private TextView tvRoomLabel;
    final private TextView tvRoomType;
    final private TextView tvPrice;
    final private TextView tvBookingFee;
    final private ImageView ivRoomImage;
    public RoomViewHolder(View skeletonView)
    {
      super(skeletonView);
      tvRoomLabel = (TextView) skeletonView.findViewById(R.id.room_label);
      tvRoomType = (TextView) skeletonView.findViewById(R.id.room_type);
      tvPrice = (TextView) skeletonView.findViewById(R.id.price);
      tvBookingFee = (TextView) skeletonView.findViewById(R.id.booking_fee);
      ivRoomImage = (ImageView) skeletonView.findViewById(R.id.room_image);
    }
    TextView getRoomLabelTextView()
    {
      return tvRoomLabel;
    }
    TextView getRoomTypeTextView()
    {
      return tvRoomType;
    }
    TextView getPriceTextView()
    {
      return tvPrice;
    }
    TextView getBookingFeeTextView()
    {
      return tvBookingFee;
    }
    ImageView getRoomImageView()
    {
      return ivRoomImage;
    }
  }

  @Override
  public @NonNull RoomViewHolder onCreateViewHolder(ViewGroup parentView, int viewType)
  {
    View skeletonView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.room_record, parentView, false);
    return new RoomViewHolder(skeletonView);
  }

  @Override
  public int getItemCount()
  {
    return rooms.length;
  }

  public void onBindViewHolder(RoomViewHolder skeletonViewHolder, final int position)
  {
    skeletonViewHolder.getRoomLabelTextView().setText(rooms[position].getRoomLabel());
    skeletonViewHolder.getRoomTypeTextView().setText(rooms[position].getRoomType());
    skeletonViewHolder.getPriceTextView().setText(String.valueOf(rooms[position].getPrice()));
    skeletonViewHolder.getBookingFeeTextView().setText(String.valueOf(rooms[position].getBookingFee()));
    skeletonViewHolder.getRoomImageView().setImageResource(rooms[position].getImage());
  }

}
