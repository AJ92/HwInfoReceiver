package com.threedevs.aj.HwInfoReceiver.Database.Objects;

/**
 * Created by AJ on 14.06.2014.
 */
public class Setting {
    private long id;
    private String setting;
    private String value;

    //constructor for retreiving data from database
    public Setting(long id, String setting, String value){
        this.id = id;
        this.setting = setting;
        this.value = value;
    }

    //constructor for new entry in the database
    public Setting(String setting, String value){
        this.id = -1;
        this.setting = setting;
        this.value = value;
    }

    public Setting(){
        this.id = -1;
        this.setting = "";
        this.value = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
