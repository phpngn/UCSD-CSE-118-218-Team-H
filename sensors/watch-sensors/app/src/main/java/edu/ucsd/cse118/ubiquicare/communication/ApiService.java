package edu.ucsd.cse118.ubiquicare.communication;

import java.util.List;

import edu.ucsd.cse118.ubiquicare.communication.model.HealthDataReport;
import edu.ucsd.cse118.ubiquicare.communication.model.ResponseStructure;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("event")
    Call<String> reportHeartRate(@Body HealthDataReport heartRateReport);

    @POST("bloodoxygen")
    Call<ResponseStructure> reportBloodOxygen(@Body HealthDataReport bloodOxygenReport);

    @POST("falldetection")
    Call<Void> reportFallDetection(@Body List<Float> values);
}

