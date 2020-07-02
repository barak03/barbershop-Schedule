package com.example.finalprojectandroid2.model;

import java.io.Serializable;
import java.util.ArrayList;

class User implements Serializable
{

    public String email;
    public String name;
    public String phoneNumber;

    public User(){}

    public User(String i_Email, String i_Name, String i_PhoneNumber)
    {
        this.email = i_Email;
        this.name = i_Name;
        this.phoneNumber = i_PhoneNumber;
    }

    public String getM_Email() {
        return email;
    }

    public String getM_Name() {
        return name;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

}
