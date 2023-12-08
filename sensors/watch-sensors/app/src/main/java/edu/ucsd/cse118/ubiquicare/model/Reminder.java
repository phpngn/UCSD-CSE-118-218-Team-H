package edu.ucsd.cse118.ubiquicare.model;

import java.sql.Date;
import java.sql.Time;

public class Reminder {
    private String title;
    private Time time;
    private boolean checkedOff;

    public Reminder(String title, Time time, boolean checkedOff) {
        this.title = title;
        this.time = time;
        this.checkedOff = checkedOff;
    }

    public String getTitle() {
        return title;
    }

    public Time getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean isCheckedOff() {
        return checkedOff;
    }

    public void setCheckedOff(boolean checkedOff) {
        this.checkedOff = checkedOff;
    }
}
