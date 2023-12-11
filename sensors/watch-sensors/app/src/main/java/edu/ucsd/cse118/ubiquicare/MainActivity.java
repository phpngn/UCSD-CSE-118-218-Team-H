package edu.ucsd.cse118.ubiquicare;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.Manifest;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse118.ubiquicare.databinding.ActivityMainBinding;
import edu.ucsd.cse118.ubiquicare.notifications.fallDetectionAlert;
import edu.ucsd.cse118.ubiquicare.sensors.FallDetectionFragment;
import edu.ucsd.cse118.ubiquicare.reminders.RemindersFetcher;
import edu.ucsd.cse118.ubiquicare.sensors.FallDetectionModel;
import edu.ucsd.cse118.ubiquicare.sensors.HeartRateFragment;


public class MainActivity extends FragmentActivity {

    private FallDetectionModel fallDetectionModel;
    private Context context = this;
    private TextView mTextView;
    private TextView mReminderTextView;
    private ActivityMainBinding binding;
    private RemindersFetcher remindersFetcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BODY_SENSORS}, 123);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        fallDetectionModel = new ViewModelProvider(this).get(FallDetectionModel.class);
        final Observer<Boolean> fallObserver = createFallAlertObserver();

        fallDetectionModel.getFallData().observe(this, fallObserver);;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextView = binding.text;
        mReminderTextView = binding.reminderTextView;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.heart_rate_fragment_placeholder, new HeartRateFragment(this));
        ft.replace(R.id.fall_detection_fragment_placeholder, new FallDetectionFragment(this));
        ft.commit();

        remindersFetcher = new RemindersFetcher(this);
        remindersFetcher.startFetchingReminders();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remindersFetcher.stopFetchingReminders();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        remindersFetcher.startFetchingReminders();
    }

    public void updateReminderText(String text) {
        if (mReminderTextView != null) {
            mReminderTextView.setText(text);
        }
    }

    public Observer<Boolean> createFallAlertObserver(){
        Observer<Boolean> fallAlertObserver= new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean fell) {
                // Update the UI when fallen
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                if (fell) {
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("localAlert");
                    if (prev != null) {
                        ft2.remove(prev);
                    }
                    ft2.addToBackStack(null);
                    fallDetectionAlert newAlert = fallDetectionAlert.newInstance(context);
                    newAlert.show(ft2, "localAlert");

                    //dismiss alert if not interacted with
                    final Handler handler  = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (newAlert.isShowing()) {
                                newAlert.dismiss();
                            }
                        }
                    };
                    handler.postDelayed(runnable,8000);
                }
            }
        };
        return fallAlertObserver;
    }
}