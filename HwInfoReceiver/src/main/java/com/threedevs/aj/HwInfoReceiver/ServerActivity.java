package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Sensor;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Server;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

import java.util.ArrayList;
import java.util.List;


public class ServerActivity extends ActionBarActivity implements SensorDialog.SensorDialogListener{

    private Networker networker = null;
    private DataBaseHandle db;

    Server server;
    String server_ip = null;

    List<Sensor> sensors = new ArrayList<Sensor>();
    List<String> sensor_strings;

    private String TAG = "ServerActivity";

    private ListView lv;
    private TextView tv;

    private long timer_delay = 2000;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            //well we want to communicate tho :)
            if(networker != null){
                //request stuff
                for(int  i = 0; i < sensors.size(); i++) {
                    networker.SendDataToNetwork("get::" + Long.toString(sensors.get(i).getIndex()) + ";");
                }

                //read stuff
                List<String> messages = networker.getMessages();
                //split the messages and set up the sensor strings for this moment...
                //MESSAGE DISPATCHER
                for(int i = 0; i < messages.size(); i ++){

                    String msg = messages.get(i);
                    String[] message_split = msg.split("::");
                    if(message_split.length > 0) {

                        if(message_split[0].equals("reading")) {

                            try {
                                String sensor_id = message_split[1];
                                long id = Long.parseLong(sensor_id, 10);
                                setSensorString(id,msg);
                            }catch (Exception e){
                                Log.e(TAG, e.toString());
                            }


                        }
                    }
                }
            }

            //work on UI here...
            timerHandler.postDelayed(this, timer_delay);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);


        lv = (ListView) this.findViewById(R.id.listView);

        tv = (TextView) this.findViewById(R.id.textView);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                server_ip= null;
            } else {
                server_ip= extras.getString("ip");
            }
        } else {
            server_ip= (String) savedInstanceState.getSerializable("ip");
            tv.setText("Server ip: " + server_ip);
        }

        //if screen rotated or shit... no intent is fired but we stored the ip in global context...
        if(server_ip == null){
            server_ip = ((CustomApplication)getApplication()).getIP();
        }


        //database
        db = new DataBaseHandle(getApplicationContext());

        if(server_ip == null){
            showToast("Server ip not given ...");
        }
        else {
            server = db.getServerByIP(server_ip);
            if(server != null){
                showToast("Server found in DB");
                tv.setText("Server connected:   ip: " + server_ip + " id: " + server.getId());
            }
        }

        networker = ((CustomApplication)getApplication()).getNetworker();


        refillList();

        //start dat timer :)
        timerHandler.postDelayed(timerRunnable, 0);
    }


    @Override
    public void onPause() {
        super.onPause();
        //stop timer ... when paused...
        timerHandler.removeCallbacks(timerRunnable);
    }


    @Override
    public void onResume() {
        super.onPause();
        //start timer again
        timerHandler.postDelayed(timerRunnable, 0);
    }


    //ListViews context Menu...
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Sensor: " + sensors.get(info.position).getId());
            String[] menuItems = getResources().getStringArray(R.array.sensor_list_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.sensor_list_menu);
        String menuItemName = menuItems[menuItemIndex];

        int listItemIndex = info.position;

        //Connect
        if(menuItemIndex == 0){
            showToast("Settings");
            Sensor s = sensors.get(listItemIndex);
            showNoticeDialog(s);
        }
        //Delete
        else if(menuItemIndex == 1){
            showToast("Delete");
            deleteSensor(listItemIndex);
        }
        return true;
    }


    public void refillList(){
        sensors = new ArrayList<Sensor>();
        sensor_strings = new ArrayList<String>();

        //database
        db = new DataBaseHandle(getApplicationContext());

        if(server != null) {
            sensors = db.getAllSensorsByServer(server);
            showToast( sensors.size() + " sensors found...");
        }

        for(int i = 0; i < sensors.size(); i++){
            sensor_strings.add(Long.toString(sensors.get(i).getIndex()));
        }

        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sensor_strings);
        lv.setAdapter(listenAdapter);
        registerForContextMenu(lv);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(CharSequence cs){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, cs, duration);
        toast.show();
    }
    
    public void addSensor(View v){
        if(server != null){
            //database
            db = new DataBaseHandle(getApplicationContext());
            Sensor sensor = new Sensor(1);
            long sensor_id = db.createSensor(sensor);

            sensor_strings.add(Long.toString(sensor_id));

            db.createServerSensor(server.getId(), sensor_id);
        }
        refillList();
    }


    private void deleteSensor(int index){
        Sensor s = sensors.get(index);

        db = new DataBaseHandle(getApplicationContext());
        long sensor_id = s.getId();
        long serversensorid = db.getServerSensorIDBySensorID(sensor_id);
        if(serversensorid != -1) {
            db.deleteServerSensor(serversensorid);
            Log.i(TAG, "server sensor deleted (id: " + serversensorid + "  "  + sensor_id + ")...");
        }
        else{
            Log.i(TAG, "server sensor NOT FOUND (id: " + serversensorid + "  " + sensor_id + ") ...");
        }
        db.deleteSensor(sensor_id);

        refillList();
    }


    private void setSensorString(long index, String message){
        for(int i = 0; i < sensors.size(); i++){
            if(sensors.get(i).getIndex() == index){

                //split the message:
                String[] split_message = message.split("::");

                if(split_message.length > 0) {

                    if(split_message[0].equals("reading")) {

                        String value = "";

                        value = value + split_message[2] + ": " + split_message[3] + " " + split_message[4];

                        sensor_strings.set(i, value);
                        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sensor_strings);
                        lv.setAdapter(listenAdapter);
                        registerForContextMenu(lv);

                    }
                }

            }
        }
    }

    public void getSensor10(View v){
        sendSensorRequest(10);
    }

    public void sendSensorRequest(int sensor_id){
        if(networker != null) {
            networker.SendDataToNetwork("get::" + Integer.toString(sensor_id) + ";");
        }
    }

    public void showNoticeDialog(Sensor sensor) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SensorDialog(sensor.getId(), (int) sensor.getIndex());
        dialog.show(getSupportFragmentManager(), "SensorDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SensorDialog sd = (SensorDialog) dialog;
        long sensor_id = sd.getSensorID();
        int value = sd.getSensorIndex();
        for(int i = 0; i < sensors.size(); i++){
            Sensor s = sensors.get(i);
            if(s.getId() == sensor_id){
                //update sensor
                s.setIndex(value);
                db = new DataBaseHandle(getApplicationContext());
                db.updateSensor(s);
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
