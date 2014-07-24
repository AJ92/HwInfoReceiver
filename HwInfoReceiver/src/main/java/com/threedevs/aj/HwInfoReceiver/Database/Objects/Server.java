package com.threedevs.aj.HwInfoReceiver.Database.Objects;

/**
 * Created by AJ on 14.06.2014.
 */
public class Server {
    private long id;
    private String ip;
    private String hostname;

    //constructor called when server is retreived from database
    public Server(long id, String ip, String hostname){
        this.id = id;
        this.ip = ip;
        this.hostname = hostname;
    }

    //when you try to add a new server, we only need the ip
    public Server(String ip){
        this.id = -1;
        this.ip = ip;
        this.hostname = "not available";
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
