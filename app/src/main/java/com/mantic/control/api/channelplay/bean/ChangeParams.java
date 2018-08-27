package com.mantic.control.api.channelplay.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/13.
 */

public class ChangeParams {
    private int start;
    private int end;
    private int to_position;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getTo_position() {
        return to_position;
    }

    public void setTo_position(int to_position) {
        this.to_position = to_position;
    }
}
