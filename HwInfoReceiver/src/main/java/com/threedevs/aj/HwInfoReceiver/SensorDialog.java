package com.threedevs.aj.HwInfoReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by AJ on 10.08.2014.
 */
public class SensorDialog extends DialogFragment {

    private static final String EXTRA_SENSOR_ID = "SENSOR_ID";
    private static final String EXTRA_SENSOR_VALUE = "SENSOR_VALUE";

    public SensorDialog(){

    }

    public static SensorDialog newInstance(long sensor_id, int value){
        SensorDialog fragment = new SensorDialog();
        Bundle bundle = new Bundle(2);
        bundle.putLong(EXTRA_SENSOR_ID, sensor_id);
        bundle.putInt(EXTRA_SENSOR_VALUE, value);
        fragment.setArguments(bundle);
        return fragment ;
    }

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SensorDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private SensorDialogListener mListener;

    private NumberPicker np;

    private long sensor_id;
    private int value = 0;

    private boolean success = false;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SensorDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SensorDialog");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sensor_id = getArguments().getLong(EXTRA_SENSOR_ID);
        value = getArguments().getInt(EXTRA_SENSOR_VALUE);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_sensor_settings, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.sensor_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        success = true;
                        mListener.onDialogPositiveClick(SensorDialog.this);
                    }
                })
                .setNegativeButton(R.string.sensor_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(SensorDialog.this);
                        SensorDialog.this.getDialog().cancel();
                    }
                });




        np = (NumberPicker) view.findViewById(R.id.numberPicker);
        if(np == null){
            Log.e("SensorDialog","NumberPicker is null?");
        }
        np.setMaxValue(200);
        np.setMinValue(1);
        np.setValue(value);

        return builder.create();
    }

    public long getSensorID(){
        return sensor_id;
    }

    public int getSensorIndex(){
        return np.getValue();
    }

    public boolean isSuccess(){ return success;}
}
