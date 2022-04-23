package com.example.brains_cloud_client;



import com.example.brains_cloud_common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Socket socket;
    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    public static void start(){
        try{
            socket=new Socket(HOST,PORT);
            out= new ObjectEncoderOutputStream(socket.getOutputStream());
            in= new ObjectDecoderInputStream(socket.getInputStream(),50*1024*1024/*50Mb*/);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void stop(){
        try{
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        try{
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        try{
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(AbstractMessage msg){
        try{
            out.writeObject(msg);
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void sendAuthData(String login, String pass){
        String msg=String.format("%s %s",login,pass);
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert= new Alert(Alert.AlertType.ERROR, "Ошибка при регистрации.");
            alert.showAndWait();
        }
    }

    public static AbstractMessage readObject() throws ClassNotFoundException,IOException{
        Object obj=in.readObject();
        return (AbstractMessage) obj;
    }
}
