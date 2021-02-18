package com.hostel_online.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HostelListAdapter extends RecyclerView.Adapter<HostelListAdapter.HostelViewHolder>
{
  Hostel[] hostels;
  public HostelListAdapter(Hostel[] hostels)
  {
    this.hostels = hostels;
  }

  public static class HostelViewHolder extends RecyclerView.ViewHolder
  {
    TextView tvHostelName;
    RatingBar rbRating;
    RatingBar rbPriceRating;
    RatingBar rbFreedomRating;
    RatingBar rbComfortRating;
    ImageView ivHostelDp;
    public HostelViewHolder(View skeletonView)
    {
      super(skeletonView);
      tvHostelName = (TextView) skeletonView.findViewById(R.id.hostel_name);
      rbRating = (RatingBar) skeletonView.findViewById(R.id.rating);
      rbPriceRating = (RatingBar) skeletonView.findViewById(R.id.price_rating);
      rbFreedomRating = (RatingBar) skeletonView.findViewById(R.id.freedom_rating);
      rbComfortRating = (RatingBar) skeletonView.findViewById(R.id.comfort_rating);
      ivHostelDp = (ImageView) skeletonView.findViewById(R.id.hostel_dp);
    }
    TextView getHostelNameTextView()
    {
      return tvHostelName;
    }
    RatingBar getRatingBar()
    {
      return rbRating;
    }
    RatingBar getPriceRatingBar(){
      return rbPriceRating;
    }
    RatingBar getFreedomRatingBar()
    {
      return rbFreedomRating;
    }
    RatingBar getComfortRatingBar()
    {
      return rbComfortRating;
    }
    ImageView getHostelDpImageView()
    {
      return ivHostelDp;
    }
  }

  @Override
  public @NonNull HostelViewHolder onCreateViewHolder(ViewGroup parentView, int viewType)
  {
    View skeletonView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.hostel_record, parentView, false);
    return new HostelViewHolder(skeletonView);
  }

  @Override
  public int getItemCount()
  {
    return hostels.length;
  }

  public void onBindViewHolder(HostelViewHolder skeletonViewHolder, final int position)
  {
    skeletonViewHolder.getHostelNameTextView().setText(hostels[position].getName());
    skeletonViewHolder.getRatingBar().setRating(hostels[position].getRating());
    skeletonViewHolder.getPriceRatingBar().setRating(hostels[position].getPriceRating());
    skeletonViewHolder.getComfortRatingBar().setRating(hostels[position].getComfortRating());
    skeletonViewHolder.getFreedomRatingBar().setRating(hostels[position].getFreedomRating());
    skeletonViewHolder.getHostelDpImageView().setImageResource(hostels[position].getHostelDp());
  }

}