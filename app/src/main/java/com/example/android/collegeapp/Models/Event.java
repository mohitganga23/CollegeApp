package com.example.android.collegeapp.Models;

public class Event extends com.example.android.collegeapp.Models.EventId {

    public String desc, title, address, eventOrg,
            date, time, contact_one, contact_two, orgEmail, image_url, user_id;

    public Event() {}

    public Event(String desc, String title, String address, String eventOrg, String date, String time, String contact_one, String contact_two, String orgEmail, String image_url, String user_id) {
        this.desc = desc;
        this.title = title;
        this.address = address;
        this.eventOrg = eventOrg;
        this.date = date;
        this.time = time;
        this.contact_one = contact_one;
        this.contact_two = contact_two;
        this.orgEmail = orgEmail;
        this.image_url = image_url;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEventOrg() {
        return eventOrg;
    }

    public void setEventOrg(String eventOrg) {
        this.eventOrg = eventOrg;
    }

    public String getContact_one() {
        return contact_one;
    }

    public void setContact_one(String contact_one) {
        this.contact_one = contact_one;
    }

    public String getContact_two() {
        return contact_two;
    }

    public void setContact_two(String contact_two) {
        this.contact_two = contact_two;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
