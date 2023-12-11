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
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;

import edu.ucsd.cse118.ubiquicare.databinding.FragmentFalldetectionBinding;


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
    private Double[] data = new Double[] {9.0,9.0,9.0,9.0,9.0,9.0};

    private Double[] lowData = new Double[] {9.0,9.0,9.0,9.0,9.0,9.0};

    private Double[] highData = new Double[] {9.0,9.0,9.0,9.0,9.0,9.0};

    private Double[] normalData = new Double[] {9.0,9.0,9.0,9.0,9.0,9.0};
    private int index = 0;
    private int windowSize = 8;

    private double low = 0;
    private double high = 0;
    private double normal = 0;
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
       //System.out.println(Arrays.toString(data));
        senseFall();
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

    public void senseFall(){
        //move the window one space (removes the oldest value and adds new value at end)
        for(int i = 0; i < data.length-1; i++ ){
            data[i] = data[i+1];
        }
        data[data.length-1] = totalAcceleration;

        //possible fall sensed
        if (totalAcceleration < 5 && !fallChanceFlag) {
            fallChanceFlag = true;
            index = 0;
            low = totalAcceleration;
            Log.d("low val", ((Double)low).toString());
            return;
        }

        if(fallChanceFlag){
            index++;
            if(index < windowSize && high==0){
                Log.d("possible high val", ((Double)totalAcceleration).toString());
                if(totalAcceleration >16){
                    high=totalAcceleration;
                    index=0;
                    Log.d("high val", ((Double)high).toString());
                }
                return;
            }

            if(index < windowSize && high!=0) {
                if(totalAcceleration >7 && totalAcceleration<13){
                    //fall detected change state
                    fallDetectionModel.setFallData(true);
                    fallDetectionModel.setFallValuesData(Arrays.asList(data));
                    //reset values for next fall
                    fallChanceFlag = false;
                    low = 0;
                    high = 0;
                    index = 0;
                }
                return;
            }
            if(index >= windowSize){
                //no fall detected reset vals
                fallChanceFlag = false;
                low = 0;
                high = 0;
                index = 0;
                return;
            }
        }

        /*
        //if possible fall sensed check following values for fall pattern
        if(fallChanceFlag){
            index--;
            //look from the fall flag to the latest acceleration
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
                        fallDetectionModel.setFallValuesData(Arrays.asList(data));
                        fallChanceFlag = false;
                        return;
                    }
                    else{
                        fallChanceFlag = false;
                    }
                }
            }
        }*/

    }



}
