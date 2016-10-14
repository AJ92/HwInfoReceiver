package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.threedevs.aj.HwInfoReceiver.CardLayout.LazyAdapter;
import com.threedevs.aj.HwInfoReceiver.CardLayout.RowItem;
import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Sensor;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Server;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;


public class ServerActivity extends ActionBarActivity implements SensorDialog.SensorDialogListener{


    //UI TEST
    private List<RowItem> rowItems;

    private Networker networker = null;
    private DataBaseHandle db;

    Server server = null;
    String server_ip = null;

    List<Sensor> sensors = new ArrayList<Sensor>();
    List<String> sensor_strings = new ArrayList<String>();

    List<CustomGauge> sensor_gauges = new ArrayList<CustomGauge>();
    List<GaugeData> sensor_gaugedatas = new ArrayList<GaugeData>();


    //keep track of all gauges...


    private String TAG = "ServerActivity";

    private TextView tv;

    float dpHeight = 0.0f;
    float dpWidth = 0.0f;
    float dpDensitiy = 0.0f;

    float viewHeight = 0;
    float viewWidth = 0;

    int tempID = 0;

    private long timer_delay = 2000;

    private LazyAdapter adapter;

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
                                updateSensorData(id, msg);
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
        //setContentView(R.layout.activity_server);


        LayoutInflater inf = getLayoutInflater();
        RelativeLayout view = (RelativeLayout) inf.inflate(R.layout.activity_server, null);
        setContentView(view);


        //get screen dimensions in dp
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        dpDensitiy = displayMetrics.density;

        viewHeight = view.getHeight();
        viewWidth = view.getWidth();




        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                server_ip= null;
            } else {
                server_ip= extras.getString("ip");
            }
        } else {
            server_ip= (String) savedInstanceState.getSerializable("ip");
            setTitle("Server ip: " + server_ip);
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
                setTitle("Server ip: " + server_ip + " id: " + server.getId());
            }
        }

        networker = ((CustomApplication)getApplication()).getNetworker();

        reLoadSensorsFromDataBase();

        //build layout
        buildLayout();

        //start dat timer :)
        timerHandler.postDelayed(timerRunnable, 0);
    }


    //build / rebuild the layout!
    private void buildLayout(){

        GridLayout gl = (GridLayout) findViewById(R.id.gridLayout);

        if(gl != null){
            showToast("bulding GridLayout");
            gl.removeAllViews();


            rowItems = new ArrayList<RowItem>();

            //add all gauges...
            for(GaugeData gd : sensor_gaugedatas){


                RowItem item = new RowItem((float) gd.getValueMin(),(float) gd.getValueMax(),(float) gd.getValue(),gd.getUnit(),gd.getName(),gd.getPrecision());
                item.setAttrib1(gd.getValueMin() + " " + gd.getUnit());
                item.setAttrib2(gd.getValue() + " " + gd.getUnit());
                item.setAttrib3(gd.getValueMax() + " " + gd.getUnit());

                rowItems.add(item);
            }


            ListView lv = (ListView) findViewById(R.id.myList);

            // Set the adapter on the ListView
            adapter = new LazyAdapter(getApplicationContext(), R.layout.list_row, rowItems);
            lv.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

            //add Spaces... ???
            //not yet...


            registerForContextMenu(lv);

        }



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
        if (v.getId()==R.id.myList) {
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


    public void reLoadSensorsFromDataBase(){
        sensors = new ArrayList<Sensor>();
        sensor_strings = new ArrayList<String>();
        sensor_gauges = new ArrayList<CustomGauge>();
        sensor_gaugedatas = new ArrayList<GaugeData>();

        //database
        db = new DataBaseHandle(getApplicationContext());

        if(server != null) {
            sensors = db.getAllSensorsByServer(server);
            showToast( sensors.size() + " sensors found...");
        }

        for(int i = 0; i < sensors.size(); i++){
            sensor_strings.add(Long.toString(sensors.get(i).getIndex()));
            sensor_gaugedatas.add(new GaugeData());
        }
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
            Sensor sensor = new Sensor(tempID);
            long sensor_id = db.createSensor(sensor);

            db.createServerSensor(server.getId(), sensor_id);
        }

        reLoadSensorsFromDataBase();

        buildLayout();
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

        buildLayout();
    }


    private void updateSensorData(long index, String message){
        for(int i = 0; i < sensors.size(); i++){
            if(sensors.get(i).getIndex() == index){

                //split the message:
                String[] split_message = message.split("::");

                if(split_message.length > 0) {

                    if(split_message[0].equals("reading")) {

                        //update string
                        String value = "";
                        value = value + split_message[2] + ": " + split_message[3] + " " + split_message[4];
                        sensor_strings.set(i, value);

                        //update GaugeData
                        GaugeData gd = sensor_gaugedatas.get(i);
                        gd.setName(split_message[2]);
                        gd.setUnit(split_message[4]);
                        try {
                            double value_double = Double.parseDouble(split_message[3]);
                            gd.setValue(value_double);
                        }
                        catch (Exception e){
                            return;
                        }

                        //update Gauge


                        RowItem item = rowItems.get(i);

                        item.setMaxValue((float) gd.getValueMax());
                        item.setMinValue((float) gd.getValueMin());
                        item.setCurrentValue((float) gd.getValue());

                        item.setUnit(gd.getUnit());
                        item.setTitle(gd.getName());

                        int precision = gd.getPrecision();

                        item.setAttrib1("Min: " + format("%." + precision + "f", gd.getValueMin()));
                        item.setAttrib2("Cur: " + format("%." + precision + "f", gd.getValue()));
                        item.setAttrib3("Max: " + format("%." + precision + "f", gd.getValueMax()));

                        /*
                        CustomGauge g = item.getGauge();
                        if(g!=null){
                            g.setMaxValue((float) gd.getValueMax());
                            g.setMinValue((float) gd.getValueMin());
                            g.setValue((float) gd.getValue());

                            g.setTitle(gd.getUnit());
                        }
                        */
                    }
                }

            }
        }

        adapter.notifyDataSetChanged();
    }


    public void showNoticeDialog(Sensor sensor) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SensorDialog(sensor.getId(), (int) sensor.getIndex());
        dialog.show(getSupportFragmentManager(), "SensorDialog");
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
}
