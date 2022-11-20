package com.ass2.i190426_i190435;

public class User {
    int id;
    String name, email, password, gender,phone,  status, lastSeen, deviceId, dp;




    public User() {
    }

    public User(int id, String name, String email, String password, String gender, String phone, String status, String lastSeen, String deviceId, String dp) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.status = status;
        this.lastSeen = lastSeen;
        this.deviceId = deviceId;
        this.dp = dp;
    }

    public User(String name, String email, String password, String gender, String phone, String status, String lastSeen, String deviceId, String dp) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.status = status;
        this.lastSeen = lastSeen;
        this.deviceId = deviceId;
        this.dp = dp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }
}
