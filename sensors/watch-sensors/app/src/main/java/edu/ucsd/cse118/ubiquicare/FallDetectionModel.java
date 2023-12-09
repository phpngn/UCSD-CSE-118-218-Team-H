package edu.ucsd.cse118.ubiquicare;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.List;

public class FallDetectionModel extends ViewModel {
    public  MutableLiveData<Boolean> fallenLiveData = new MutableLiveData<>();
    public List<Double> fallenValuesLiveData;

    public LiveData<Boolean> getFallData (){
        return fallenLiveData;
    }

    public List<Double> getFallValuesData () { return fallenValuesLiveData;}

    public void setFallData (boolean fell){
        fallenLiveData.setValue(fell);
    }

    public void setFallValuesData(List<Double> values){
        fallenValuesLiveData = values;
    }

    public FallDetectionModel(){

    }


}
