package com.mantic.control.data.jd;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/31.
 * desc:
 */
public class TokenBean {
    /**
     * uid : 151972068879940665
     * access_token : 59d26e7f058f423fb9d0e934bdd0bbc3
     * expires_in : 2591988
     * user_nick : coomaan
     * result : OK
     * time : 1519790511
     * refresh_token : 9af38ba251134fe8823faa8c8bc6392e
     * avatar : https://i.jd.com/commons/img/no-img_mid_.jpg
     */

    private String uid;
    private String access_token;
    private int expires_in;
    private String user_nick;
    private String result;
    private int time;
    private String refresh_token;
    private String avatar;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
