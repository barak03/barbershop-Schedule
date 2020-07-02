package com.example.finalprojectandroid2.model;

import java.util.ArrayList;

public class day
{
    public int day;
    public int month;
    public int year;
    public ArrayList<event> eventsList = new ArrayList<event>();

    public void setDay(int day) { this.day = day; }

    public void setMonth(int month) { this.month = month; }

    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
}
