package edu.ucsd.cse118.ubiquicare;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.os.Bundle;
import android.widget.TextView;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import edu.ucsd.cse118.ubiquicare.databinding.ActivityMainBinding;
import edu.ucsd.cse118.ubiquicare.reminders.RemindersFetcher;
import edu.ucsd.cse118.ubiquicare.sensors.HeartRateFragment;

public class MainActivity extends FragmentActivity {

    private TextView mTextView;
    private TextView mReminderTextView;
    private ActivityMainBinding binding;
    private RemindersFetcher remindersFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.BODY_SENSORS},123);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;
        mReminderTextView = binding.reminderTextView;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.heart_rate_fragment_placeholder, new HeartRateFragment(this));
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
}