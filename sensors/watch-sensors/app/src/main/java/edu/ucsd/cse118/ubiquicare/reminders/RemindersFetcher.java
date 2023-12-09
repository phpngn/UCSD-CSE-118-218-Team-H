package edu.ucsd.cse118.ubiquicare.reminders;

import android.content.Context;
import android.util.Log;
import android.os.Handler;

import java.util.List;

import edu.ucsd.cse118.ubiquicare.model.Reminder;
import edu.ucsd.cse118.ubiquicare.communication.RemindersConnector;
import edu.ucsd.cse118.ubiquicare.MainActivity;

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
                    remindersConnector.getRemindersDue(new RemindersConnector.RemindersListener() {
                        @Override
                        public void onRemindersReceived(List<Reminder> reminders) {
                            // Handle received reminder
                            // Forward the reminder to the necessary location or perform actions
                            if (reminders.size() > 0) {
                                String reminderText = reminders.get(0).getTitle();
                                Log.d("fetchRemindersPeriodically", reminderText);
                                SmartwatchController smartwatchController = new SmartwatchController(context);
                                smartwatchController.vibrateSmartwatch();

                                if (context instanceof MainActivity) {
                                    MainActivity mainActivity = (MainActivity) context;
                                    mainActivity.updateReminderText("Reminder now: " + reminderText);
                                }
                            } else {
                                Log.d("fetchRemindersPeriodically", "no reminder");
                                if (context instanceof MainActivity) {
                                    MainActivity mainActivity = (MainActivity) context;
                                    mainActivity.updateReminderText("Currently no reminders");
                                }
                            }
                        }

                        @Override
                        public void onRemindersFailed(String errorMessage) {
                            // Handle failure/error in receiving the reminder
                            Log.d("fetchRemindersPeriodically", "onRemindersFailed: " + errorMessage);
                        }
                    });

                    // Schedule next fetch after the interval
                    handler.postDelayed(this, FETCH_INTERVAL);
                }
            }
        }, FETCH_INTERVAL);
    }
}
