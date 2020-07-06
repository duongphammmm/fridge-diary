package com.example.fireapp;

import java.util.Date;

public class Item {

    public String name, amount, user_id;
    public Date expiry, timestamp;

    public Item() {}

    public Item(String name, Date expiry, String user_id, Date timestamp) {
        this.name = name;
        this.amount = amount;
        this.expiry = expiry;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
