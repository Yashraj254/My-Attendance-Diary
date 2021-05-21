package com.myFirstApp1.AttendanceDiary.Models;

public class Home {

    public String name,standard,enroll,college,imageUrl;

    public Home() {
    }

    public Home(String name, String standard, String enroll, String college,String imageUrl) {
        this.name = name;
        this.standard = standard;
        this.enroll = enroll;
        this.college = college;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getEnroll() {
        return enroll;
    }

    public void setEnroll(String enroll) {
        this.enroll = enroll;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
