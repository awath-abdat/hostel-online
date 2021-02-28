package com.hostel_online.app;

import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Notificat  {
        private String hostelId;
        private Date date;
        private String message;
        private String title;

        private Notificat(){

        }
        Notificat(String hostelId, Date date, String title, String message){
            this.hostelId=hostelId;
            this.title=title;
            this.message=message;
            this.date=date;
        }

    public void setHostelId(String hostelId) {
        this.hostelId = hostelId;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(date); }

    public void setDate(Date date) {
            this.date = date;
    }

        public String getHostelId() {
            return hostelId;
        }


        public String getTitle() {
            return title;
        }
        public String getMessage(){
            return message;
        }


}
