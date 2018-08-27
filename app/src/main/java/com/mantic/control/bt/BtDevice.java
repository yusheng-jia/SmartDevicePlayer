package com.mantic.control.bt;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/8/8.
 * desc: 蓝牙扫描完实体类
 */

public class BtDevice {
    private String name;
    private String mac;
    private boolean bind;
    private String connectState = "normal";

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public String  getConnectState() {
        return connectState;
    }

    public void setConnectState(String connecting) {
        connectState = connecting;
    }

    @Override
    public String toString() {
        return "BtDevice{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", bind=" + bind +
                ", connectState=" + connectState +
                '}';
    }
}
