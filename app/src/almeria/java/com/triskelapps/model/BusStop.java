package com.triskelapps.model;

import android.content.Context;

import androidx.room.Entity;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop  extends BusStopBase{

    private String waitTimeCode;
    private String waitTimeCodeLine;

    public String getWaitTimeCode() {
        return waitTimeCode;
    }

    public void setWaitTimeCode(String waitTimeCode) {
        this.waitTimeCode = waitTimeCode;
    }

    public String getWaitTimeCodeLine() {
        return waitTimeCodeLine;
    }

    public void setWaitTimeCodeLine(String waitTimeCodeLine) {
        this.waitTimeCodeLine = waitTimeCodeLine;
    }

    @Override
    public boolean hasBusStopExtraInfo() {
        return false;
    }

    @Override
    public String getBusStopExtraInfo(Context context) {
        return null;
    }
}
