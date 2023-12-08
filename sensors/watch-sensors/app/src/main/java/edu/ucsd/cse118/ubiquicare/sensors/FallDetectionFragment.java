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
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.beans.*;

import edu.ucsd.cse118.ubiquicare.FallDetectionModel;
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
    private double totalAcceleration;
    private boolean fallChanceFlag = false;

    private FallDetectionModel fallDetectionModel;
    private double[] data = {9,9,9,9,9,9};
    private int index = 0;

    private float numberUpdates = 0;

    public FallDetectionFragment (Context mContext) {
        this.mContext = mContext;
        Log.d("fall detection", "sensor created!");

        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public void onStart() {
        super.onStart();
        fallDetectionModel = new ViewModelProvider(requireActivity()).get(FallDetectionModel.class);

        fallDetectionModel.setFallData(false);
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
        //Log.d("movement", Float.toString(xAcceleration) +", " + Float.toString(yAcceleration) +", " + Float.toString(zAcceleration));
        totalAcceleration = Math.sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration + zAcceleration*zAcceleration);
       // System.out.println(Arrays.toString(data));
        for(int i = 0; i < data.length-1; i++ ){
            data[i] = data[i+1];
        }
        data[data.length-1] = totalAcceleration;


            if(fallChanceFlag){
                index--;
                for(int i = index; i <= data.length-1; i++){
                    if(index == data.length-2 && i == data.length-1){
                        if(data[i] < 5.9 || data[i] > 10){
                            return;
                        }
                        else{
                            fallChanceFlag = false;
                        }
                    }
                    if(index == data.length-3 && i == data.length-1){
                        if(data[i-1] <  5.9 && data[i] > 10){
                            return;
                        }
                        else if(data[i-1] > 10){
                            return;
                        }
                        else {
                            fallChanceFlag = false;
                        }
                    }
                    if(index == data.length-4 && i == data.length-1){
                        if(data[i]>7 && data[i]<11){
                            //binding.textView5.setText("you fell");
                            fallDetectionModel.setFallData(true);
                            fallChanceFlag = false;
                            return;
                        }
                        else{
                            fallChanceFlag = false;
                        }
                    }
                }
            }

            if (totalAcceleration < 5.9 && !fallChanceFlag) {
                fallChanceFlag = true;
                index = data.length-1;
                binding.textView4.setText(Double.toString(Math.round(totalAcceleration)) +"possible fall");
                return;
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
