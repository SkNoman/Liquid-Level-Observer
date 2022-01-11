package com.example.waterlevel;
public class UserHelperClass {


    String name ;
    String username;
    String email;
    String phone;
    String city;
    String pas;
    String device_token;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String username, String email, String phone, String city, String pas ,String device_token) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.pas = pas;
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPas() {
        return pas;
    }

    public void setPas(String pas) {
        this.pas = pas;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
