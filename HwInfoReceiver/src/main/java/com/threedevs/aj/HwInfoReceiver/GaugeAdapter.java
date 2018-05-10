package com.threedevs.aj.HwInfoReceiver;

/**
 * Created by AJ on 17.10.2016.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GaugeAdapter extends BaseAdapter {
        private Context context;
        private final GaugeData[] gaugeDatas;

        public GaugeAdapter(Context context, GaugeData[] gaugeDatas) {
            this.context = context;
            this.gaugeDatas = gaugeDatas;
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