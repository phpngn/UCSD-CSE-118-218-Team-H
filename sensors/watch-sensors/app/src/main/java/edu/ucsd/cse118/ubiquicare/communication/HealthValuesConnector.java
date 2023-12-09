package edu.ucsd.cse118.ubiquicare.communication;

import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ucsd.cse118.ubiquicare.model.Datapoint;
import retrofit2.Call;

import edu.ucsd.cse118.ubiquicare.model.HealthDataReport;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthValuesConnector {

    private ApiService apiService;

    public HealthValuesConnector() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void sendHeartRateValues(List<Float> values) {
        Log.d("sendHeartRateValues", "sendHeartRateValues: " + values.toString());
        HealthDataReport heartRateReport = createHeartRateReport(values);

        Call<String> call = apiService.reportHeartRate(heartRateReport);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Handle successful response here
                System.out.println("Heart rate values sent successfully");
                Log.d("sendHeartRateValues", "call: " + call);
                Log.d("sendHeartRateValues", "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure/error here
                System.out.println("Heart rate values failed to send");
                Log.d("sendHeartRateValues", "call: " + call);
                Log.d("sendHeartRateValues", "onFailure: " + t.getMessage());
            }
        });
    }

    public HealthDataReport createHeartRateReport(List<Float> values) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String formattedTimestamp = timestamp.toString().replaceAll("Z", "");
        List<Datapoint> datapoints = new ArrayList<>();
        for (Float value : values) {
            datapoints.add(new Datapoint(Datapoint.Sensor.HEART_RATE, value));
        }
        HealthDataReport heartRateReport = new HealthDataReport(HealthDataReport.EventType.HEART_RATE, HealthDataReport.EventLevel.REPORT, "device1", formattedTimestamp, datapoints);
        return heartRateReport;
    }
}
