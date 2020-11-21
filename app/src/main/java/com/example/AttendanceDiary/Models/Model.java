package com.example.AttendanceDiary.Models;


public class Model {

    private String date;
    private int status;

    public Model(String date) {
        this.date = date;
    }

    public Model(int status) {
        this.status = status;
    }

    public Model(String date, int status) {
        this.date = date;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}





