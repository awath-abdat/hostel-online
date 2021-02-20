package com.hostel_online.app;

import java.util.Comparator;
import java.util.Map;

public class HostelComparator implements Comparator<Map<String, Object>>
{
  public HostelComparator() { }
  @SuppressWarnings({"all"})
  public int compare(Map<String, Object> first, Map<String, Object> second)
  {
    try{
      Integer firstValue = (int)first.get("Points");
      Integer secondValue = (int)second.get("Points");
      return firstValue.compareTo(secondValue);
    }catch(Exception e){
      return 0;
    }
  }
}