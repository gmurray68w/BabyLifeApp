package com.example.babylife;

public class NotificationItem {
    private String type;
    private String frequency;
    private int requestCode;

    public NotificationItem(String type, String frequency, int requestCode) {
        this.type = type;
        this.frequency = frequency;
        this.requestCode = requestCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
