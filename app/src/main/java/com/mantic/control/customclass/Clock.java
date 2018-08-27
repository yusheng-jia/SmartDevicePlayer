package com.mantic.control.customclass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Clock implements Serializable{
    public String uuid;
    public String userid;
    public String deviceUuid;
    @SerializedName("days")
    public List<Integer> integerList;

    public int closed=0;
    public String period="";
    public long startTime;
    public long endTime;
    public String event;
    public String playListUri="";
}
