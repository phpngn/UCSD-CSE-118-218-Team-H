package edu.ucsd.cse118.ubiquicare.communication;

import android.util.Log;

import edu.ucsd.cse118.ubiquicare.communication.ApiService;
import edu.ucsd.cse118.ubiquicare.communication.RetrofitClient;
import edu.ucsd.cse118.ubiquicare.model.Reminder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemindersConnector {
    private ApiService apiService;

    public RemindersConnector() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public interface ReminderListener {
        void onReminderReceived(Reminder reminder);
        
        void onReminderFailed(String errorMessage);
    }
    
    public void getReminderDue(final ReminderListener reminderListener) {
        Call<Reminder> call = apiService.getReminderDue();
        call.enqueue(new Callback<Reminder>() {
            @Override
            public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful response here
                    Reminder receivedReminder = response.body();
                    System.out.println("Reminder received successfully");
                    Log.d("getReminderDue", "call: " + call);
                    Log.d("getReminderDue", "onResponse: " + response);
    
                    // Pass the received reminder outside the method
                    reminderListener.onReminderReceived(receivedReminder);
                } else {
                    // Handle unsuccessful response here
                    System.out.println("Reminder failed to receive");
                    Log.d("getReminderDue", "Unsuccessful response: " + response);
                    reminderListener.onReminderFailed("Failed to receive reminder");
                }
            }
    
            @Override
            public void onFailure(Call<Reminder> call, Throwable t) {
                // Handle failure/error here
                System.out.println("Reminder failed to receive");
                Log.d("getReminderDue", "call: " + call);
                Log.d("getReminderDue", "onFailure: " + t.getMessage());
    
                reminderListener.onReminderFailed("Failed to receive reminder: " + t.getMessage());
            }
        });
    }
    
}
