package edu.ucsd.cse118.ubiquicare.model;

import java.sql.Date;
import java.sql.Time;

public class Reminder {
    private String title;
    private String timestamp;
    private int checked;

    public Reminder(String title, String timestamp, int checked) {
        this.title = title;
        this.timestamp = timestamp;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String timestamp) {
        this.timestamp = timestamp;
    }

    public int isChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
