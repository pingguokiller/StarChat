package com.nano.starchat2.netclient;

import android.os.Handler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 2015/3/22.
 */
public class Client {

    private Socket ClientSocket;
    private android.os.Handler mHandlerGet;
    private android.os.Handler mHandlerSend;
    private Runnable  getClientThread;
    private Runnable  sendClientThread;
    private String ip;
    private int port;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private DataOutputStream dos;
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;

        mHandlerGet = new Handler();
        mHandlerSend = new Handler();
    }

    public boolean start() {
        try {
            //建立socket连接
            ClientSocket = new Socket();
            System.out.println(ip + " " + port + ">>>>>>>>>>>>>");
            ClientSocket.connect(new InetSocketAddress(ip, port), 1000);

            if (ClientSocket.isConnected()) {
                System.out.println("Connected......");
                //out = new ObjectOutputStream(ClientSocket.getOutputStream());

                //获取输出流，用于客户端向服务器端发送数据
                dos = new DataOutputStream(ClientSocket.getOutputStream());

                sendClientThread = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("send message:");
                        try {
                            String str = "5545";

                            dos.writeUTF("我是客户端，请求连接!");
                            //out.writeObject(str);
                            //out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mHandlerSend.postDelayed(sendClientThread, 2000);
                    }
                };

                getClientThread = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("In Client Thread!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        try {
                            in=new ObjectInputStream(ClientSocket.getInputStream());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        if(ClientSocket.isClosed()){
                            return;
                        }
                        try {
                            Object obj=in.readObject();
                            if(obj instanceof String){
                                System.out.println("Client recive:"+obj);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        mHandlerGet.postDelayed(getClientThread, 1000);
                    }
                };

                mHandlerSend.post(sendClientThread);
                //mHandlerGet.post(getClientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
