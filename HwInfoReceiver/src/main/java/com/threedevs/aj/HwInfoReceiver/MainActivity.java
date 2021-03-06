package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;
import com.threedevs.aj.HwInfoReceiver.Database.Objects.Server;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

import java.util.ArrayList;
import java.util.List;

import static com.threedevs.aj.HwInfoReceiver.R.id;
import static com.threedevs.aj.HwInfoReceiver.R.layout;
import static com.threedevs.aj.HwInfoReceiver.R.menu;


public class MainActivity extends AppCompatActivity implements ServerDialog.ServerDialogListener {

    private Networker networker = null;
    private DataBaseHandle db;
    private List<String> ip_list;
    private List<String> hostname_list;
    private List<Server> server_list;

    private EditText et;
    private ListView lv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        boolean use_dark_theme = sharedPref.getBoolean(getString(R.string.setting_use_dark_theme_pref), false);
        boolean auto_connect_last = sharedPref.getBoolean(getString(R.string.setting_auto_connect_last_server_pref), false);
        String last_server = sharedPref.getString(getString(R.string.setting_last_server_pref), "");

        if(use_dark_theme) {
            setTheme(R.style.AppTheme_Dark);
        }
        else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        et = (EditText) this.findViewById(id.editText);
        //we do not check if it is an ip ... we asume the user is smart enough to use one !
        //String ip = et.getText().toString();

        lv = (ListView) this.findViewById(id.listView);

        refillList();

        /*
        Server server1 = new Server("192.168.2.107");
        db.createServer(server1);
        */

        // get the networker...
        networker = ((CustomApplication)getApplication()).getNetworker();

        //first run?
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(CustomApplication.PREFS_NAME, 0);
        boolean first_run = settings.getBoolean("first_run", true);
        int desktop_app_version = settings.getInt("desktop_app_version", 0);
        boolean app_update_2 = settings.getBoolean("app_update_2", true);

        if(first_run == true) {
            Intent si = new Intent(MainActivity.this, IntroActivity.class);
            MainActivity.this.startActivity(si);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("first_run", false);

            // Commit the edits!
            editor.commit();
        }

        if(desktop_app_version <= 0) {
            Intent si = new Intent(MainActivity.this, IntroUpdateAppActivity.class);
            MainActivity.this.startActivity(si);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("desktop_app_version", 1);

            // Commit the edits!
            editor.commit();
        }

        if(app_update_2 == true) {
            Intent si = new Intent(MainActivity.this, IntroUpdate2Activity.class);
            MainActivity.this.startActivity(si);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("app_update_2", false);

            // Commit the edits!
            editor.commit();
        }


        if(auto_connect_last){
            if(ip_list.size() > 0){
                if(ip_list.contains(last_server)){
                    connectToServer(ip_list.indexOf(last_server));
                }
            }
        }
    }


    //ListViews context Menu...
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(ip_list.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.ip_list_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.ip_list_menu);
        String menuItemName = menuItems[menuItemIndex];

        int listItemIndex = info.position;
        String listItemName = ip_list.get(listItemIndex);

        //Connect
        if(menuItemIndex == 0){
            connectToServer(listItemIndex);
        }
        //Delete
        else if(menuItemIndex == 1){
            removeFromList(listItemIndex);
        }

        return true;
    }

    public void refillList(){
        ip_list = new ArrayList<String>();
        hostname_list = new ArrayList<String>();

        //database
        db = new DataBaseHandle(getApplicationContext());

        server_list = db.getAllServers();

        for(int i = 0; i < server_list.size(); i++){
            ip_list.add(server_list.get(i).getIp());
            //showToast(servers.get(i).getIp());
        }

        ListAdapter listenAdapter = new ArrayAdapter(this, R.layout.simple_list_item_c, ip_list);
        lv.setAdapter(listenAdapter);
        registerForContextMenu(lv);
    }

    public void addToList(String ip){
        ip_list.add(ip);

        //put the stuff into the db here...
        Server server = new Server(ip);
        db.createServer(server);
        //db insert end...

        refillList();

        showToast("added");
    }

    public void removeFromList(int index){

        db.deleteServer(server_list.get(index).getId());

        refillList();

        showToast("removed");
    }


    public void connectToServer(int index){
        String ip = ip_list.get(index);
        //hostname_list.get(index);
        networker = ((CustomApplication)getApplication()).createNetworker(ip);

        ((CustomApplication)getApplication()).setIP(ip);

        Intent si = new Intent(MainActivity.this, ServerActivityV2.class);
        si.putExtra("ip", ip);
        MainActivity.this.startActivity(si);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_help) {
            Intent si = new Intent(MainActivity.this, IntroServerConnectionActivity.class);
            MainActivity.this.startActivity(si);
            return true;
        }

        if (id == R.id.action_add_server) {
            showNoticeDialog();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent si = new Intent(MainActivity.this, MainSettingsActivity.class);
            MainActivity.this.startActivity(si);
            return true;
        }

        //    dropdb(null);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void showPopupServerAdd(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menu.menu_server_add, popup.getMenu());
        popup.show();
    }

    public void showToast(CharSequence cs){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, cs, duration);
        toast.show();
    }

    //ADD button call
    public void addServerToList(View v) {
        //we do not check if it is an ip ... we asume the user is smart enough to use one !
        String ip = et.getText().toString();
        addToList(ip);
    }

    public void dropdb(View v){
        db = new DataBaseHandle(getApplicationContext());
        db.drop();
        refillList();
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ServerDialog();
        dialog.show(getSupportFragmentManager(), "ServerDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        ServerDialog sd = (ServerDialog) dialog;
        //check if it was successful
        if(((ServerDialog) dialog).isSuccess()) {
            addToList(sd.getAddress());
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
