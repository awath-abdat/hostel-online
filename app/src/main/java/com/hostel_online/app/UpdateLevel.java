package com.hostel_online.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class UpdateLevel extends Fragment
{
  private final Map<String, Object> level;
  private final int position;
  public UpdateLevel(int position)
  {
    this.position = position + 1;
    if(EditOrAddHostel.hostelLevels != null && (EditOrAddHostel.hostelLevels.get("Level " + (position + 1))) != null)
      this.level = EditOrAddHostel.hostelLevels.get("Level " + (position + 1));
    else
    {
      this.level = new HashMap<>();
      this.level.put("NumberOfRooms", 2);
      this.level.put("Label", "Level 1");
    }
  }

  public static UpdateLevel newInstance()
  {
    return new UpdateLevel(1);
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View fragmentView = inflater.inflate(R.layout.fragment_update_level, container, false);
    RecyclerView updateLevelGridView = fragmentView.findViewById(R.id.update_level_grid_view);
    EditText noRoomsOnLevel = fragmentView.findViewById(R.id.update_no_rooms_on_level);
    EditText roomsLabel = fragmentView.findViewById(R.id.update_level_prefix);
    roomsLabel.addTextChangedListener(new TextWatcher()
    {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String previousLabel = (String)level.get("Label");
        String label = "Level " + position;
        if(roomsLabel.getText().toString().length() > 0)
          label = roomsLabel.getText().toString().toUpperCase();
        level.put("Label", label);
        int i = 1;
        String roomLabelToChange = previousLabel + "-01";
        while(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(roomLabelToChange) != null)
        {
          EditOrAddHostel.hostelRooms.remove(roomLabelToChange);
          i++;
          roomLabelToChange = previousLabel + "-" + (i < 10 ? ("0".concat(String.valueOf(i))) : String.valueOf(i));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {}
    });
    noRoomsOnLevel.addTextChangedListener(new TextWatcher(){
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      public void afterTextChanged(Editable text){}
      public void onTextChanged(CharSequence s, int start, int count, int after)
      {
        int n;
        if(s.toString().length() > 0)
          n = Integer.parseInt(s.toString());
        else
          n = 2;
        level.put("NumberOfRooms", n);
        String levelLabel = (String)level.get("Label");
        if(levelLabel != null)
        {
          int i;
          for(i = 1; i <= n; i++) {
            Map<String, Object> rooms = new HashMap<>();
            String roomNumber = (i < 10 ? ("0".concat(String.valueOf(i))) : String.valueOf(i));
            rooms.put("RoomNumber", roomNumber);
            rooms.put("LevelLabel", levelLabel);
            EditOrAddHostel.hostelRooms.put(levelLabel.concat("-").concat(roomNumber), rooms);
          }
          String excessRoomLabel = levelLabel + "-" + (i < 10 ? ("0".concat(String.valueOf(i))) : String.valueOf(i));
          while(EditOrAddHostel.hostelRooms != null && EditOrAddHostel.hostelRooms.get(excessRoomLabel) != null)
          {
            EditOrAddHostel.hostelRooms.remove(excessRoomLabel);
            i++;
            excessRoomLabel = levelLabel + "-" + (i < 10 ? ("0".concat(String.valueOf(i))) : String.valueOf(i));
          }
          updateLevelGridView.setAdapter(new RoomGridAdapter((String)level.get("Label"), n));
        }else{
          Toast.makeText(getActivity(), "Give a prefix for the labels of the rooms on this level.", Toast.LENGTH_LONG).show();
        }
        EditOrAddHostel.hostelLevels.put("Level " + position, level);
        Log.w("This Room", EditOrAddHostel.hostelRooms.toString());
      }
    });
    DisplayMetrics dm = updateLevelGridView.getContext().getResources().getDisplayMetrics();
    float screenWidthDp = dm.widthPixels / dm.density;
    ViewGroup.LayoutParams params = updateLevelGridView.getLayoutParams();
    int numberOfRooms;
    String levelLabel;
    if(EditOrAddHostel.hostelLevels != null && EditOrAddHostel.hostelLevels.get("Level " + position) != null && EditOrAddHostel.hostelLevels.get("Level " + position) != null && EditOrAddHostel.hostelLevels.get("Level " + position).get("NumberOfRooms") != null)
    {
      levelLabel = (String)EditOrAddHostel.hostelLevels.get("Level " + position).get("Label");
      numberOfRooms = (int)EditOrAddHostel.hostelLevels.get("Level " + position).get("NumberOfRooms");
    }else{
      levelLabel = "Level 1";
      numberOfRooms = 2;
    }
    params.height =  (int)((numberOfRooms / (screenWidthDp / 100)) * (75 * dm.density) + 0.5);
    updateLevelGridView.setLayoutParams(params);
    updateLevelGridView.setHasFixedSize(true);
    updateLevelGridView.setLayoutManager(new GridLayoutManager(getContext(), (int)(screenWidthDp / 100 + 0.5)));
    updateLevelGridView.setAdapter(new RoomGridAdapter(levelLabel, numberOfRooms));
    return fragmentView;
  }
}