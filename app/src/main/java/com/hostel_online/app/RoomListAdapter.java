package com.hostel_online.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>
{
  ArrayList<Room> rooms;
  private WeakReference<Context> context;
  public RoomListAdapter(Context context, ArrayList<Room> rooms)
  {
    this.rooms = rooms;
    this.context = new WeakReference<>(context);
  }

  public static class RoomViewHolder extends RecyclerView.ViewHolder
  {
    final private TextView tvRoomLabel;
    final private TextView tvRoomType;
    final private TextView tvPrice;
    final private TextView tvBookingFee;
    final private Button btnBooking;
    final private ImageView ivRoomImage;
    public RoomViewHolder(View skeletonView)
    {
      super(skeletonView);
      tvRoomLabel = (TextView) skeletonView.findViewById(R.id.room_label);
      tvRoomType = (TextView) skeletonView.findViewById(R.id.room_type);
      tvPrice = (TextView) skeletonView.findViewById(R.id.price);
      tvBookingFee = (TextView) skeletonView.findViewById(R.id.booking_fee);
      ivRoomImage = (ImageView) skeletonView.findViewById(R.id.room_image);
      btnBooking = (Button) skeletonView.findViewById(R.id.book_button);
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
    Button getBtnBooking()
    {
      return btnBooking;
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
    return rooms.size();
  }

  public void onBindViewHolder(RoomViewHolder skeletonViewHolder, final int position)
  {
    skeletonViewHolder.getRoomLabelTextView().setText(rooms.get(position).getRoomLabel());
    skeletonViewHolder.getRoomTypeTextView().setText(rooms.get(position).getRoomType());
    skeletonViewHolder.getPriceTextView().setText(String.valueOf(rooms.get(position).getPrice()));
    skeletonViewHolder.getBookingFeeTextView().setText(String.valueOf(rooms.get(position).getBookingFee()));
    skeletonViewHolder.getBtnBooking().setOnClickListener(v -> {
      Intent sendDialogIntent = new Intent((Activity)context.get(), Dialog.class);
      sendDialogIntent.putExtra("RequestCode", HostelDetailsAndRoomList.RC_MAKE_BOOKING_PAYMENT);
      sendDialogIntent.putExtra("RoomLabel", rooms.get(position).getRoomLabel());
      ((Activity)context.get()).startActivityForResult(sendDialogIntent, HostelDetailsAndRoomList.RC_MAKE_BOOKING_PAYMENT);
    });
    if(rooms.get(position).getImage() != null)
      GlideApp.with(context.get()).load(rooms.get(position).getImage()).into(skeletonViewHolder.getRoomImageView());
    else
      GlideApp.with(context.get()).load(ResourcesCompat.getDrawable(context.get().getResources(), R.mipmap.empty, null)).into(skeletonViewHolder.getRoomImageView());
  }
}
