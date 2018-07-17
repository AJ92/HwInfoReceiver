package com.threedevs.aj.HwInfoReceiver.Networking;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * This is used in CustomApplication!
 * Created by AJ on 09.08.2014.
 */
public class Networker extends Thread {


    private Socket nsocket; //Network Socket
    private InputStream nis; //Network Input Stream
    private OutputStream nos; //Network Output Stream

    private String ip = null;

    // Need handler for callbacks to the UI thread
    private Handler mHandler = new Handler();
    //accessed by thread... bad idea maybe...
    private ArrayList<String> messages = new ArrayList<String>();
    //accessed by UI and so on...
    private ArrayList<String> messages_safe = new ArrayList<String>();

    private static final String TAG = "Networker";

    private boolean cancelled = false;

    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            //copy stuff from thread to a new array and give it to the ui thread...
            ArrayList<String> copied_messages = new ArrayList<String>();
            for(int i = 0; i < messages.size(); i++){
                String line = messages.get(i);
                if(line != null) {
                    //line might contain multiple messages... split em...
                    String[] lines = line.split(";");
                    for (int j = 0; j < lines.length; j++) {
                        copied_messages.add(lines[j]);
                    }
                }
            }
            updateResultsInUi(copied_messages);
            messages.clear();
        }
    };

    public Networker(String ip) {
        this.ip = ip;
    }


    public void run() {
        try {
            //Log.i(TAG, "run: Creating socket");
            SocketAddress sockaddr = new InetSocketAddress(ip, 8881);
            nsocket = new Socket();
            nsocket.setTcpNoDelay(true);
            nsocket.setSoTimeout(60000);
            nsocket.connect(sockaddr, 60000); //12 second connection timeout
            if (nsocket.isConnected()) {
                nis = nsocket.getInputStream();
                nos = nsocket.getOutputStream();
                //Log.i(TAG, "run: Socket created, streams assigned");
                //Log.i(TAG, "run: Waiting for inital data...");
                byte[] buffer = new byte[4096];
                int read = nis.read(buffer, 0, 4096); //This is blocking
                while ((read != -1) && !isCancelled() && nsocket.isConnected()) {
                    byte[] tempdata = new byte[read];
                    System.arraycopy(buffer, 0, tempdata, 0, read);
                    String datastring = new String(tempdata);
                    messages.add(datastring);
                    mHandler.post(mUpdateResults);
                    //Log.i(TAG, "run: Got some data");
                    read = nis.read(buffer, 0, 4096); //This is blocking
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "run: IOException");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "run: Exception");
        } finally {
            try {
                nis.close();
                nos.close();
                nsocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "run: Finished");
        }
        Log.i(TAG, "run: Finished!");
    }


    public boolean isCancelled(){
        return cancelled;
    }

    public void cancel(){
        cancelled = true;
    }

    private void updateResultsInUi(ArrayList<String> copied_messages) {
        // Back in the UI thread -- update our UI elements based on the data in mResults
        for(int i = 0; i < copied_messages.size(); i++){
            //Log.i(TAG, "message: " + copied_messages.get(i));
            messages_safe.add(copied_messages.get(i));
        }
    }


    public List<String> getMessages(){
        List<String> msgs = new ArrayList<String>();
        for(int i = 0; i < messages_safe.size(); i++){
            msgs.add(messages_safe.get(i));
        }
        messages_safe.clear();
        return msgs;
    }


    private class SendNetworkMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            int count = params.length;
            for (int i = 0; i < count; i++) {
                try {
                    if (nsocket.isConnected()) {
                        //Log.i(TAG, "SendDataToNetwork: Writing received message to socket");
                        nos.write(params[i].getBytes());
                    } else {
                        Log.i(TAG, "SendDataToNetwork: Cannot send message. Socket is closed");
                    }
                } catch (Exception e) {
                    Log.i(TAG, "SendDataToNetwork: Message send failed. Caught an exception: " + e);
                    e.printStackTrace();
                    cancelled = true;
                }
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void SendDataToNetwork(String cmd) { //You run this from the menu_main thread.
        new SendNetworkMessage().execute(cmd);
    }

    public boolean isConnected(){
        if(nsocket != null && !cancelled){
            if(nsocket.isConnected()){
                return true;
            }
        }
        return false;
    }

}
