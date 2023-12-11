package edu.ucsd.cse118.ubiquicare.notifications;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse118.ubiquicare.sensors.FallDetectionModel;
import edu.ucsd.cse118.ubiquicare.R;
import edu.ucsd.cse118.ubiquicare.communication.HealthValuesConnector;

public class fallDetectionAlert extends DialogFragment {
    private static Context mcontext;
    private FallDetectionModel fallDetectionModel;

    private HealthValuesConnector healthValuesConnector;
    public static fallDetectionAlert newInstance(Context context) {
        fallDetectionAlert f = new fallDetectionAlert();
        mcontext = context;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         fallDetectionModel = new ViewModelProvider(requireActivity()).get(FallDetectionModel.class);
         healthValuesConnector = new HealthValuesConnector();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_falldialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Vibrator vibrator = (Vibrator)mcontext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

        view.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fallDetectionModel.setFallData(false);
                getActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                healthValuesConnector.sendFallDetectionValues(fallDetectionModel.getFallValuesData());
                fallDetectionModel.setFallData(false);
                getActivity().onBackPressed();
            }
        });
    }


    @Override
    public void dismiss() {
        healthValuesConnector.sendFallDetectionValues(fallDetectionModel.getFallValuesData());
        fallDetectionModel.setFallData(false);
        getActivity().onBackPressed();
    }


    public boolean isShowing (){
        return this.isVisible();
    }


}
