package com.srs.sfcontrol.server;

import com.srs.common.ToAndroid;
import com.srs.sfcontrol.server.config.ServerConfig;
import com.srs.sfcontrol.server.db.DBService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//@
public class Server {

    private ServerConfig localConfig;
    private TimerHost timerHost;

    public Server() {

        localConfig = new ServerConfig();
        localConfig.LoadConfig();

        DBService.initDBService();
        timerHost = new TimerHost(localConfig.getDelayPingServer());
        timerHost.start();

        try {
            AndroidHandler.runAndroid();
            runNetty1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runNetty1() throws Exception {


        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(10 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new MainHandler(timerHost)
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(ServerConfig.getPortServer()).sync();
            System.out.println("Слушаем порт "+ ServerConfig.getPortServer());
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        System.out.println("Сервер стартовал");
        new Server();
    }
}
