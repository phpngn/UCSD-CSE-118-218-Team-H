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
    private ActivityMainBinding binding;
    private RemindersFetcher remindersFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.BODY_SENSORS},123);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new HeartRateFragment(this));
        ft.commit();

        remindersFetcher = new RemindersFetcher();
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
}