package com.example.AttendanceDiary;

public class UserInfo {
String name,standard,enroll,college;

    public UserInfo() {
    }

    public UserInfo(String name, String standard, String enroll, String college) {
        this.name = name;
        this.standard = standard;
        this.enroll = enroll;
        this.college = college;
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
}
