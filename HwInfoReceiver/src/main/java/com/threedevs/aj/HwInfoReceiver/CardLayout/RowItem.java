package com.threedevs.aj.HwInfoReceiver.CardLayout;

import com.threedevs.aj.HwInfoReceiver.CustomGauge;

public class RowItem {
    private String title;
    private String attrib1;
    private String attrib2;
    private String attrib3;

    private float minValue;
    private float maxValue;
    private float currentValue;

    private String unit;

    private int precision;

    private CustomGauge gauge;

    public RowItem(float minValue, float maxValue, float value, String unit , String title, int precision) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = value;
        this.unit = unit;

        this.title = title;
        this.precision = precision;

        this.gauge = null;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttrib1() {
        return attrib1;
    }
    public void setAttrib1(String attrib) {
        this.attrib1 = attrib;
    }

    public String getAttrib2() {
        return attrib2;
    }
    public void setAttrib2(String attrib) {
        this.attrib2 = attrib;
    }

    public String getAttrib3() {
        return attrib3;
    }
    public void setAttrib3(String attrib) {
        this.attrib3 = attrib;
    }


    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }


    public float getMinValue() {
        return minValue;
    }
    public void setMinValue(float value) {
        this.minValue = value;
    }

    public float getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(float value) {
        this.maxValue = value;
    }

    public float getCurrentValue() {
        return currentValue;
    }
    public void setCurrentValue(float value) {
        this.currentValue = value;
    }

    public void setGauge(CustomGauge gauge){
        this.gauge = gauge;
    }

    public CustomGauge getGauge(){
        return gauge;
    }



    @Override
    public String toString() {
        return title + "\n";
    }
}
