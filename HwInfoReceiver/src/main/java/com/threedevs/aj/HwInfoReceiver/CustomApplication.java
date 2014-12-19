package com.threedevs.aj.HwInfoReceiver;

import android.app.Application;

import com.threedevs.aj.HwInfoReceiver.Networking.NetworkTask;
import com.threedevs.aj.HwInfoReceiver.Networking.Networker;

/**
 * Created by AJ on 14.07.2014.
 */
public class CustomApplication extends Application {


    private NetworkTask nt;
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
