package com.srs.sfcontrol.common;

import java.util.ArrayList;
import java.util.List;

public class ConfigKDHost extends AbstractMessage{
    List<LoggerParams> listParams = new ArrayList<>();
    public void addList(List<LoggerParams> inParam)
    {
        listParams.addAll(inParam);
    };

    public List<LoggerParams> getListParams() {
        return listParams;
    }
}
