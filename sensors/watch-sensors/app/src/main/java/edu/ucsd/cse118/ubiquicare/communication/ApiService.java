package edu.ucsd.cse118.ubiquicare.communication;

import java.util.List;

import edu.ucsd.cse118.ubiquicare.model.HealthDataReport;
import edu.ucsd.cse118.ubiquicare.model.Reminder;
import edu.ucsd.cse118.ubiquicare.model.ResponseStructure;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("event")
    Call<String> reportHeartRate(@Body HealthDataReport heartRateReport);

    @GET("notifications")
    Call<List<Reminder>> getRemindersDue();
}

