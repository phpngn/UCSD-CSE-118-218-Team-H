package edu.ucsd.cse118.ubiquicare.reminders;

import android.content.Context;
import android.util.Log;
import android.os.Handler;

import edu.ucsd.cse118.ubiquicare.model.Reminder;
import edu.ucsd.cse118.ubiquicare.communication.RemindersConnector;

public class RemindersFetcher {
    private RemindersConnector remindersConnector;
    private Context context;

    public RemindersFetcher(Context context) {
        remindersConnector = new RemindersConnector();
        this.context = context;
    }

    private boolean isFetching = false;
    private Handler handler;
    private final long FETCH_INTERVAL = 5 * 1000;

    public void startFetchingReminders() {
        if (!isFetching) {
            isFetching = true;
            handler = new Handler();
            fetchRemindersPeriodically();
        }
    }

    public void stopFetchingReminders() {
        if (isFetching) {
            isFetching = false;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
        }
    }


    private void fetchRemindersPeriodically() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFetching) {
                    // Fetch reminders here
                    remindersConnector.getReminderDue(new RemindersConnector.ReminderListener() {
                        @Override
                        public void onReminderReceived(Reminder reminder) {
                            // Handle received reminder
                            // Forward the reminder to the necessary location or perform actions
                            Log.d("fetchRemindersPeriodically", reminder.getTitle());
                            SmartwatchController smartwatchController = new SmartwatchController(context);
                            smartwatchController.vibrateSmartwatch();
                            smartwatchController.showNotification("Your notification message here");

                        }

                        @Override
                        public void onReminderFailed(String errorMessage) {
                            // Handle failure/error in receiving the reminder
                            Log.d("fetchRemindersPeriodically", "no reminder");
                        }
                    });

                    // Schedule next fetch after the interval
                    handler.postDelayed(this, FETCH_INTERVAL);
                }
            }
        }, FETCH_INTERVAL);
    }
}
