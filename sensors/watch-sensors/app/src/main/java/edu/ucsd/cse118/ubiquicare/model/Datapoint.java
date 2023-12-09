package edu.ucsd.cse118.ubiquicare.model;

import com.google.gson.annotations.SerializedName;

public class Datapoint {
    public enum Sensor {
        @SerializedName("heartrate")
        HEART_RATE;
    }
    private Sensor sensor;
    private float value;

    public Datapoint(Sensor sensor, float value) {
        this.sensor = sensor;
        this.value = value;
    }
}
