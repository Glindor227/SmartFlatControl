package com.srs.sfcontrol.server.config;


public class ServerConfig {
    private static int portServer;
    private static int delayPingServer;

    public ServerConfig() {
    }

    public void LoadConfig(){

        this.portServer= 8189;
        delayPingServer = 20000;
    }
    public static int getPortServer() {
        return portServer;
    }

    public static int getDelayPingServer() {
        return delayPingServer;
    }
}
