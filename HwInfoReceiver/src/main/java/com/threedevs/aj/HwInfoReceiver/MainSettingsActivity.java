package com.threedevs.aj.HwInfoReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.threedevs.aj.HwInfoReceiver.Database.DataBaseHandle;


public class MainSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

        boolean use_dark_theme = sharedPref.getBoolean(getString(R.string.setting_use_dark_theme_pref), false);
        boolean keep_screen_on = sharedPref.getBoolean(getString(R.string.setting_keep_screen_on_pref), false);
        boolean auto_connect_last = sharedPref.getBoolean(getString(R.string.setting_auto_connect_last_server_pref), false);

        boolean custom_font_scale = sharedPref.getBoolean(getString(R.string.setting_custom_font_scale_pref), false);
        int custom_font_scale_value = sharedPref.getInt(getString(R.string.setting_custom_font_scale_value_pref), 100);

        boolean custom_temp_scale = sharedPref.getBoolean(getString(R.string.setting_custom_temp_scale_pref), false);
        int custom_temp_scale_min = sharedPref.getInt(getString(R.string.setting_custom_temp_scale_min_pref), 0);
        int custom_temp_scale_max = sharedPref.getInt(getString(R.string.setting_custom_temp_scale_max_pref), 100);


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

        CheckBox checkBoxCustomFontScale = (CheckBox) this.findViewById(R.id.checkBoxCustomFontScale);
        Button buttonCustomFontScaleSmaller = (Button) this.findViewById(R.id.buttonCustomFontScaleSmaller);
        Button buttonCustomFontScaleLarger = (Button) this.findViewById(R.id.buttonCustomFontScaleLarger);
        TextView textViewCustomFontScale = (TextView) this.findViewById(R.id.textViewCustomFontScale);

        CheckBox checkBoxCustomTemperatureScale = (CheckBox) this.findViewById(R.id.checkBoxCustomTemperatureScale);
        EditText editTextCustomTemperatureScaleMin = (EditText) this.findViewById(R.id.editTextCustomTemperatureScaleMin);
        EditText editTextCustomTemperatureScaleMax = (EditText) this.findViewById(R.id.editTextCustomTemperatureScaleMax);


        Button buttonWipeDB = (Button) this.findViewById(R.id.buttonWipeDB);

        //set values
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

        if(custom_font_scale){
            checkBoxCustomFontScale.setChecked(true);
            buttonCustomFontScaleSmaller.setEnabled(true);
            buttonCustomFontScaleLarger.setEnabled(true);
            textViewCustomFontScale.setText(custom_font_scale_value + "%");
        }
        else{
            checkBoxCustomFontScale.setChecked(false);
            buttonCustomFontScaleSmaller.setEnabled(false);
            buttonCustomFontScaleLarger.setEnabled(false);
            textViewCustomFontScale.setText("100%");
        }

        if(custom_temp_scale){
            checkBoxCustomTemperatureScale.setChecked(true);
            editTextCustomTemperatureScaleMin.setEnabled(true);
            editTextCustomTemperatureScaleMax.setEnabled(true);
            editTextCustomTemperatureScaleMin.setText(custom_temp_scale_min + "");
            editTextCustomTemperatureScaleMax.setText(custom_temp_scale_max + "");
        }
        else{
            checkBoxCustomTemperatureScale.setChecked(false);
            editTextCustomTemperatureScaleMin.setEnabled(false);
            editTextCustomTemperatureScaleMax.setEnabled(false);
            editTextCustomTemperatureScaleMin.setText("0");
            editTextCustomTemperatureScaleMax.setText("100");
        }

        //response on edit of values
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


        checkBoxCustomFontScale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean checked = false;
                if(compoundButton.isChecked()){
                    checked = true;
                }
                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.setting_custom_font_scale_pref), checked);
                editor.commit();

                //enable/disable related gui elements
                Button buttonCustomFontScaleSmaller = (Button) MainSettingsActivity.this.findViewById(R.id.buttonCustomFontScaleSmaller);
                Button buttonCustomFontScaleLarger = (Button) MainSettingsActivity.this.findViewById(R.id.buttonCustomFontScaleLarger);
                buttonCustomFontScaleSmaller.setEnabled(checked);
                buttonCustomFontScaleLarger.setEnabled(checked);

                //set preview...
                TextView textViewCustomFontScale = (TextView) MainSettingsActivity.this.findViewById(R.id.textViewCustomFontScale);
                int custom_font_scale_value = sharedPref.getInt(getString(R.string.setting_custom_font_scale_value_pref), 100);
                if(checked) {
                    textViewCustomFontScale.setText(custom_font_scale_value + "%");
                }
                else{ //default
                    textViewCustomFontScale.setText("100%");
                }
            }
        });

        checkBoxCustomTemperatureScale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean checked = false;
                if(compoundButton.isChecked()){
                    checked = true;
                }
                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.setting_custom_temp_scale_pref), checked);
                editor.commit();

                //enable/disable related gui elements...
                EditText editTextCustomTemperatureScaleMin = (EditText) MainSettingsActivity.this.findViewById(R.id.editTextCustomTemperatureScaleMin);
                EditText editTextCustomTemperatureScaleMax = (EditText) MainSettingsActivity.this.findViewById(R.id.editTextCustomTemperatureScaleMax);
                editTextCustomTemperatureScaleMin.setEnabled(checked);
                editTextCustomTemperatureScaleMax.setEnabled(checked);

                int custom_temp_scale_min = sharedPref.getInt(getString(R.string.setting_custom_temp_scale_min_pref), 0);
                int custom_temp_scale_max = sharedPref.getInt(getString(R.string.setting_custom_temp_scale_max_pref), 100);
                if(checked){
                    editTextCustomTemperatureScaleMin.setText(custom_temp_scale_min + "");
                    editTextCustomTemperatureScaleMax.setText(custom_temp_scale_max + "");
                }
                else{
                    editTextCustomTemperatureScaleMin.setText("0");
                    editTextCustomTemperatureScaleMax.setText("100");
                }
            }
        });

        buttonCustomFontScaleSmaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                int custom_font_scale_value = sharedPref.getInt(getString(R.string.setting_custom_font_scale_value_pref), 100);
                if(custom_font_scale_value <= 80){
                    return;
                }
                custom_font_scale_value -= 1;
                editor.putInt(getString(R.string.setting_custom_font_scale_value_pref), custom_font_scale_value);
                editor.commit();

                TextView textViewCustomFontScale = (TextView) MainSettingsActivity.this.findViewById(R.id.textViewCustomFontScale);
                textViewCustomFontScale.setText(custom_font_scale_value + "%");
            }
        });
        buttonCustomFontScaleLarger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                int custom_font_scale_value = sharedPref.getInt(getString(R.string.setting_custom_font_scale_value_pref), 100);
                if(custom_font_scale_value >= 120){
                    return;
                }
                custom_font_scale_value += 1;
                editor.putInt(getString(R.string.setting_custom_font_scale_value_pref), custom_font_scale_value);
                editor.commit();

                TextView textViewCustomFontScale = (TextView) MainSettingsActivity.this.findViewById(R.id.textViewCustomFontScale);
                textViewCustomFontScale.setText(custom_font_scale_value + "%");
            }
        });

        editTextCustomTemperatureScaleMin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    EditText editTextCustomTemperatureScaleMin = (EditText) MainSettingsActivity.this.findViewById(R.id.editTextCustomTemperatureScaleMin);
                    editTextCustomTemperatureScaleMin.clearFocus();

                    //hide keyboard
                    View view = MainSettingsActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        editTextCustomTemperatureScaleMax.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    EditText editTextCustomTemperatureScaleMax = (EditText) MainSettingsActivity.this.findViewById(R.id.editTextCustomTemperatureScaleMax);
                    editTextCustomTemperatureScaleMax.clearFocus();

                    //hide keyboard
                    View view = MainSettingsActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        editTextCustomTemperatureScaleMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                * has valid values.
                */
                if (!hasFocus) {
                    EditText et = (EditText) v;

                    int value_min = 0;
                    try{
                        value_min = Integer.valueOf(et.getText().toString());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    EditText editTextCustomTemperatureScaleMax = (EditText) MainSettingsActivity.this.findViewById(R.id.editTextCustomTemperatureScaleMax);
                    int value_max = 0;
                    try{
                        value_max = Integer.valueOf(editTextCustomTemperatureScaleMax.getText().toString());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    if(value_min > value_max){
                        value_min = value_max;
                    }

                    et.setText(value_min+"");

                    //save the value
                    SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                            getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putInt(getString(R.string.setting_custom_temp_scale_min_pref), value_min);
                    editor.commit();
                }
            }
        });

        editTextCustomTemperatureScaleMax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                * has valid values.
                */
                if (!hasFocus) {
                    EditText et = (EditText) v;

                    int value_max = 0;
                    try{
                        value_max = Integer.valueOf(et.getText().toString());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    EditText editTextCustomTemperatureScaleMin = (EditText) MainSettingsActivity.this.findViewById(R.id.editTextCustomTemperatureScaleMin);
                    int value_min = 0;
                    try{
                        value_min = Integer.valueOf(editTextCustomTemperatureScaleMin.getText().toString());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    if(value_max < value_min){
                        value_max = value_min;
                    }

                    et.setText(value_max+"");

                    //save the value
                    SharedPreferences sharedPref = MainSettingsActivity.this.getSharedPreferences(
                            getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putInt(getString(R.string.setting_custom_temp_scale_max_pref), value_max);
                    editor.commit();
                }
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
