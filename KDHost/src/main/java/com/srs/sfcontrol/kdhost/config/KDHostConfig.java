package com.srs.sfcontrol.kdhost.config;

public class KDHostConfig {

    private static Integer portServer;
    private static String ipServer;
    private static String login;

    public KDHostConfig() {
    }

    public void LoadConfig(){

        this.portServer= 8189;
        this.login = "login4";
        ipServer = "localhost";
    }
    public static Integer getPortServer() {
        return portServer;
    }

    public static String getIpServer() {
        return ipServer;
    }

    public static String getLogin() {
        return login;
    }
}
