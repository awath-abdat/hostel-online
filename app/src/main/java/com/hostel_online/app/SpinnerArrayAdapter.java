package com.hostel_online.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class SpinnerArrayAdapter extends ArrayAdapter<String>
{
  final private String[] objects;
  public SpinnerArrayAdapter(Context context, int textViewResourceId, String[] objects)
  {
    super(context, textViewResourceId, objects);
    this.objects = objects;
  }
  @Override
  public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
  {
    return getCustomView(position, convertView, parent);
  }

  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent)
  {
    return getCustomView(position, convertView, parent);
  }

  public View getCustomView(int position, View convertView, @NonNull ViewGroup parent)
  {
    View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_layout, parent, false);
    final TextView rowText = row.findViewById(R.id.gender_spinner_text);
    rowText.setText(objects[position]);
    return row;
  }
}
