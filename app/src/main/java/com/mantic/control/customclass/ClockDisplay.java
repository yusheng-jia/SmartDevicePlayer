package com.mantic.control.customclass;

public class ClockDisplay {
    private String clocktime;
    private String hintmessage;
    public ClockDisplay(String time,String hint){
        this.clocktime=time;
        this.hintmessage=hint;
    }
    public void setClocktime(String time){
        this.clocktime=time;
    }
    public void setHintmessage(String message){
        this.hintmessage=message;
    }

    public String getClocktime() {
        return clocktime;
    }

    public String getHintmessage() {
        return hintmessage;
    }
}
