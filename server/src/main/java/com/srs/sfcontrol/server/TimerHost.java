package com.srs.sfcontrol.server;

import com.srs.sfcontrol.server.db.DBService;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class TimerHost{
    private
    Map<Integer,PingInfo> timeKD = new Hashtable<>();
    private int delayPingServer;

    public class PingInfo{
        private Long time;
        private Boolean alarm;

        public Long getTime() {
            return time;
        }

        public Boolean getAlarm() {
            return alarm;
        }

        public PingInfo(Long time) {
            this.time = time;
            alarm=false;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public void setAlarm(Boolean alarm) {
            this.alarm = alarm;
        }
    }

    public TimerHost(int delayPingServer) {
        System.out.println("конструктор  TimerHost");
        this.delayPingServer =delayPingServer;
        //лезем в базу и вытаскиваем список всех КД
        //TODO надо проверять всели в порядке с базой
        try {
            List<Integer> listKD = DBService.getKDList();
            for (Integer idKD:listKD) {
                timeKD.put(idKD,new PingInfo(System.currentTimeMillis()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("список серверов в конфигурации - " + timeKD.size());
    }

    public void  start(){

        Thread th = new Thread(() -> {
            System.out.println("Стартовали поток  TimerHost");
            try {
                while (true){
                    Thread.sleep(1000);
                    for (Integer idKD :timeKD.keySet()) {
                        PingInfo oldPI = timeKD.get(idKD);
                        if (oldPI.getAlarm())//уже тревожный сервер
                            continue;
                        Long delta = System.currentTimeMillis() - oldPI.getTime();
                        if(delta>delayPingServer){
                            oldPI.setAlarm(true);
                            System.out.println("TimerHost: для("+idKD+") вышло вермя ожидания");
                            DBService.setCurrentValue(idKD,5,0,true);
                            timeKD.put(idKD,oldPI);
                        }
                    }



                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        th.start();


    }

//todo надо делать потоконезависымый. Из разных хендлов netty сюда могут обращаться
    public void setActive(Integer idKD){
        if(!timeKD.containsKey(idKD))
        {
            System.out.println("несуществующий ключ объекта "+ idKD);
            return;
        }
        System.out.println("Обновили активность для "+ idKD);

        PingInfo pi = timeKD.get(idKD);
        pi.setTime(System.currentTimeMillis());
        if(pi.getAlarm()){
            pi.setAlarm(false);
            DBService.setCurrentValue(idKD,5,1,true);

        }

        timeKD.put(idKD,pi);
        //для определенного КД(по id) сообщается что была активность
    }

}
