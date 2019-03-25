package com.srs.sfcontrol.server;

import com.srs.sfcontrol.common.ConfigKDHost;
import com.srs.sfcontrol.common.StartKDHost;
import com.srs.sfcontrol.common.StateKDHost;
import com.srs.sfcontrol.server.db.DBService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.sql.SQLException;


public class MainHandler extends ChannelInboundHandlerAdapter {
    private Integer idKD=-1;
    private TimerHost timerHost;

    public MainHandler(TimerHost th) {
        timerHost = th;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try {
            if (msg == null) {
                return;
            }


            if (msg instanceof StartKDHost) {
                StartKDHost startKD = (StartKDHost) msg;
                System.out.println("Пришло начало обмена "+startKD.getLogin());
                idKD = DBService.getIdFromLogin(startKD.getLogin());
                if(idKD == -1 ){
                    System.out.println("Нет такого логина "+ startKD.getLogin());
                }
                timerHost.setActive(idKD);
                ConfigKDHost configKDHost = new ConfigKDHost();
                configKDHost.addList(DBService.getParamList(idKD));
//                ctx.writeAndFlush(new StartKDHost("Жужу"));// послали конфигурацию для слежения

                ctx.writeAndFlush(configKDHost);// послали конфигурацию для слежения
                System.out.println("Послали клиенту конфигурацию. "+startKD.getLogin());

            }
            if (msg instanceof StateKDHost) {
                StateKDHost stateKD = (StateKDHost) msg;
                timerHost.setActive(idKD);
                DBService.setCurrentValue(idKD,stateKD.getTypeParam(),stateKD.getStateParam(),true);


            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("MainHandler Освобождаем msg");
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
