package com.example.campuscourier.shared;

import java.io.Serializable;

public class Users implements Serializable {

    private String email, telegram;
    private int points;

    public Users() {
    }

    public Users(String email, String telegram, int points) {
        this.email = email;
        this.telegram = telegram;
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
