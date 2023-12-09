package edu.ucsd.cse118.ubiquicare.communication;

import android.util.Log;

import java.util.List;

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

    public interface RemindersListener {
        void onRemindersReceived(List<Reminder> reminders);
        void onRemindersFailed(String errorMessage);
    }


    public void getRemindersDue(final RemindersListener remindersListener) {
        Call<List<Reminder>> call = apiService.getRemindersDue(); // Assuming the endpoint returns a list of reminders
        call.enqueue(new Callback<List<Reminder>>() {
            @Override
            public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful response here
                    List<Reminder> receivedReminders = response.body();

                    System.out.println("Reminders received successfully");
                    Log.d("getRemindersDue", "call: " + call);
                    Log.d("getRemindersDue", "onResponse: " + response);

                    // Pass the received reminders outside the method
                    remindersListener.onRemindersReceived(receivedReminders);
                } else {
                    // Handle unsuccessful response here
                    System.out.println("Reminders failed to receive");
                    Log.d("getRemindersDue", "Unsuccessful response: " + response);
                    remindersListener.onRemindersFailed("Failed to receive reminders");
                }
            }

            @Override
            public void onFailure(Call<List<Reminder>> call, Throwable t) {
                // Handle failure/error here
                System.out.println("Reminders failed to receive");
                Log.d("getRemindersDue", "call: " + call);
                Log.d("getRemindersDue", "onFailure: " + t.getMessage());

                remindersListener.onRemindersFailed("Failed to receive reminders: " + t.getMessage());
            }
        });
    }
}

