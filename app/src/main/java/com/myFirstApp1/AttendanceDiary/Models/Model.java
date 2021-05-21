package com.myFirstApp1.AttendanceDiary.Models;


public class Model {

    private String date;
    private String status;
    private String id;

    public Model() {
    }

    public Model(String date) {
        this.date = date;
    }



    public Model(String date, String status, String id) {
        this.date = date;
        this.status = status;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}





