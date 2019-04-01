package com.srs.sfcontrol.server;

import com.srs.common.Authentication;
import com.srs.common.AuthenticationGood;
import com.srs.common.ToAndroid;
import com.srs.sfcontrol.server.config.ServerConfig;
import com.srs.sfcontrol.server.db.DBService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class AndroidHandler {

    private static Boolean isAuth = false;// есть ли авторизация

    //todo пока  делаем поддержку одного Android подклюения
    // todo сделать отключение соединения, если долго нет активности
    public static void runAndroid(){
        Thread th = new Thread(() -> {
            System.out.println("Стартовали поток  runAndroid");
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(ServerConfig.getPortAndroid());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (true){
                System.out.println("Инициализация Android ");
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    System.out.println("пришло чтото от Android ");
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    while (true){
                        System.out.println("ждем Android");
                        Object msg = ois.readObject();
                        if(msg instanceof String) {
                            String str = (String)msg;
                            if (str.equals("getParams")) {
                                System.out.println("пришол от Android запрос:" + str);
    //                            AndroidTest(dos);
                                if(!isAuth){
                                    oos.writeObject("Не было аутентификации.");
                                    continue;
                                }
                                sendParamsToAndroid(oos);

                            }else {
                                System.out.println("пришло от Android неизвестная строка:" + str);
                            }
                            continue;
                        }
                        if(msg instanceof Authentication){
                            System.out.println("Пришла аутентификация.");
                            Authentication authentication = (Authentication)msg;
                            isAuth = DBService.getAndoidUser(authentication.getLogin(),authentication.getPassword());
                            if(isAuth)
                                oos.writeObject(new AuthenticationGood());
                            else
                                oos.writeObject("Аутентификация не прошла.");

                            continue;
                        }

                        System.out.println("пришло от Android непонятно что:" + msg.getClass());


                    }
                    //todo надо совобождать ресурсы - Stream и Socket
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        });
        th.start();

    }

    private static void sendParamsToAndroid(ObjectOutputStream oos) throws SQLException, IOException {
        Map<Integer,String> mapKD = DBService.getKDList();
        System.out.println("Готовим список для " + mapKD.size() +" ЦПУ КД");
        ToAndroid[] taa = new ToAndroid[mapKD.size()];
        int index = 0;
        for (Integer idKD:mapKD.keySet()) {
            ToAndroid ta = new ToAndroid(mapKD.get(idKD));
            Map<Integer,Integer> mapParams = DBService.getCurrentValue(idKD);
            System.out.println("Для " + ta.getKdName() +" "+mapParams.size()+" параметров. "+mapParams);
            for (Integer paramName:mapParams.keySet()) {
                ta.add(paramName,mapParams.get(paramName));
            }
            taa[index] = ta;
            index++;
        }

        oos.writeObject(taa);
    }
}
