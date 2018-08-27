package com.mantic.control.device;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/7/24.
 * desc:
 */

public class ReportTools {

    public static  String valueToState(String value){
        String[] a1 = value.split(",");
        String v1 = a1[0];
        String[] a2 = v1.split(":");
        return a2[1];
    }

    public static  String valueToSrc(String value){
        String[] a1 = value.split(",");
        String v1 = a1[1];
        String[] a2 = v1.split(":");
        return a2[1];
    }

    public static  String valueToInfo(String value){
        String[] a1 = value.split(",");
        String v1 = a1[2];
        String[] a2 = v1.split(":");
        return a2[1];
    }

}