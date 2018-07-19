package com.threedevs.aj.HwInfoReceiver;

/**
 * Created by AJ on 17.10.2016.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Arrays;

public class GaugeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private final GaugeData[] gaugeDatas;
    private final boolean[] gaugeDataFontAdjusted;
    private View.OnCreateContextMenuListener contextMenuListener;

    public class MyViewHolderGauge extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView currentTextView;
        public TextView minTextView;
        public TextView maxTextView;
        public CustomGauge gauge;
        public MyViewHolderGauge(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.gauge_text);
            currentTextView = (TextView) view.findViewById(R.id.gauge_text_current);
            minTextView = (TextView) view.findViewById(R.id.gauge_text_min);
            maxTextView = (TextView) view.findViewById(R.id.gauge_text_max);
            gauge = (CustomGauge) view.findViewById(R.id.gauge);

            if(contextMenuListener != null){
                view.setOnCreateContextMenuListener(contextMenuListener);
            }
        }
    }

    public class MyViewHolderGraph extends RecyclerView.ViewHolder {
        public LineChart graph;
        public MyViewHolderGraph(View view) {
            super(view);
            graph = (LineChart) view.findViewById(R.id.graph);

            if(contextMenuListener != null){
                view.setOnCreateContextMenuListener(contextMenuListener);
            }
        }
    }

    public GaugeAdapter(Context context, GaugeData[] gaugeDatas) {
        this.context = context;
        this.gaugeDatas = gaugeDatas;
        this.gaugeDataFontAdjusted = new boolean[gaugeDatas.length];
        Arrays.fill(gaugeDataFontAdjusted, false);
    }

    public void setOnCreateContextMenuListener(View.OnCreateContextMenuListener contextMenuListener){
        this.contextMenuListener = contextMenuListener;
    }

    @Override
    public int getItemCount() {
        return gaugeDatas.length;
    }

    @Override
    public int getItemViewType(int position) {
        return gaugeDatas[position].getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case GaugeData.TYPE_GAUGE: {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_server_grid_item_gauge, parent, false);
                return new MyViewHolderGauge(itemView);
            }
            case GaugeData.TYPE_GRAPH: {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_server_grid_item_graph, parent, false);
                return new MyViewHolderGraph(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder == null){
            return;
        }

        GaugeData gaugeData = gaugeDatas[position];

        String unit = gaugeData.getUnit();

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

        switch (holder.getItemViewType()) {
            case GaugeData.TYPE_GAUGE: {
                MyViewHolderGauge viewHolder = (MyViewHolderGauge) holder;
                //setup font sizes
                if (custom_font_scale && !gaugeDataFontAdjusted[position]) {
                    float font_scale = custom_font_scale_value / 100f;

                    float titleTextViewTextSize = viewHolder.titleTextView.getTextSize();
                    viewHolder.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextViewTextSize * font_scale);

                    float currentTextViewTextSize = viewHolder.currentTextView.getTextSize();
                    viewHolder.currentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextViewTextSize * font_scale);

                    float minTextViewTextSize = viewHolder.minTextView.getTextSize();
                    viewHolder.minTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, minTextViewTextSize * font_scale);

                    float maxTextViewTextSize = viewHolder.maxTextView.getTextSize();
                    viewHolder.maxTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextViewTextSize * font_scale);
                    gaugeDataFontAdjusted[position] = true;
                }

                CustomGauge gauge = viewHolder.gauge;

                if (unit.equals("%")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(100.0f);
                } else if (unit.equals("°C")) {
                    if (custom_temp_scale) {
                        gauge.setInitialMinValue(custom_temp_scale_min);
                        gauge.setInitialMaxValue(custom_temp_scale_max);
                    } else {
                        gauge.setValue(0.0f);
                        gauge.setInitialMinValue(0.0f);
                        gauge.setInitialMaxValue(100.0f);
                        gaugeData.setAutoAdjustScale(true);
                    }
                } else if (unit.equals("MB")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("V")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("MHz")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("Yes/No")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(1.0f);
                    //gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("W")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("RPM")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("MB/s")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("% of TDP")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("Gbps")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("x")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("KB/s")) {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                } else {
                    gauge.setValue(0.0f);
                    gauge.setInitialMinValue(0.0f);
                    gauge.setInitialMaxValue(0.001f);
                    gaugeData.setAutoAdjustScale(true);
                }

                gaugeData.setGaugeView(gauge);
                gaugeData.setTitleTextView(viewHolder.titleTextView);
                gaugeData.setCurrentTextView(viewHolder.currentTextView);
                gaugeData.setMinTextView(viewHolder.minTextView);
                gaugeData.setMaxTextView(viewHolder.maxTextView);

                break;
            }
            case GaugeData.TYPE_GRAPH: {
                MyViewHolderGraph viewHolder = (MyViewHolderGraph) holder;
                //Chart
                LineChart graph = viewHolder.graph;
                graph.setViewPortOffsets(84, 4, 4, 44);

                graph.getDescription().setEnabled(false);
                // enable touch gestures
                graph.setTouchEnabled(false);
                // if disabled, scaling can be done on x- and y-axis separately
                graph.setPinchZoom(false);
                // enable scaling
                graph.setScaleEnabled(true);
                graph.setDrawGridBackground(false);
                // set an alternative background color
                graph.setBackgroundColor(Color.TRANSPARENT);

                graph.animateX(1000);

                //Axes
                XAxis xl = graph.getXAxis();
                xl.setTextColor(Color.WHITE);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);
                xl.setDrawLabels(false);
                xl.setDrawAxisLine(true);
                xl.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

                YAxis leftAxis = graph.getAxisLeft();
                leftAxis.setTextColor(Color.WHITE);
                leftAxis.setDrawGridLines(false);
                leftAxis.setDrawZeroLine(true);
                leftAxis.setTextSize(7.0f);


                YAxis rightAxis = graph.getAxisRight();
                rightAxis.setEnabled(false);

                // Add a limit line
                //LimitLine ll = new LimitLine(LIMIT_MAX_MEMORY, "Upper Limit");
                //ll.setLineWidth(2f);
                //ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                //ll.setTextSize(10f);
                //ll.setTextColor(Color.WHITE);
                // reset all limit lines to avoid overlapping lines
                //leftAxis.removeAllLimitLines();
                //leftAxis.addLimitLine(ll);

                leftAxis.setDrawLimitLinesBehindData(false);

                //legend
                Legend l = graph.getLegend();
                l.setForm(Legend.LegendForm.LINE);
                l.setTextColor(Color.WHITE);
                l.setEnabled(true);

                //data
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                data.setDrawValues(false);
                // add empty data
                graph.setData(data);
                gaugeData.setGraphView(graph);

                if (unit.equals("%")) {
                    gaugeData.setValue(0.0f);
                    gaugeData.setValue(100.0f);
                    leftAxis.setAxisMaximum(100f);
                } else if (unit.equals("°C")) {
                    if (custom_temp_scale) {
                        gaugeData.setValue(custom_temp_scale_min);
                        gaugeData.setValue(custom_temp_scale_max);
                    } else {
                        gaugeData.setValue(0.0f);
                        gaugeData.setValue(100f);
                        leftAxis.setAxisMaximum(100f);
                    }
                } else if (unit.equals("MB")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("V")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("MHz")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("Yes/No")) {
                    gaugeData.setValue(1.0f);
                    gaugeData.setValue(0f);
                    //gaugeData.setAutoAdjustScale(true);
                } else if (unit.equals("W")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("RPM")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("MB/s")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("% of TDP")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("Gbps")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("x")) {
                    gaugeData.setValue(0f);
                } else if (unit.equals("KB/s")) {
                    gaugeData.setValue(0f);
                } else {
                    gaugeData.setValue(0f);
                }

                break;
            }
        }

        //gaugeData.updateViews();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}