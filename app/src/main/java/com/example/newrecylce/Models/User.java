package com.example.newrecylce.Models;

import android.content.Context;
import com.example.newrecylce.Room.DbHelper;

public class User  {


private String subjectName;
int total,id;
int present,absent,percentage;
DbHelper db;
Context context;

    public User(String subjectName,int total,int present,int absent,int percentage) {
        db = new DbHelper(context);
        this.subjectName = subjectName;
        this.present = present;
        this.absent = absent;
        this.total = total;
        this.percentage = percentage;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

}
