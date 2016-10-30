package com.threedevs.aj.HwInfoReceiver;



/**
 * Created by AJ on 20.02.2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by AJ on 10.08.2014.
 */
public class ServerDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ServerDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private ServerDialogListener mListener;

    private EditText te;

    private String address;


    private boolean success = false;

    public ServerDialog(){

    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ServerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ServerDialog");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_server_settings, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.sensor_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        success = true;
                        mListener.onDialogPositiveClick(ServerDialog.this);
                    }
                })
                .setNegativeButton(R.string.sensor_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(ServerDialog.this);
                        ServerDialog.this.getDialog().cancel();
                    }
                });




        te = (EditText) view.findViewById(R.id.editText);
        te.setText("192.168.xxx.xxx");
        if(te == null){
            Log.e("ServerDialog", "EditText is null?");
        }


        return builder.create();
    }

    public String getAddress(){
        return te.getText().toString();
    }

    public boolean isSuccess(){ return success;}
}
