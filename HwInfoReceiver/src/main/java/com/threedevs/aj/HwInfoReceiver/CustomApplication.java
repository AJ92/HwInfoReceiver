package com.threedevs.aj.HwInfoReceiver;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.threedevs.aj.HwInfoReceiver.Database.Objects.Sensor;
import com.threedevs.aj.HwInfoReceiver.Networking.NetworkTask;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJ on 14.07.2014.
 */
public class CustomApplication extends Application {

    private Networker nw;
    private String current_ip = null;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Networker createNetworker(String ip){
        if(nw != null){
            nw.cancel();
        }
        nw = new Networker(ip);
        nw.start();
        return nw;
    }

    public Networker getNetworker() {
        return nw;
    }

    public void setIP(String ip){
        current_ip = ip;
    }
    public String getIP(){
        return current_ip;
    }
}
