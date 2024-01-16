package com.example.babylife;

public class FeedingEntryList {
    String name;
    String date;
    String time;
    String type;
    String duration;
    String amount;
    String notes;

    public FeedingEntryList(String name, String date, String time, String type, String duration, String amount, String notes){
        this.name = name;
        this.date = date;
        this.time = time;
        this.type = type;
        this.duration = duration;
        this.amount = amount;
        this.notes = notes;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }



}
