package com.triskelapps.model;

import android.content.Context;

import androidx.room.Entity;

import com.triskelapps.R;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop  extends BusStopBase{

    private String waitTimeCode;
    private String waitTimeCodeLine;

    @Override
    public boolean hasBusStopExtraInfo() {
        return true;
    }

    @Override
    public String getBusStopExtraInfo(Context context) {
        return context.getString(R.string.bus_stop_transfer_format, getTransfer());
    }

    @Override
    public String getNameComplete() {
        return name;
    }


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

}
