package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;


public class MainSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        boolean use_dark_theme = sharedPref.getBoolean(getString(R.string.setting_use_dark_theme_pref), false);
        boolean keep_screen_on = sharedPref.getBoolean(getString(R.string.setting_keep_screen_on_pref), false);
        boolean auto_connect_last = sharedPref.getBoolean(getString(R.string.setting_auto_connect_last_server_pref), false);


        if(use_dark_theme) {
            setTheme(R.style.AppTheme_Dark);
        }
        else {
            setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        CheckBox checkBoxUseDarkTheme = (CheckBox) this.findViewById(R.id.checkBoxUseDarkTheme);
        CheckBox checkBoxKeepScreenOn = (CheckBox) this.findViewById(R.id.checkBoxKeepScreenOn);
        CheckBox checkBoxAutoConnectLastServer = (CheckBox) this.findViewById(R.id.checkBoxAutoConnectLastServer);
        Button buttonWipeDB = (Button) this.findViewById(R.id.buttonWipeDB);

        if(use_dark_theme){
            checkBoxUseDarkTheme.setChecked(true);
        }
        else{
            checkBoxUseDarkTheme.setChecked(false);
        }

        if(keep_screen_on){
            checkBoxKeepScreenOn.setChecked(true);
        }
        else{
            checkBoxKeepScreenOn.setChecked(false);
        }

        if(auto_connect_last){
            checkBoxAutoConnectLastServer.setChecked(true);
        }
        else{
            checkBoxAutoConnectLastServer.setChecked(false);
        }

        checkBoxUseDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                boolean checked = false;
                if(compoundButton.isChecked()){
                    checked = true;
                }

                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean(getString(R.string.setting_use_dark_theme_pref), checked);
                editor.commit();

                //reload this activity...
                Intent si = new Intent(MainSettingsActivity.this, MainSettingsActivity.class);
                MainSettingsActivity.this.startActivity(si);

                finish();
            }
        });

        checkBoxKeepScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                boolean checked = false;
                if(compoundButton.isChecked()){
                    checked = true;
                }

                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean(getString(R.string.setting_keep_screen_on_pref), checked);
                editor.commit();
                
            }
        });

        checkBoxAutoConnectLastServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                boolean checked = false;
                if(compoundButton.isChecked()){
                    checked = true;
                }

                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean(getString(R.string.setting_auto_connect_last_server_pref), checked);
                editor.commit();

            }
        });

        buttonWipeDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHandle db = new DataBaseHandle(getApplicationContext());
                db.drop();
            }
        });


    }
}
