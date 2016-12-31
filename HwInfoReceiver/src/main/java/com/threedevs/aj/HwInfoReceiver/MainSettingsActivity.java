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

        if(use_dark_theme) {
            setTheme(R.style.AppTheme_Dark);
        }
        else {
            setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        CheckBox checkBoxUseDarkTheme = (CheckBox) this.findViewById(R.id.checkBoxUseDarkTheme);
        Button buttonWipeDB = (Button) this.findViewById(R.id.buttonWipeDB);

        if(use_dark_theme){
            checkBoxUseDarkTheme.setChecked(true);
        }
        else{
            checkBoxUseDarkTheme.setChecked(false);
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


        buttonWipeDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHandle db = new DataBaseHandle(getApplicationContext());
                db.drop();
            }
        });


    }
}
