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

import java.util.ArrayList;

import edu.ucsd.cse118.ubiquicare.communication.HealthValuesConnector;
import edu.ucsd.cse118.ubiquicare.databinding.FragmentFalldetectionBinding;
import edu.ucsd.cse118.ubiquicare.databinding.FragmentHeartrateBinding;

public class FallDetectionFragment extends Fragment implements SensorEventListener {
    private FragmentFalldetectionBinding binding;
    private final Context mContext;
    private SensorManager mSensorManager;
    private Sensor mAcceleration;

    private float yAcceleration;

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
        Log.d("movement", Float.toString((sensorEvent.values[SensorManager.DATA_Y])));
        yAcceleration = sensorEvent.values[SensorManager.DATA_Y];
        if (yAcceleration < -10){
            binding.textView4.setText(Float.toString(yAcceleration) + "FALLEN");
        }
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
