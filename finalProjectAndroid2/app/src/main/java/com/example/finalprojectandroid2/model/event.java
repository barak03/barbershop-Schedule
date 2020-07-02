package com.example.finalprojectandroid2.model;

public class event
{
    String title;
    String startTime;
    String endTime;
    String availability = "Yes";

    public event(){}

    public event(String title,String startTime, String endTime)
    {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public String getTitle() { return title; }

    public String getStartTime() { return startTime; }

    public String getEndTime() { return endTime; }

    public void setTitle(String title) { this.title = title; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getAvailability() { return availability; }

    public void setAvailability(String availability) { this.availability = availability; }
}
