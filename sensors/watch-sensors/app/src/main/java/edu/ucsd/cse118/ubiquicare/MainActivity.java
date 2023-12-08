package edu.ucsd.cse118.ubiquicare;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.Manifest;

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
import edu.ucsd.cse118.ubiquicare.sensors.HeartRateFragment;


public class MainActivity extends FragmentActivity {

    private ActivityMainBinding binding;
    private FallDetectionModel fallDetectionModel;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BODY_SENSORS}, 123);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*Intent intent = new Intent(MainActivity.this, HeartRate.class);
        startActivity(intent);*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new HeartRateFragment(this));
        ft.replace(R.id.your_placeholder_2, new FallDetectionFragment(this));


        ft.commit();

        fallDetectionModel = new ViewModelProvider(this).get(FallDetectionModel.class);
        final Observer<Boolean> fallObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean fell) {
                // Update the UI
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                if (fell) {
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("localAlert");

                    if (prev != null) {
                        ft2.remove(prev);
                    }
                    ft2.addToBackStack(null);
                    fallDetectionAlert newAlert = fallDetectionAlert.newInstance(context);
                    newAlert.show(ft2, "localAlert");

                }
            }
        };
        fallDetectionModel.getFallData().observe(this, fallObserver);;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}