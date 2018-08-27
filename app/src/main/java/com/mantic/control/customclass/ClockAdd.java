package com.mantic.control.customclass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClockAdd implements Serializable{
    @SerializedName("data")
    public Clock clock;
    public String retcode;
    public String service;
}
