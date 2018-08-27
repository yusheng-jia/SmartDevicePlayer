package com.mantic.control.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by PP on 2017/10/9.
 */

public class SocketHelper {
    private static final String TAG = "SocketHelper";
    public static final int SOCKET_SERVER_MSG = 1;
    public static final int SOCKET_ClIENT_MSG = 2;
    private final Handler mHandler;
    private ServerThread serverThread;
    private boolean openServerSocket;
    private ServerSocket serverSocket;

    public SocketHelper(Handler handler) {
        mHandler = handler;
    }

    public void startServerSocket() {
        openServerSocket = true;
        if (serverThread != null) return;
        serverThread = new ServerThread();
        serverThread.start();
    }

    public void stopServerSocket() {
        openServerSocket = false;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        serverThread = null;
    }

    public void startSendSSIDandPassword(String ipAddress, String content) {
        Log.e(TAG, "startSendSSIDandPassword: before ClientThread(content).start");
        new ClientThread(ipAddress, content).start();
    }

    class ClientThread extends Thread {

        public String content;
        public String ipAddress;

        public ClientThread(String ipAddress, String str) {
            content = str;
            this.ipAddress = ipAddress;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = mHandler.obtainMessage(SOCKET_ClIENT_MSG);
            connectServer(msg);
            if (msg.obj == null || !((String) msg.obj).contains("successful")) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectServer(msg);
            }
            mHandler.sendMessage(msg);
            Log.e(TAG, " clientSocket end.");
        }

        private void connectServer(Message msg) {
            Socket socket = new Socket();
            try {
                //连接服务器 并设置连接超时为1秒
                Log.e(TAG, " ClientThread  before  socket.connect,ipAddress;"+ipAddress);
                socket.connect(new InetSocketAddress(ipAddress, 30001), 1000);
                Log.e(TAG, " ClientThread  after  socket.connect");
                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                //获取输出输出流
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //向服务器发送信息
                ou.write(content.getBytes("utf-8"));
                ou.flush();

                //读取发来服务器信息
                String result = "";
                String buffer = "";
                while ((buffer = bff.readLine()) != null) {
                    result = result + buffer;
                }
                msg.obj = result.toString();
                //发送消息 修改UI线程中的组件

                Log.e(TAG, " ClientThread  mHandler.sendMessage.");
                //关闭各种输入输出流
                bff.close();
                ou.close();

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //连接超时 在UI界面显示消息
                msg.obj = "服务器连接失败！请检查网络是否打开";
                //发送消息 修改UI线程中的组件
            } catch (IOException e) {
                e.printStackTrace();
                msg.obj = "IO过程错误！请检查网络是否打开";
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ServerThread extends Thread {
        @Override
        public void run() {
            OutputStream output;
            String serverContent = "serverSocket get SSID and password successful.";
            try {
                serverSocket = new ServerSocket(30000);
                Log.e(TAG, " serverSocket prepare to start.");
                while (openServerSocket) {
                    Message msg = new Message();
                    msg.what = SOCKET_SERVER_MSG;
                    try {
                        Socket socket = serverSocket.accept();
                        Log.e(TAG, " serverSocket.accept.");
                        //向client发送消息
                        output = socket.getOutputStream();
                        output.write(serverContent.getBytes("utf-8"));
                        output.flush();
                        socket.shutdownOutput();

                        //获取输入信息
                        BufferedReader bff = new BufferedReader(new InputStreamReader
                                (socket.getInputStream()));
                        //读取信息
                        String result = "";
                        String buffer = "";
                        while ((buffer = bff.readLine()) != null) {
                            result = result + buffer;
                        }
                        msg.obj = result.toString();
                        mHandler.sendMessage(msg);
                        Log.e(TAG, " serverSocket  mHandler.sendMessage.");
                        bff.close();
                        output.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Log.e(TAG, " serverSocket end.");
        }
    }
}
