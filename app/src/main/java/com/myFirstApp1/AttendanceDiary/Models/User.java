package com.myFirstApp1.AttendanceDiary.Models;

public class User  {


private String subjectName;
int total;
int present,absent,percentage;


    public User(String subjectName,int total,int present,int absent,int percentage) {
        this.subjectName = subjectName;
        this.present = present;
        this.absent = absent;
        this.total = total;
        this.percentage = percentage;
    }

    public User() {
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
