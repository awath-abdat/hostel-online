package com.hostel_online.app;

import android.os.Parcel;
import android.os.Parcelable;

public class HostelOnlineUser implements Parcelable
{
  private String userId;
  private String userFirstName;
  private String userLastName;
  private String userEmail;
  private String userPhoneNumber;
  private String userCourse;
  private String userGender;
  private String userHostelId;
  private String userRoomLabel;
  private String userCampus;
  private String userRole;
  private String userPhotoUrl;

  public HostelOnlineUser() { }

  public HostelOnlineUser(Parcel in)
  {
    String[] data = new String[12];
    in.readStringArray(data);
    userId = data[0];
    userPhotoUrl = data[1];
    userFirstName = data[2];
    userLastName = data[3];
    userEmail = data[4];
    userPhoneNumber = data[5];
    userCourse = data[6];
    userGender = data[7];
    userCampus = data[8];
    userHostelId = data[9];
    userRoomLabel = data[10];
    userRole = data[11];
  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    String[] data = {userId, userPhotoUrl, userFirstName, userLastName, userEmail, userPhoneNumber, userCourse, userGender, userCampus, userHostelId, userRoomLabel, userRole};
    out.writeStringArray(data);
  }

  public static final Parcelable.Creator<HostelOnlineUser> CREATOR = new Parcelable.Creator<HostelOnlineUser>() {
    public HostelOnlineUser createFromParcel(Parcel in) {
      return new HostelOnlineUser(in);
    }

    public HostelOnlineUser[] newArray(int size) {
      return new HostelOnlineUser[size];
    }
  };

  public void setUserPhotoUrl(String userPhotoUrl)
  {
    this.userPhotoUrl = userPhotoUrl;
  }

  public void setUserRoomLabel(String userRoomLabel)
  {
    this.userRoomLabel = userRoomLabel;
  }

  public void setUserCampus(String userCampus)
  {
    this.userCampus = userCampus;
  }

  public void setUserCourse(String userCourse)
  {
    this.userCourse = userCourse;
  }

  public void setUserHostelId(String userHostelId)
  {
    this.userHostelId = userHostelId;
  }

  public void setUserGender(String userGender)
  {
    this.userGender = userGender;
  }

  public void setUserPhoneNumber(String userPhoneNumber)
  {
    this.userPhoneNumber = userPhoneNumber;
  }

  public void setUserRole(String userRole)
  {
    this.userRole = userRole;
  }

  public void setUserEmail(String userEmail)
  {
    this.userEmail = userEmail;
  }

  public void setUserLastName(String userLastName)
  {
    this.userLastName = userLastName;
  }

  public void setUserFirstName(String userFirstName)
  {
    this.userFirstName = userFirstName;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getUserPhotoUrl()
  {
    return this.userPhotoUrl;
  }

  public String getUserRoomLabel()
  {
    return this.userRoomLabel;
  }

  public String getUserDisplayName()
  {
    return this.userFirstName + " " + this.getUserLastName();
  }

  public String getUserCampus()
  {
    return this.userCampus;
  }

  public String getUserCourse()
  {
    return this.userCourse;
  }

  public String getUserHostelId()
  {
    return this.userHostelId;
  }

  public String getUserEmail()
  {
    return this.userEmail;
  }

  public String getUserGender()
  {
    return this.userGender;
  }

  public String getUserPhoneNumber()
  {
    return this.userPhoneNumber;
  }

  public String getUserRole()
  {
    return this.userRole;
  }

  public String setUserEmail()
  {
    return this.userEmail;
  }

  public String getUserLastName()
  {
    return this.userLastName;
  }

  public String getUserFirstName()
  {
    return this.userFirstName;
  }

  public String getUserId()
  {
    return this.userId;
  }

}
