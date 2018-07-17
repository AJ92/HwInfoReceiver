package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Sensor;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Server;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.format;

public class ServerActivityV2 extends AppCompatActivity{

    RecyclerView recyclerView;
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

    private static List<String>                sensor_hashes = new ArrayList<String>();
    private static HashMap<String, Integer>    sensor_hash_sensor_id = new HashMap<String, Integer>();
    private static HashMap<String, String>     sensor_hash_sensor_name = new HashMap<String, String>();

    //refresh time for server communication...
    private long timer_delay = 2000;

    private boolean destroyed = false;

    private int connections_failed = 0;
    private int max_connections_failed = 5;


    static final int APPLICATION_SETTINGS_REQUEST = 1;  // The request code


    private Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if(destroyed){
                return;
            }

            if(connections_failed  >=  max_connections_failed + 1) {
                return;
            }


            if(networker == null){
                Log.e(TAG, "networker == null");
                connections_failed += 1;

                if(connections_failed >= max_connections_failed) {
                    //show connection lost dialog...
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showConnectionLostDialog();
                        }
                    });
                    return;
                }
            }
            else{
                Log.e(TAG, "networker != null");
                if(!networker.isConnected()){
                    Log.e(TAG, "    !networker.isConnected()");
                    connections_failed += 1;

                    if(connections_failed >= max_connections_failed) {
                        //show connection lost dialog...
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConnectionLostDialog();
                            }
                        });
                        return;
                    }
                }
                else{
                    Log.e(TAG, "    connections_failed = 0");
                    connections_failed = 0;
                }
            }



            //well we want to communicate tho :)
            if (networker != null) {

                //simple ping...
                networker.SendDataToNetwork("ping::ping;");

                //request stuff
                for (int i = 0; i < sensors.size(); i++) {
                    networker.SendDataToNetwork("v2gd::" + sensors.get(i).getHash() + ";");
                }

                //read stuff
                List<String> messages = networker.getMessages();
                //split the messages and set up the sensor strings for this moment...
                //MESSAGE DISPATCHER
                for (int i = 0; i < messages.size(); i++) {

                    String msg = messages.get(i);
                    String[] message_split = msg.split("::");
                    if (message_split.length > 0) {

                        if (message_split[0].equals("v2sd")) {

                            try {
                                String sensor_hash = message_split[1];
                                String sensor_name = message_split[2];

                                if(!sensor_hashes.contains(sensor_hash)){
                                    sensor_hashes.add(sensor_hash);
                                }

                                sensor_hash_sensor_name.put(sensor_hash, sensor_name);

                                updateSensorData(sensor_hash, msg);
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

    private void updateSensorData(String hash, String message) {
        if(!sensor_hash_sensor_id.containsKey(hash)){
            return;
        }

        int sensor_index = sensor_hash_sensor_id.get(hash);

        Log.e(TAG,"updating sensor at index: " + sensor_index);


        //split the message:
        String[] split_message = message.split("::");

        if (split_message.length > 0) {

            if (split_message[0].equals("v2sd")) {

                Log.e(TAG,"updating gauge at index: " + sensor_index);
                Log.e(TAG,"updating gauge name: " + split_message[2]);

                //update GaugeData
                GaugeData gd = sensor_gaugedatas.get(sensor_index);
                gd.setName(split_message[2]);
                gd.setUnit(split_message[4]);
                try {
                    double value_double = Double.parseDouble(split_message[3]);
                    gd.setValue(value_double);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                //update Gauge
                //gd.updateViews();

            }
        }

        //gaugeAdapter.notifyDataSetChanged();
        //gaugeAdapter.notifyDataSetInvalidated();
        //recyclerView.invalidateViews();
        //recyclerView.setAdapter(gaugeAdapter);
    }

    public void reLoadSensorsFromDataBase(){
        sensors = new ArrayList<Sensor>();
        sensor_gaugedatas = new ArrayList<GaugeData>();

        //database
        db = new DataBaseHandle(getApplicationContext());

        if(server != null) {
            sensors = db.getAllSensorsByServer(server);
            for(int i = 0; i < sensors.size(); i++) {
                Log.i(TAG, "sensor id: " + sensors.get(i).getId() + "  sensor hash: " + sensors.get(i).getHash());
            }
            Log.i(TAG, sensors.size() + " sensors found...");
            showToast( sensors.size() + " sensors found...");
        }

        //sensor_hash_sensor_id.clear();
        //sensor_hash_sensor_name.clear();

        for(int i = 0; i < sensors.size(); i++){
            //construct GaugeData and CustomGauge
            GaugeData gd = new GaugeData();
            //gd.setName("hash: " + sensors.get(i).getHash());
            gd.setName("Loading...");
            sensor_gaugedatas.add(gd);

            sensor_hash_sensor_id.put(sensors.get(i).getHash(), i);
            sensor_hash_sensor_name.put(sensors.get(i).getHash(), "hash: " + sensors.get(i).getHash());
            //sensor_hash_sensor_name.put(sensors.get(i).getHash(), "Loading...");
        }
    }

    public void createGridView(){
        //init test gaugesDatas...
        final GaugeData gaugeDatas[] = new GaugeData[sensor_gaugedatas.size()];
        for (int i = 0; i < sensor_gaugedatas.size(); i++) {
            gaugeDatas[i] = sensor_gaugedatas.get(i);
        }

        recyclerView = (RecyclerView) findViewById(R.id.activity_server_v2);

        gaugeAdapter = new GaugeAdapter(this, gaugeDatas);
        gaugeAdapter.setOnCreateContextMenuListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gaugeAdapter);


    }


    private int selectedRecylcerViewItemPosition = 0;

    //ListViews context Menu...
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        selectedRecylcerViewItemPosition = recyclerView.getChildAdapterPosition(v);
        if(RecyclerView.NO_POSITION == selectedRecylcerViewItemPosition){
            return;
        }
        menu.setHeaderTitle("Sensor: " + sensors.get(selectedRecylcerViewItemPosition).getId());
        String[] menuItems = getResources().getStringArray(R.array.sensor_list_menu);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(RecyclerView.NO_POSITION == selectedRecylcerViewItemPosition){
            return false;
        }

        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.sensor_list_menu);
        String menuItemName = menuItems[menuItemIndex];

        int listItemIndex = selectedRecylcerViewItemPosition;

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

    public void showNoticeDialog(final Sensor sensor) {
        // Create an instance of the dialog fragment and show it

        //get all sensors...
        final ArrayList<String> sensor_name_list = new ArrayList<String>();


        for(int i = 0; i < sensor_hashes.size(); i++){
            sensor_name_list.add(sensor_hash_sensor_name.get(sensor_hashes.get(i)));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ServerActivityV2.this);
        builder.setTitle("Select Option");
        builder.setItems(sensor_name_list.toArray(new CharSequence[sensor_name_list.size()]), new DialogInterface.OnClickListener() {
                @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("value is", "" + which);

                //update sensor
                sensor.setHash(sensor_hashes.get(which));
                db = new DataBaseHandle(getApplicationContext());
                int updated = db.updateSensor(sensor);

                Log.i(TAG, "updated sensor : " + updated);



                //update gauge data and gauge
                for(int i = 0; i < sensors.size(); i++) {
                    if(sensors.get(i).getId() == sensor.getId()) {
                        sensor_hash_sensor_id.put(sensor_hashes.get(which), i);

                        GaugeData gs = sensor_gaugedatas.get(i);
                        gs.setValue(0.0);
                        gs.setValueMax(0.0);
                        gs.setValueMin(0.0);
                    }
                }
            }
        });
        builder.show();

    }

    public void addSensor(View v){
        if(server != null){
            //database
            db = new DataBaseHandle(getApplicationContext());
            Sensor sensor = new Sensor("0");
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

        if (id == R.id.action_help) {
            Intent si = new Intent(ServerActivityV2.this, IntroSensorsActivity.class);
            ServerActivityV2.this.startActivity(si);
            return true;
        }
        if (id == R.id.action_add_sensor) {
            addSensor(null);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent si = new Intent(ServerActivityV2.this, MainSettingsActivity.class);
            ServerActivityV2.this.startActivityForResult(si, APPLICATION_SETTINGS_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        boolean use_dark_theme = sharedPref.getBoolean(getString(R.string.setting_use_dark_theme_pref), false);
        boolean keep_screen_on = sharedPref.getBoolean(getString(R.string.setting_keep_screen_on_pref), false);


        if(use_dark_theme) {
            setTheme(R.style.AppTheme_Dark);
        }
        else {
            setTheme(R.style.AppTheme);
        }


        if(keep_screen_on){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_v2);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                server_ip = null;
            } else {
                server_ip = extras.getString("ip");
            }


            sensor_hashes.clear();
            sensor_hash_sensor_id.clear();
            sensor_hash_sensor_name.clear();

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
                setTitle(server_ip + " id: " + server.getId());
            }
        }

        networker = ((CustomApplication) getApplication()).getNetworker();


        recyclerView = (RecyclerView) findViewById(R.id.activity_server_v2);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));


        reLoadSensorsFromDataBase();
        createGridView();

        //start dat timer :)
        //give it some time to connect...
        timerHandler.postDelayed(timerRunnable, 0);


        if(server_ip != null){
            // Restore preferences
            SharedPreferences settings = getSharedPreferences(getString(R.string.shared_pref_key), 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(getString(R.string.setting_last_server_pref), server_ip);

            // Commit the edits!
            editor.commit();

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        destroyed = true;
        //if(networker!=null){
        //    networker.cancel();
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == APPLICATION_SETTINGS_REQUEST) {
            reLoadSensorsFromDataBase();
            createGridView();
        }
    }

    public void showToast(CharSequence cs) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, cs, duration);
        toast.show();
    }

    public void showConnectionLostDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.connection_lost_title)
                .setMessage(R.string.connection_lost_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        destroyed = true;
                        ServerActivityV2.this.finish();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setCancelable(false)
                .show();
    }




    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}