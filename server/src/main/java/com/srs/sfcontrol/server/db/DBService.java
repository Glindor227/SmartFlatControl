package com.srs.sfcontrol.server.db;

import com.srs.sfcontrol.common.LoggerParams;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Новый класс авториации через БД
public class DBService {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psInsertCurrent,psInsertLog;


    public static void initDBService() {
        try {
            connect();
            System.out.println("Подключились к БД");
            prepareStatements();
//            inputUsers();

        } catch (Exception e){
            e.printStackTrace();
            disconnect();
        }

    }
/*
    private void inputUsers() throws SQLException {
        statement.executeUpdate("INSERT INTO user (login,pass,nick) VALUES ('login6','pass6','nick6');");
        System.out.println("Добавили запись в БД");
    }
*/


    private static void disconnect(){
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        statement = connection.createStatement();

    }

    //TODO надо делать метод потоконезависимым
    public static List<Integer> getKDList() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT id FROM hostKD;");
        ArrayList<Integer> arr = new ArrayList<>();
        //TODO сделать на стимах
        while (rs.next()) {
            arr.add(rs.getInt(1));
        }
        return arr;
    }

    //TODO надо делать метод потоконезависимым
    public static Integer getIdFromLogin(String login) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT id FROM hostKD WHERE login = '"+login+"';");
        while (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    //TODO надо делать метод потоконезависимым
    public static List<LoggerParams> getParamList(Integer idKD) throws SQLException {
        List<LoggerParams> list = new ArrayList<>();
        ResultSet rs = statement.executeQuery("SELECT id_param,yellow,red FROM paramsKDHost  WHERE id_host = "+ idKD +";");
        //TODO сделать на стимах
        while (rs.next()) {
            list.add(new LoggerParams(rs.getInt("id_param"),rs.getInt("yellow"),rs.getInt("red")));
        }

        return list;
    }

    private static void prepareStatements() throws SQLException {
        psInsertCurrent = connection.prepareStatement("INSERT INTO value (id_host, id_type,value,date) VALUES (?, ?, ?, ?);");
        psInsertLog =  connection.prepareStatement("INSERT INTO log (id_host, id_type,value,date) VALUES (?, ?, ?, ?);");


    }

    public static void setCurrentValue(Integer idKD, Integer typeParam, Integer stateParam,Boolean logAdd) {
        try {
            statement.executeUpdate("DELETE FROM value WHERE id_host = " +idKD+ " AND id_type = "+typeParam+";");

            psInsertCurrent.setInt(1,idKD);
            psInsertCurrent.setInt(2,typeParam);
            psInsertCurrent.setInt(3,stateParam);
            String sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            psInsertCurrent.setString(4,sdf);

            psInsertCurrent.executeUpdate();
            if(logAdd){
                psInsertLog.setInt(1,idKD);
                psInsertLog.setInt(2,typeParam);
                psInsertLog.setInt(3,stateParam);
                psInsertLog.setString(4,sdf);
                psInsertLog.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
