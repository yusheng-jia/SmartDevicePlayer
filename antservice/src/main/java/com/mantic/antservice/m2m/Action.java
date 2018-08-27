package com.mantic.antservice.m2m;

/**
 * Created by Jia on 2017/5/24.
 */

public class Action {
    public static final String bind = "bind";
    public static final String bind_rsp ="bind_rsp";
    public static final String unbind ="unbind";
    public static final String unbin_rsp ="unbin_rsp";
    public static final String write ="write";
    public static final String write_rsp ="write_rsp";
    public static final String read ="read";
    public static final String read_rsp ="read_rsp";
    public static final String exe ="exe";
    public static final String exe_rsp ="exe_rsp";
    public static final String report ="report";
    public static final String ping = "ping";

    public class Type{
        public static final String STRING = "STRING";
        public static final String INTEGER = "INTEGER";
    }

    public static final String CONTROL_SUCCESS = "ant_control_success";
    public static final String CONTROL_FAILED = "ant_control_failed";
}
