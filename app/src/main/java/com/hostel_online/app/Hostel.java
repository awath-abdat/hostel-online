package com.hostel_online.app;

public class Hostel
{
  private String name;
  private String description;
  private float rating;
  private float price_rating;
  private float comfort_rating;
  private float freedom_rating;
  private int hostel_dp;

  Hostel(String name, float rating, float price_rating, float comfort_rating, float freedom_rating, int hostel_dp)
  {
    this.name = name;
    this.rating = rating;
    this.price_rating = price_rating;
    this.comfort_rating = comfort_rating;
    this.freedom_rating = freedom_rating;
    this.hostel_dp = hostel_dp;
  }

  Hostel(String constructorName, float price_rating, float comfort_rating, float freedom_rating, int hostel_dp, String description)
  {
    this.name = constructorName;
    this.rating = (price_rating + comfort_rating + freedom_rating) / 3;
    this.price_rating = price_rating;
    this.comfort_rating = comfort_rating;
    this.freedom_rating = freedom_rating;
    this.hostel_dp = hostel_dp;
    this.description = description;
  }

  public String getName()
  {
    return this.name;
  }

  public float getRating()
  {
    return this.rating;
  }

  public float getPriceRating()
  {
    return this.price_rating;
  }

  public float getComfortRating()
  {
    return this.comfort_rating;
  }

  public float getFreedomRating()
  {
    return this.freedom_rating;
  }

  public int getHostelDp()
  {
    return this.hostel_dp;
  }

  /*Functions for setting the private variables of this Hostel Classs*/
  public void setName(String pName)
  {
    this.name = pName;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setRating(float pRating)
  {
    this.rating = pRating;
  }

  public void setPriceRating(float pPriceRating)
  {
    this.price_rating = pPriceRating;
  }

  public void setComfortRating(float pComfortRating)
  {
    this.comfort_rating = pComfortRating;
  }

  public void setFreedomRating(float pFreedomRating)
  {
    this.freedom_rating = pFreedomRating;
  }

  public void setHostelDp(int pHostelDp)
  {
    this.hostel_dp = pHostelDp;
  }
}
