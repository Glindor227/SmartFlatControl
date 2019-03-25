package com.srs.sfcontrol.kdhost;


import com.srs.sfcontrol.common.*;
import com.srs.sfcontrol.kdhost.config.KDHostConfig;
import org.jutils.jprocesses.model.ProcessInfo;

import java.io.IOException;
import java.util.List;

public class KDHost {
    static List<LoggerParams> kdParamsView;

    public void start(){
        System.out.println("Запустили клиент");
        KDHostConfig localConfig = new KDHostConfig();
        localConfig.LoadConfig();
        //todo надо предусмотреть что соединение с сервером не открылось
        if(!Network.start(KDHostConfig.getIpServer(),KDHostConfig.getPortServer())){
            System.out.println("Не смогли открыть сетевое соединение!");
        }


        Network.sendMsg(new StartKDHost(KDHostConfig.getLogin()));
        while (true) {
            try {
                System.out.println("Ждем сообщений от сервака");
                AbstractMessage am = Network.readObject();
                System.out.println("От сервака чтото пришло");
                if(am instanceof ConfigKDHost){

                    kdParamsView =((ConfigKDHost)am).getListParams();
                    System.out.println("Пришла конфигурация для опроса КД. параметров - "+kdParamsView.size());
                    for (LoggerParams lp:kdParamsView) {
                        System.out.println("параметр -"+lp.getType()+" Y- "+lp.getYellow()+" R - "+lp.getRed());
                    }

                    Thread t = new Thread(() -> {
                        System.out.println("Стартовали внутренний цикл опроса КД ");
                        while (true){
                            try {
                                ProcessInfo kdInfo = KDProcess.getKDState();
                                //todo состояния надо отсылать не по одному классу, а массивом классов(чтобы в одну посылку все ушло
                                if (kdInfo==null) {
                                    Network.sendMsg(new StateKDHost(4, 0));//нет в памяти КД
                                    //todo есчли нет такого процесса - задержка не будет происходить. нехорошо
                                    continue;
                                }
                                Network.sendMsg(new StateKDHost(4, 1));//нет в памяти КД
                                for (LoggerParams lp:kdParamsView) {
                                    if(lp.getType()==1){
                                        System.out.println("Виртуальная память:" + kdInfo.getVirtualMemory() +" физпамять:"+kdInfo.getPhysicalMemory()+" старт:"+kdInfo.getStartTime());
                                        Network.sendMsg(new StateKDHost(lp.getType(), Integer.parseInt(kdInfo.getPhysicalMemory())));//нет в памяти КД
                                    }
                                }

                                //todo время ожидания надо получать с сервака и делать не слип, а так чтобы приходило ровно раз в полученое время
                                Thread.sleep(10000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.setDaemon(true);
                    t.start();

                }
                if(am instanceof StartKDHost){
                    System.out.println("От сервака StartKDHost");


                }

                } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }



        Network.stop();


        System.out.println("Остановили клиент");

    }


    public static void main(String[] args) {
        new KDHost().start();
    }



}

