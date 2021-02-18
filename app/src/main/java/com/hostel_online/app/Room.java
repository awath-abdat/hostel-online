package com.hostel_online.app;

public class Room
{
  private String roomLabel;
  private String roomType;
  private int image;
  private int hostelId;
  private int price;
  private int bookingFee;
  public Room(String roomLabel, String roomType, int image, int hostelId, int price, int bookingFee)
  {
    this.bookingFee = bookingFee;
    this.roomType = roomType;
    this.price = price;
    this.image = image;
    this.bookingFee = bookingFee;
    this.hostelId = hostelId;
    this.roomLabel = roomLabel;
  }
  public int getImage()
  {
    return this.image;
  }
  public int getPrice()
  {
    return this.price;
  }
  public int getBookingFee()
  {
    return this.bookingFee;
  }
  public void setBookingFee(int bookingFee)
  {
    this.bookingFee = bookingFee;
  }
  public int getHostelId()
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
  public void setImage(int image)
  {
    this.image = image;
  }
  public void setPrice(int price)
  {
    this.price = price;
  }
  public void setHostelId(int hostelId)
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
