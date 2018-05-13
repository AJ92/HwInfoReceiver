package com.threedevs.aj.HwInfoReceiver;

import android.widget.TextView;

import static java.lang.String.format;

/**
 * Created by AJ on 26.12.2014.
 */
public class GaugeData {
    private double value_current = 0.0;
    private double value_max = 0.0;
    private double value_min = 0.0;

    private boolean first_value_set = false;
    private boolean first_value_set_gauge = false;

    private boolean auto_adjust_scale = false;

    private int precision = 5;

    private String name = "";
    private String unit = "";

    private CustomGauge view;
    private TextView name_view;
    private TextView cur_view;
    private TextView min_view;
    private TextView max_view;

    public GaugeData(){

    }

    public void updateViews(){
        if(view!=null){
            view.setTitle(unit);
            view.setValue((float)value_current);

            if(auto_adjust_scale && first_value_set) {
                view.setMaxValue((float) value_max);
                view.setMinValue((float) value_min);
            }

            first_value_set_gauge = true;
            view.update();
        }

        if(name_view!=null){
            name_view.setText(name);
        }
        if(cur_view!=null){
            cur_view.setText("Cur: " + format("%." + precision + "f", value_current));
        }
        if(min_view!=null){
            min_view.setText("Min: " + format("%." + precision + "f", value_min));
        }
        if(max_view!=null){
            max_view.setText("Max: " + format("%." + precision + "f", value_max));
        }
    }

    public void setAutoAdjustScale(boolean adjust){
        auto_adjust_scale = adjust;
    }

    public void setGauge(CustomGauge g){
        view = g;
    }

    public void setTitleTextView(TextView tv){
        name_view = tv;
    }

    public void setCurrentTextView(TextView tv){
        cur_view = tv;
    }

    public void setMinTextView(TextView tv){
        min_view = tv;
    }

    public void setMaxTextView(TextView tv){
        max_view = tv;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setUnit(String unit){
        this.unit = unit;
        if(unit.equals("%")){
            precision = 1;
        }
        else if(unit.equals("°C")){
            precision = 1;
        }
        else if(unit.equals("MB")){
            precision = 1;
        }
        else if(unit.equals("V")){
            precision = 6;
        }
        else if(unit.equals("MHz")){
            precision = 2;
        }
        else if(unit.equals("Yes/No")){
            precision = 0;
        }
        else if(unit.equals("W")){
            precision = 6;
        }
        else if(unit.equals("RPM")){
            precision = 1;
        }
        else if(unit.equals("MB/s")){
            precision = 6;
        }
        else if(unit.equals("% of TDP")){
            precision = 3;
        }
        else if(unit.equals("Gbps")){
            precision = 1;
        }
    }

    public String getUnit(){
        return unit;
    }

    public void setValue(double value){
        value_current = value;
        if(first_value_set == false){
            first_value_set = true;
            value_max = value;
            value_min = value;
            return;
        }
        value_max = Math.max(value_max,value);
        value_min = Math.min(value_min,value);
    }

    public double getValue(){
        return value_current;
    }

    public double getValueMin(){
        return value_min;
    }

    public double getValueMax(){
        return value_max;
    }

    public void setValueMin(double value){
        value_min = value;
    }

    public void setValueMax(double value){
        value_max = value;
    }

    public int getPrecision(){
        return precision;
    }

}
