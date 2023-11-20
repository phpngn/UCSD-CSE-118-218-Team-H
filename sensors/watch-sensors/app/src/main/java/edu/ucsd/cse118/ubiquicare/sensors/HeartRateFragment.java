package edu.ucsd.cse118.ubiquicare.sensors;


import androidx.fragment.app.Fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.ucsd.cse118.ubiquicare.databinding.FragmentHeartrateBinding;

public class HeartRateFragment extends Fragment implements SensorEventListener {
    private FragmentHeartrateBinding binding;
    private SensorManager mSensorManager;
    private Sensor mHeartRate;
    private String heartRateVal;
    Context mContext;

    /*@Override
   public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("heartbeat", "sensor created!");
    }*/

    public HeartRateFragment(Context mContext) {
        this.mContext = mContext;
        Log.d("heartbeat", "sensor created!");

        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //ActivityCompat.requestPermissions(HeartRate.this, new String[] {Manifest.permission.BODY_SENSORS},123);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentHeartrateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        binding.textView3.setText("new heart rate");
        //EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }


    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        System.out.println(event.values[0]);
        Log.d("heartrate", Float.toString((event.values[0])));
        heartRateVal = Float.toString((event.values[0]));
        binding.textView3.setText("Heart Rate: " + heartRateVal);
    }
}
