package com.mantic.control.websocket;


import com.mantic.antservice.m2m.WebSocketCommand;
import com.mantic.control.api.Url;
import com.mantic.control.utils.Glog;

import io.crossbar.autobahn.WebSocket;
import io.crossbar.autobahn.WebSocketConnection;
import io.crossbar.autobahn.WebSocketException;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/8/29.
 * desc: WebSocket
 */

public class WebSocketController {
    private static final String TAG = WebSocketController.class.getName();

    //标准单例模式
    private static volatile WebSocketController instance;

    public boolean isDisconnect = false;

    private WebSocketController(){}

    public static WebSocketController getInstance(){
        if (instance == null){
            synchronized (WebSocketController.class){
                if (instance == null){
                    instance = new WebSocketController();
                }
            }
        }
        return instance;
    }

    public  WebSocket mConnection = new WebSocketConnection();


    public void startWebSocket(final String deviceId) {

        try {
            mConnection.connect(Url.WEBSOCKET_URL, new WebSocket.ConnectionHandler() {
                @Override
                public void onOpen() {
                    Glog.i(TAG,"onOpen..." + deviceId);
                    isDisconnect = false;
                    WebSocketController.getInstance().mConnection.sendTextMessage(WebSocketCommand.getInstance(deviceId).bindDeviceCommand());
                }

                @Override
                public void onClose(int code, String reason) {
                    Glog.i(TAG,"onClose...");
                    isDisconnect = true;
                    mConnection = null;
                    mConnection = new WebSocketConnection();
                    WebSocketCommand.getInstance(deviceId).setWebSocketCommandEmpty();
                }

                @Override
                public void onTextMessage(String payload, WebSocketConnection.WebSocketListener webSocketListener) {
                    Glog.i(TAG,"onTextMessage: "+payload);
//                    try {
//                        JSONObject jsonObject = new JSONObject(payload);
//                        String code = (String) jsonObject.get("code");
//                        Glog.i(TAG,"code =============:"+code);
//                        //ant 目前为null 可以后续构建object
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    if (webSocketListener != null){
                        webSocketListener.onMessage(payload);
                    }
                }

                @Override
                public void onRawTextMessage(byte[] payload) {
                    Glog.i(TAG,"onRawTextMessage...");
                }

                @Override
                public void onBinaryMessage(byte[] payload) {
                    Glog.i(TAG,"onBinaryMessage...");
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}
