package com.srs.sfcontrol.server.config;


public class ServerConfig {
    private static int portServer;
    private static int portAndroid;
    private static int delayPingServer;

    public ServerConfig() {
    }

    public void LoadConfig(){

        this.portServer= 8189;
        this.portAndroid= 8190;
        delayPingServer = 20000;
    }
    public static int getPortServer() {
        return portServer;
    }

    public static int getDelayPingServer() {
        return delayPingServer;
    }

    public static int getPortAndroid() {
        return portAndroid;
    }
}
