package edu.ucsd.cse118.ubiquicare.sensors;

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

import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse118.ubiquicare.communication.HealthValuesConnector;
import edu.ucsd.cse118.ubiquicare.databinding.FragmentFalldetectionBinding;
import edu.ucsd.cse118.ubiquicare.databinding.FragmentHeartrateBinding;

public class FallDetectionFragment extends Fragment implements SensorEventListener {
    private FragmentFalldetectionBinding binding;
    private final Context mContext;
    private SensorManager mSensorManager;
    private Sensor mAcceleration;
    private Sensor mGyroscope;

    private float yAcceleration;
    private float zAcceleration;
    private float xAcceleration;

    private float numberUpdates = 0;

    public FallDetectionFragment (Context mContext) {
        this.mContext = mContext;
        Log.d("fall detection", "sensor created!");

        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor source = sensorEvent.sensor;
        numberUpdates++;
        yAcceleration = sensorEvent.values[SensorManager.DATA_Y];
        xAcceleration = sensorEvent.values[SensorManager.DATA_X];
        zAcceleration = sensorEvent.values[SensorManager.DATA_Z];
        Log.d("movement", Float.toString(xAcceleration) +", " + Float.toString(yAcceleration) +", " + Float.toString(zAcceleration));

            if (yAcceleration < -10) {
                binding.textView4.setText(Float.toString(yAcceleration) + "FALLEN");
            }
            binding.textView5.setText(Float.toString(numberUpdates));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentFalldetectionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }




}
