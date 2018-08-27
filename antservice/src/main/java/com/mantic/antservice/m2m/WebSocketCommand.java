package com.mantic.antservice.m2m;


/**
 * Created by Jia on 2017/5/24.
 */

public class WebSocketCommand {

    private static final String TAG = "WebSocketCommand";
    private static String DeviceED = "00aa000000002b";

//    public static final String BASE_URL = "ws://tt.api.coomaan.com:8899/ws";

    private static WebSocketCommand sInstance;

    private WebSocketCommand(){

    }

    public static synchronized WebSocketCommand getInstance(String deviceId) {
        if (sInstance == null) {
            sInstance = new WebSocketCommand();
            DeviceED = deviceId;
        }
        return sInstance;
    }

    public static synchronized void setWebSocketCommandEmpty() {
        sInstance = null;
    }

    /**
     * 控制音量
     * @param volume
     * @return
     */
    public String writeVolCommand(int volume){
        String volumeString = Integer.toString(volume);
        return  "{\"action\":\""+Action.write+"\",\"ed\":\""+DeviceED+"\",\"res\":\"/10000/0/4\",\"val\":\""+volumeString+"\",\"type\":\"INTEGER\"}";
    }

    /**
     * 读取当前音量
     * @return
     */
    public String readVolCommand(){
       return  "{\"action\":\""+Action.read+"\",\"ed\":\""+DeviceED+"\",\"res\":\"/10000/0/4\"}";
    }

    /**
     * 播放歌曲
     * @param url
     * @return
     */
    public String playMusicCommand(String url){
        return "{\"action\":\"" + Action.exe + "\",\"ed\":\"" + DeviceED + "\",\"res\":\"/10000/0/2\",\"val\":\"" + url + "\",\"type\":\"STRING\"}";
    }

    /**
     * 控制音乐
     * @param control { pause resume stop }
     * @return
     */
    public String controlMusicCommand(String control){
        return "{\"action\":\"" + Action.exe + "\",\"ed\":\"" + DeviceED + "\",\"res\":\"/10000/0/3\",\"val\":\"" + control +"\",\"type\":\"STRING\"}";
    }

    /**
     *  绑定设备
     * @return
     */
    public String bindDeviceCommand(){
        return "{\"action\":\"" + Action.bind + "\",\"ed\":\"" + DeviceED + "\"}";

    }

    /**
     *
     * @return
     */
    public String unbindDeviceCommand(){
        return "{\"action\":\"" + Action.unbind + "\",\"ed\":\"" + DeviceED + "\"}";
    }

    /**
     * 上报设备
     * @param edpoint
     * @param value
     * @param type
     * @return
     */
    public String reportDeviceCommand(String edpoint, String value, int type){
        if (type == 0){ //INTEGER 类型
            return "{\"action\":\"" + Action.report + "\",\"ed\":\"" + DeviceED + "\",\"res\":\"" + edpoint + "\",\"val\":\"" + value + "\",\"type\":\"" + Action.Type.INTEGER + "\"}";
        }else{ // STRING 类型
            return "{\"action\":\"" + Action.report + "\",\"ed\":\"" + DeviceED + "\",\"res\":\"" + edpoint + "\",\"val\":\"" + value + "\",\"type\":\"" + Action.Type.STRING + "\"}";
        }
    }

    /**
     *  心跳
     * @return
     */
    public String signalDeviceCommand(){
        return "{\"action\":\"" + Action.ping + "\",\"state\":0}";

    }

}
