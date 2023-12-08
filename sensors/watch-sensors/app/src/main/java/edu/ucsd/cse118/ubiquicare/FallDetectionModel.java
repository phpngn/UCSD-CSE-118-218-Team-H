package edu.ucsd.cse118.ubiquicare;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FallDetectionModel extends ViewModel {
    public  MutableLiveData<Boolean> fallenLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getFallData (){
        return fallenLiveData;
    }

    public void setFallData (boolean fell){
        fallenLiveData.setValue(fell);
    }

    public FallDetectionModel(){

    }


}
