package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class UpdateLevelFragmentPagerAdapter extends FragmentPagerAdapter
{
  public UpdateLevelFragmentPagerAdapter(@NonNull FragmentManager fm)
  {
    super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
  }

  @NonNull
  @Override
  public Fragment getItem(int position)
  {
    return new UpdateLevel(position);
  }

  public int getCount()
  {
    if(EditOrAddHostel.hostelLevels != null)
      return EditOrAddHostel.hostelLevels.size();
    else
      return 1;
  }

  @Override
  public CharSequence getPageTitle(int position)
  {
    return ("Level " + (position + 1));
  }
}
