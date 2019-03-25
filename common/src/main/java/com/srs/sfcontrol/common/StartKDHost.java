package com.srs.sfcontrol.common;

public class StartKDHost extends AbstractMessage{
    private String login;

    public String getLogin() {
        return login;
    }

    public StartKDHost(String login) {
        this.login = login;
    }
}
