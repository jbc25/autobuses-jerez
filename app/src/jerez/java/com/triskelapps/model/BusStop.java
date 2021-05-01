package com.triskelapps.model;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.triskelapps.R;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop  extends BusStopBase{


    protected String transfer;
    protected String direction;

    @Ignore
    protected boolean nonRegular;

    protected String way;


    @Override
    public boolean hasBusStopExtraInfo() {
        return true;
    }

    @Override
    public String getBusStopExtraInfo(Context context) {
        return context.getString(
                R.string.bus_stop_direction_transfer_format, getDirection(), getTransfer());
    }

    @Override
    public String getNameComplete() {
        return name + (nonRegular ? "\n(No regular)" : "");
    }


    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public boolean isNonRegular() {
        return nonRegular;
    }

    public void setNonRegular(boolean nonRegular) {
        this.nonRegular = nonRegular;
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

}
