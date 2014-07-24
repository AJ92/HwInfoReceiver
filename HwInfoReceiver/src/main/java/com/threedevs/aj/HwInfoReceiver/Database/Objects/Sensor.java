package com.threedevs.aj.HwInfoReceiver.Database.Objects;

/**
 * Created by AJ on 14.06.2014.
 */
public class Sensor {
    private long id;
    private long index;

    public Sensor(long id, long index){
        this.id = id;
        this.index = index;
    }

    public Sensor(long index){
        this.index = index;
    }

    public Sensor(){
        this.index = -1;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
