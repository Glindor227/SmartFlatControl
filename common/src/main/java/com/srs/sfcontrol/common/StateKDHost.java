package com.srs.sfcontrol.common;

public class StateKDHost extends AbstractMessage{
    private Integer typeParam;
    private Integer stateParam;

    public StateKDHost(Integer typeParam, Integer stateParam) {
        this.typeParam = typeParam;
        this.stateParam = stateParam;
    }

    public Integer getTypeParam() {
        return typeParam;
    }

    public Integer getStateParam() {
        return stateParam;
    }
}
