package edu.ucsd.cse118.ubiquicare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HealthDataReport {
    public enum EventType {
        @SerializedName("heartrate")
        HEART_RATE,
        @SerializedName("bloodoxygen")
        BLOOD_OXYGEN,
        @SerializedName("fall")
        FALL_DETECTION;
    }

    public enum EventLevel {
        @SerializedName("report")
        REPORT,
        @SerializedName("alert")
        ALERT;
    }
    @SerializedName("event_type")
    private EventType eventType;
    @SerializedName("event_level")
    private EventLevel eventLevel;
    private String device;
    private String timestamp;
    private List<Datapoint> datapoints;

    public HealthDataReport(EventType eventType, EventLevel eventLevel, String device, String timestamp, List<Datapoint> datapoints) {
        this.eventType = eventType;
        this.eventLevel = eventLevel;
        this.device = device;
        this.timestamp = timestamp;
        this.datapoints = datapoints;

    }

}
