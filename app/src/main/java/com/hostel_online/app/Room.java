package com.hostel_online.app;

public class Room
{
  private String roomLabel;
  private String roomType;
  private String image;
  private String hostelId;
  private String price;
  private String bookingFee;

  public Room()
  {
  }

  public Room(String roomLabel, String roomType, String image, String hostelId, String price, String bookingFee)
  {
    this.bookingFee = bookingFee;
    this.roomType = roomType;
    this.price = price;
    this.image = image;
    this.bookingFee = bookingFee;
    this.hostelId = hostelId;
    this.roomLabel = roomLabel;
  }
  public String getImage()
  {
    return this.image;
  }
  public String getPrice()
  {
    return this.price;
  }
  public String getBookingFee()
  {
    return this.bookingFee;
  }
  public void setBookingFee(String bookingFee)
  {
    this.bookingFee = bookingFee;
  }
  public String getHostelId()
  {
    return this.hostelId;
  }
  public String getRoomLabel()
  {
    return this.roomLabel;
  }
  public String getRoomType()
  {
    return this.roomType;
  }
  public void setImage(String image)
  {
    this.image = image;
  }
  public void setPrice(String price)
  {
    this.price = price;
  }
  public void setHostelId(String hostelId)
  {
    this.hostelId = hostelId;
  }
  public void getRoomLabel(String roomLabel)
  {
    this.roomLabel = roomLabel;
  }
  public void getRoomType(String roomType)
  {
    this.roomType = roomType;
  }
}
