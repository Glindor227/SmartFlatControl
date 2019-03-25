package com.srs.sfcontrol.common;

public class LoggerParams extends AbstractMessage{
    private Integer type;
    private Integer yellow;
    private Integer red;

    public LoggerParams(Integer type, Integer yellow, Integer red) {
        this.type = type;
        this.yellow = yellow;
        this.red = red;
    }

    public Integer getType() {
        return type;
    }

    public Integer getYellow() {
        return yellow;
    }

    public Integer getRed() {
        return red;
    }
}
