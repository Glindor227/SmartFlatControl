package com.srs.common;

import java.util.HashMap;
import java.util.Map;

public class ToAndroid extends AbstractAndroid {
    private static final long serialVersionUID =3201728743084568826L;
    private String kdName;
    private Map<Integer,Integer> paramArray = new HashMap<>();

    public Map<Integer,Integer> getParams() {
        return paramArray;
    }
    public void add(Integer name,Integer value){
        paramArray.put(name,value);
    }

    public String getKdName() {
        return kdName;
    }

    public ToAndroid(String kdName){
        this.kdName = kdName;
    }
}
