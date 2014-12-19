package com.threedevs.aj.HwInfoReceiver.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.threedevs.aj.HwInfoReceiver.Database.Objects.Sensor;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Server;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJ on 14.06.2014.
 */
public class DataBaseHandle extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHandle";

    private static final String DATABASE_NAME = "serverManager";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_SERVERS = "servers";
    private static final String TABLE_SETTINGS = "settings";
    private static final String TABLE_SENSORS = "sensors";
    private static final String TABLE_SERVER_SENSOR = "server_sensor";
    private static final String TABLE_SENSOR_SETTING = "sensor_setting";

    //Keys

    //common
    private static final String KEY_ID = "id";

    //Servers
    private static final String KEY_IP = "ip";
    private static final String KEY_HOSTNAME = "hostname";

    //Settings
    private static final String KEY_SETTING = "setting";
    private static final String KEY_VALUE = "value";

    //Sensors
    private static final String KEY_INDEX = "sensorindex";


    //Server - Sensor - Setting
    private static final String KEY_SERVERID = "server_id";
    private static final String KEY_SENSORID = "sensor_id";
    private static final String KEY_SETTINGID = "setting_id";


    //Table creation...
    private static final String CREATE_TABLE_SERVERS = "CREATE TABLE " +
            TABLE_SERVERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IP + " TEXT," +
            KEY_HOSTNAME + " TEXT" + ")";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE " +
            TABLE_SETTINGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SETTING + " TEXT," +
            KEY_VALUE + " TEXT" + ")";

    private static final String CREATE_TABLE_SENSORS = "CREATE TABLE " +
            TABLE_SENSORS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INDEX + " TEXT" + ")";

    private static final String CREATE_TABLE_SERVER_SENSOR = "CREATE TABLE " +
            TABLE_SERVER_SENSOR + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SERVERID + " INTEGER," +
            KEY_SENSORID + " INTEGER" + ")";

    private static final String CREATE_TABLE_SENSOR_SETTING = "CREATE TABLE " +
            TABLE_SENSOR_SETTING + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SENSORID + " INTEGER," +
            KEY_SETTINGID + " INTEGER" + ")";



    public DataBaseHandle(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG,"onCreate");

        //drop if failed during creation ...
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER_SENSOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR_SETTING);
        //end drop


        sqLiteDatabase.execSQL(CREATE_TABLE_SERVERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SETTINGS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SENSORS);

        sqLiteDatabase.execSQL(CREATE_TABLE_SERVER_SENSOR);
        sqLiteDatabase.execSQL(CREATE_TABLE_SENSOR_SETTING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.i(TAG,"onUpgrade");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER_SENSOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR_SETTING);

        onCreate(sqLiteDatabase);
    }


    public void drop(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Log.i(TAG,"drop dat db like its hot...");

        onCreate(sqLiteDatabase);
    }

    //ops on database (insert, delete, update, bla bla ...)

    //CREATE
    public long createServer(Server server) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP, server.getIp());
        values.put(KEY_HOSTNAME, server.getHostname());


        // insert row
        long server_id = db.insert(TABLE_SERVERS, null, values);

        return server_id;
    }


    public long createSensor(Sensor sensor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INDEX, sensor.getIndex());

        // insert row
        long sensor_id = db.insert(TABLE_SENSORS, null, values);

        return sensor_id;
    }

    public long createSetting(Setting setting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SETTING, setting.getSetting());
        values.put(KEY_VALUE, setting.getValue());

        // insert row
        long setting_id = db.insert(TABLE_SETTINGS, null, values);

        return setting_id;
    }

    public long createServerSensor(long server_id, long sensor_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SERVERID, server_id);
        values.put(KEY_SENSORID, sensor_id);

        // insert row
        long server_sensor_id = db.insert(TABLE_SERVER_SENSOR, null, values);

        return server_sensor_id;
    }

    public long createSensorSetting(long sensor_id, long setting_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENSORID, sensor_id);
        values.put(KEY_SETTINGID, setting_id);

        // insert row
        long sensor_setting_id = db.insert(TABLE_SENSOR_SETTING, null, values);

        return sensor_setting_id;
    }


    //UPDATE
    public int updateServer(Server server) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP, server.getIp());
        values.put(KEY_HOSTNAME, server.getHostname());

        // updating row
        return db.update(TABLE_SERVERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(server.getId()) });
    }

    public int updateSensor(Sensor sensor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INDEX, sensor.getIndex());

        // updating row
        return db.update(TABLE_SENSORS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(sensor.getId()) });
    }

    public int updateSetting(Setting setting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SETTING, setting.getSetting());
        values.put(KEY_VALUE, setting.getValue());

        // updating row
        return db.update(TABLE_SETTINGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(setting.getId()) });
    }



    //DELETE
    public void deleteServer(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVERS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public void deleteSensor(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSORS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public void deleteSetting(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public void deleteServerSensor(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVER_SENSOR, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public void deleteSensorSetting(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSOR_SETTING, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }




    //getLists
    public List<Server> getAllServers() {
        List<Server> servers = new ArrayList<Server>();

        String selectQuery = "SELECT  * FROM " + TABLE_SERVERS;

        Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Server server = new Server(c.getLong((c.getColumnIndex(KEY_ID))),
                        c.getString((c.getColumnIndex(KEY_IP))),
                        c.getString((c.getColumnIndex(KEY_HOSTNAME))));

                // adding to sensor list
                servers.add(server);
            } while (c.moveToNext());
        }
        return servers;
    }

    public List<Sensor> getAllSensorsByServer(Server server) {
        List<Sensor> sensors = new ArrayList<Sensor>();

        String selectQuery = "SELECT  * FROM " + TABLE_SERVERS + " tservers, "
                + TABLE_SENSORS + " tsensors, " + TABLE_SERVER_SENSOR + " tserversensor WHERE tservers."
                + KEY_ID + " = '" + String.valueOf(server.getId()) + "'" + " AND tservers." + KEY_ID
                + " = " + "tserversensor." + KEY_SERVERID + " AND tsensors." + KEY_ID + " = "
                + "tserversensor." + KEY_SENSORID;

        Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Sensor sensor = new Sensor();
                sensor.setId(c.getLong((c.getColumnIndex(KEY_ID))));
                sensor.setIndex((c.getLong(c.getColumnIndex(KEY_INDEX))));

                // adding to sensor list
                sensors.add(sensor);
            } while (c.moveToNext());
        }
        Log.i(TAG, "found " + sensors.size() + " entries." );
        return sensors;
    }

    public List<Setting> getAllSettingsBySensor(Sensor sensor) {
        List<Setting> settings = new ArrayList<Setting>();

        String selectQuery = "SELECT  * FROM " + TABLE_SENSORS + " tsensors, "
                + TABLE_SETTINGS + " tsettings, " + TABLE_SENSOR_SETTING + " tsensorsetting WHERE tsensors."
                + KEY_ID + " = '" + String.valueOf(sensor.getId()) + "'" + " AND tsensors." + KEY_ID
                + " = " + "tsensorsetting." + KEY_SENSORID + " AND tsettings." + KEY_ID + " = "
                + "tsensorsetting." + KEY_SETTINGID;

        Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Setting setting = new Setting();
                setting.setId(c.getLong((c.getColumnIndex(KEY_ID))));
                setting.setSetting((c.getString(c.getColumnIndex(KEY_SETTING))));
                setting.setValue((c.getString(c.getColumnIndex(KEY_VALUE))));

                // adding to sensor list
                settings.add(setting);
            } while (c.moveToNext());
        }
        return settings;
    }


    public Server getServerByIP(String ip) {
        Server server = null;

        String selectQuery = "SELECT  * FROM " + TABLE_SERVERS + " WHERE " + KEY_IP + " = '" + ip + "'";

        Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Server server_tmp = new Server(c.getLong((c.getColumnIndex(KEY_ID))),
                        c.getString((c.getColumnIndex(KEY_IP))),
                        c.getString((c.getColumnIndex(KEY_HOSTNAME))));

                // adding to sensor list
                server = server_tmp;
            } while (c.moveToNext());
        }
        return server;
    }

    public long getServerSensorIDBySensorID(long id) {
        long serversensorid = -1;

        String selectQuery = "SELECT " + KEY_ID +  " FROM " + TABLE_SERVER_SENSOR + " WHERE " + KEY_SENSORID + " = '" + id + "'";

        Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                serversensorid = c.getLong((c.getColumnIndex(KEY_ID)));
            } while (c.moveToNext());
        }
        return serversensorid;
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
