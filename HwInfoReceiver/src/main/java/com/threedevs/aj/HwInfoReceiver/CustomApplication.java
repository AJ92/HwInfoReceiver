package com.threedevs.aj.HwInfoReceiver;

import android.app.Application;

import com.threedevs.aj.HwInfoReceiver.Networking.NetworkTask;

/**
 * Created by AJ on 14.07.2014.
 */
public class CustomApplication extends Application {


    private NetworkTask nt;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public NetworkTask createNetworkTask(String ip){



        nt = new NetworkTask(ip);
        nt.execute();
        return nt;
    }

    public NetworkTask getNetworkTask() {
        return nt;
    }
}
