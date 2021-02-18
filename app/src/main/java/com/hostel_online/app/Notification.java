package com.hostel_online.app;

public class Notification
{
  private String urgency;
  private String body;
  public Notification(String urgency, String body)
  {
    this.urgency = urgency;
    this.body = body;
  }

  public String getUrgency()
  {
    return this.urgency;
  }

  public String getBody()
  {
    return this.body;
  }
}
