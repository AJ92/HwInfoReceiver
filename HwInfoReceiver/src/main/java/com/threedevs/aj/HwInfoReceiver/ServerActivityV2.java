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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.threedevs.aj.HwInfoReceiver.CardLayout.RowItem;
import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Sensor;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Server;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class ServerActivityV2 extends ActionBarActivity implements SensorDialog.SensorDialogListener{

    GridView gridView;
    GaugeAdapter gaugeAdapter;

    private String TAG = "ServerActivityV2";

    //stuff...
    private DataBaseHandle db;
    private Server server = null;
    private String server_ip = null;
    //ref to the networker in customApplication class
    private Networker networker = null;
    //holds id and index
    private List<Sensor> sensors = new ArrayList<Sensor>();
    //holds actual sensor data...
    private List<GaugeData> sensor_gaugedatas = new ArrayList<GaugeData>();
    //refresh time for server communication...
    private long timer_delay = 2000;

    private boolean destroyed = false;


    private Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if(destroyed){
                return;
            }

            //well we want to communicate tho :)
            if (networker != null) {
                //request stuff
                for (int i = 0; i < sensors.size(); i++) {
                    networker.SendDataToNetwork("get::" + Long.toString(sensors.get(i).getIndex()) + ";");
                }

                //read stuff
                List<String> messages = networker.getMessages();
                //split the messages and set up the sensor strings for this moment...
                //MESSAGE DISPATCHER
                for (int i = 0; i < messages.size(); i++) {

                    String msg = messages.get(i);
                    String[] message_split = msg.split("::");
                    if (message_split.length > 0) {

                        if (message_split[0].equals("reading")) {

                            try {
                                String sensor_id = message_split[1];
                                long id = Long.parseLong(sensor_id, 10);
                                updateSensorData(id, msg);
                            } catch (Exception e) {
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

    private void updateSensorData(long index, String message) {
        Log.e(TAG,"updating sensor at index: " + index);
        for (int i = 0; i < sensors.size(); i++) {
            if (sensors.get(i).getIndex() == index) {

                //split the message:
                String[] split_message = message.split("::");

                if (split_message.length > 0) {

                    if (split_message[0].equals("reading")) {

                        //update string
                        String value = "";
                        value = value + split_message[2] + ": " + split_message[3] + " " + split_message[4];


                        Log.e(TAG,"updating gauge at index: " + i);
                        Log.e(TAG,"updating gauge name: " + split_message[2]);

                        //update GaugeData
                        GaugeData gd = sensor_gaugedatas.get(i);
                        gd.setName(split_message[2]);
                        gd.setUnit(split_message[4]);
                        try {
                            double value_double = Double.parseDouble(split_message[3]);
                            gd.setValue(value_double);
                        } catch (Exception e) {
                            return;
                        }

                        //update Gauge
                        gd.updateViews();

                    }
                }

            }
        }
        gaugeAdapter.notifyDataSetChanged();
        //gridView.invalidateViews();
        //gridView.setAdapter(gaugeAdapter);
    }

    public void reLoadSensorsFromDataBase(){
        sensors = new ArrayList<Sensor>();
        sensor_gaugedatas = new ArrayList<GaugeData>();

        //database
        db = new DataBaseHandle(getApplicationContext());

        if(server != null) {
            sensors = db.getAllSensorsByServer(server);
            showToast( sensors.size() + " sensors found...");
        }

        for(int i = 0; i < sensors.size(); i++){
            //construct GaugeData and CustomGauge
            GaugeData gd = new GaugeData();
            gd.setName("id: " + sensors.get(i).getId());
            sensor_gaugedatas.add(gd);
        }
    }

    public void createGridView(){
        //init test gaugesDatas...
        GaugeData gaugeDatas[] = new GaugeData[sensor_gaugedatas.size()];
        for (int i = 0; i < sensor_gaugedatas.size(); i++) {
            gaugeDatas[i] = sensor_gaugedatas.get(i);
        }

        gridView = (GridView) findViewById(R.id.activity_server_v2);

        gaugeAdapter = new GaugeAdapter(this, gaugeDatas);

        gridView.setAdapter(gaugeAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        ((TextView) v.findViewById(R.id.gauge_text))
                                .getText(), Toast.LENGTH_SHORT).show();

            }
        });

        registerForContextMenu(gridView);

    }


    //ListViews context Menu...
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.activity_server_v2) {
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

    public void showNoticeDialog(Sensor sensor) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = SensorDialog.newInstance(sensor.getId(), (int) sensor.getIndex());
        dialog.show(getSupportFragmentManager(), "SensorDialog");
    }

    public void addSensor(View v){
        if(server != null){
            //database
            db = new DataBaseHandle(getApplicationContext());
            Sensor sensor = new Sensor(0);
            long sensor_id = db.createSensor(sensor);

            db.createServerSensor(server.getId(), sensor_id);
        }

        reLoadSensorsFromDataBase();
        createGridView();

        //start dat timer :)
        timerHandler.postDelayed(timerRunnable, 0);
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

        reLoadSensorsFromDataBase();
        createGridView();

        //start dat timer :)
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        SensorDialog sd = (SensorDialog) dialog;
        //check if it was successful
        if(((SensorDialog) dialog).isSuccess()) {
            long sensor_id = sd.getSensorID();
            int value = sd.getSensorIndex();
            for (int i = 0; i < sensors.size(); i++) {
                Sensor s = sensors.get(i);
                if (s.getId() == sensor_id) {
                    //update sensor
                    s.setIndex(value);
                    db = new DataBaseHandle(getApplicationContext());
                    db.updateSensor(s);


                    //update gauge data and gauge
                    GaugeData gs = sensor_gaugedatas.get(i);
                    gs.setValue(0.0);
                    gs.setValueMax(0.0);
                    gs.setValueMin(0.0);

                }
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server, menu);
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
        if (id == R.id.action_add_sensor) {
            addSensor(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_v2);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                server_ip = null;
            } else {
                server_ip = extras.getString("ip");
            }
        } else {
            server_ip = (String) savedInstanceState.getSerializable("ip");
            setTitle("Server ip: " + server_ip);
        }

        //if screen rotated or shit... no intent is fired but we stored the ip in global context...
        if (server_ip == null) {
            server_ip = ((CustomApplication) getApplication()).getIP();
        }


        //database
        db = new DataBaseHandle(getApplicationContext());

        if (server_ip == null) {
            showToast("Server ip not given ...");
        } else {
            server = db.getServerByIP(server_ip);
            if (server != null) {
                showToast("Server found in DB");
                setTitle("Server ip: " + server_ip + " id: " + server.getId());
            }
        }

        networker = ((CustomApplication) getApplication()).getNetworker();

        reLoadSensorsFromDataBase();
        createGridView();



        //start dat timer :)
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        destroyed = true;
        if(networker!=null){
            networker.cancel();
        }
    }

    public void showToast(CharSequence cs) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, cs, duration);
        toast.show();
    }
}