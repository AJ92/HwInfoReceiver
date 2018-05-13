package com.threedevs.aj.HwInfoReceiver;

/**
 * Created by AJ on 17.10.2016.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;

public class GaugeAdapter extends BaseAdapter {
        private Context context;
        private final GaugeData[] gaugeDatas;
        private final boolean[] gaugeDataFontAdjusted;

        public GaugeAdapter(Context context, GaugeData[] gaugeDatas) {
            this.context = context;
            this.gaugeDatas = gaugeDatas;
            this.gaugeDataFontAdjusted = new boolean[gaugeDatas.length];
            Arrays.fill(gaugeDataFontAdjusted, false);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (convertView == null) {
                gridView = new View(context);
                gridView = inflater.inflate(R.layout.activity_server_grid_item, null);
            } else {
                gridView = (View) convertView;
            }

            //get some settings values
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.shared_pref_key),
                    Context.MODE_PRIVATE
            );
            boolean custom_font_scale = sharedPref.getBoolean(context.getString(R.string.setting_custom_font_scale_pref), false);
            int custom_font_scale_value = sharedPref.getInt(context.getString(R.string.setting_custom_font_scale_value_pref), 100);

            boolean custom_temp_scale = sharedPref.getBoolean(context.getString(R.string.setting_custom_temp_scale_pref), false);
            int custom_temp_scale_min = sharedPref.getInt(context.getString(R.string.setting_custom_temp_scale_min_pref), 0);
            int custom_temp_scale_max = sharedPref.getInt(context.getString(R.string.setting_custom_temp_scale_max_pref), 100);


            GaugeData gaugeData = gaugeDatas[position];

            // set value into textview
            TextView titleTextView = (TextView) gridView
                    .findViewById(R.id.gauge_text);

            TextView currentTextView = (TextView) gridView
                    .findViewById(R.id.gauge_text_current);

            TextView minTextView = (TextView) gridView
                    .findViewById(R.id.gauge_text_min);

            TextView maxTextView = (TextView) gridView
                    .findViewById(R.id.gauge_text_max);

            //gauge
            CustomGauge gauge = (CustomGauge) gridView
                    .findViewById(R.id.gauge);

            //setup font sizes
            if(custom_font_scale && !gaugeDataFontAdjusted[position]) {
                float font_scale = custom_font_scale_value / 100f;

                float titleTextViewTextSize = titleTextView.getTextSize();
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextViewTextSize * font_scale);

                float currentTextViewTextSize = currentTextView.getTextSize();
                currentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextViewTextSize * font_scale);

                float minTextViewTextSize = minTextView.getTextSize();
                minTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, minTextViewTextSize * font_scale);

                float maxTextViewTextSize = maxTextView.getTextSize();
                maxTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextViewTextSize * font_scale);
                gaugeDataFontAdjusted[position] = true;
            }



            String unit = gaugeData.getUnit();
            if(unit.equals("%")){
                gauge.setMinValue(0.0f);
                gauge.setMaxValue(100.0f);
            }
            else if(unit.equals("°C")){
                if(custom_temp_scale) {
                    gauge.setMinValue(custom_temp_scale_min);
                    gauge.setMaxValue(custom_temp_scale_max);
                }
                else {
                    gauge.setMinValue(0.0f);
                    gauge.setMaxValue(100.0f);
                    gaugeData.setAutoAdjustScale(true);
                }
            }
            else if(unit.equals("MB")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("V")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("MHz")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("Yes/No")){
                //gauge.setMinValue(0.0f);
                //gauge.setMaxValue(1.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("W")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("RPM")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("MB/s")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("% of TDP")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }
            else if(unit.equals("Gbps")){
                //gauge.setMinValue(0.0f);
                gaugeData.setAutoAdjustScale(true);
            }

            gaugeData.setGauge(gauge);
            gaugeData.setTitleTextView(titleTextView);
            gaugeData.setCurrentTextView(currentTextView);
            gaugeData.setMinTextView(minTextView);
            gaugeData.setMaxTextView(maxTextView);

            gaugeData.updateViews();

            return gridView;
        }

        @Override
        public int getCount() {
            return gaugeDatas.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }