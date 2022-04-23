package com.example.brains_cloud_server;


import com.example.brains_cloud_common.FileMessage;
import com.example.brains_cloud_common.FileRequest;

import io.netty.channel.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final List<Channel> channels = new ArrayList<>();
    private String clientName, login, pass;
    private static int newClientIndex = 1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;
            if (fr.getFileName().startsWith("newLogPass:")) {
                String[] parts =fr.getFileName().split("\\s");
                login=parts[1];
                pass=parts[2];
                int index=Integer.parseInt(parts[3]);
                System.out.println(":) "+login+" "+pass+" "+index);
                new JDBCConnect(login,pass,index);
            } else {
                if (Files.exists(Paths.get("server_storage/" + fr.getFileName()))) {
                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFileName()));
                    ctx.writeAndFlush(fm);
                }
            }

        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("Клиент подключился! " + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент # " + newClientIndex;
        newClientIndex++;
    }
}
