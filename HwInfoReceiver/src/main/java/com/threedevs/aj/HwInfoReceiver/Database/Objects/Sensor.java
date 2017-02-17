package com.threedevs.aj.HwInfoReceiver.Database.Objects;

/**
 * Created by AJ on 14.06.2014.
 */
public class Sensor {
    private long id;
    private String index;

    public Sensor(long id, String index){
        this.id = id;
        this.index = index;
    }

    public Sensor(String index){
        this.index = index;
    }

    public Sensor(){
        this.index = "-1";
    }

    public String getHash() {
        return index;
    }

    public void setHash(String index) {
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
