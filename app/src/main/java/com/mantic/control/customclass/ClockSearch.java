package com.mantic.control.customclass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ClockSearch implements Serializable{
    @SerializedName("events")
    public List<Clock>  clockList;
}
