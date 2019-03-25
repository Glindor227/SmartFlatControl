package com.srs.sfcontrol.kdhost;


import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import java.util.List;

public class KDProcess {
    private final static String kdProcessName = "idNucule.exe";
    public static  ProcessInfo getKDState(){
        long time1 = System.currentTimeMillis();

        List<ProcessInfo> processesList = JProcesses.getProcessList(kdProcessName);
        System.out.println("Время получения информации о idNucule"+ (System.currentTimeMillis()-time1));
        if(processesList.size()==0)
            return null;
        else return processesList.get(0);
    }

}
