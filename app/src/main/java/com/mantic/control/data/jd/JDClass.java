package com.mantic.control.data.jd;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/31.
 * desc:
 */
public class JDClass {
    public static String appKey = "HUR698I6I5GK5WRYP3SRBRIJ8CS6UWYK";
    public static String appSecret = "7jaf4agkvm6jiyw3euvd39h44gefhmwm";
    public static String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static String redirectUri = "https://wx.coomaan.com/chatbot";
    public static String urlToken = "https://wx.coomaan.com/jd/token?code=CODE&test=1";
    public static String state = "";
    public static String AddressUrl = "https://smartopentest.jd.com/routerjson?";
    public static  String addressContent="360buy_param_json={\"client_ip\":\"172.34.56.78\",\"device_id\":\"AB-CD-EF-FF-FF-DC\", \"version\":\"1\"}";
    //这个是计算签名用到的 不能加 =
    public static  String signContent="360buy_param_json{\"client_ip\":\"172.34.56.78\",\"device_id\":\"AB-CD-EF-FF-FF-DC\", \"version\":\"1\"}";

    public static String  signContentdefConf(String uuid){
        if (!uuid.isEmpty()){
            return "360buy_param_json{\"device_id\":\"" + uuid +"\",\"client_ip\":\"172.34.56.78\"}";
        }else {
            return "360buy_param_json{\"device_id\":\"AB-CD-EF-FF-FF-DC\",\"client_ip\":\"172.34.56.78\"}";
        }
    }
}
