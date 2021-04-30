package com.triskelapps.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.triskelapps.BuildConfig;
import com.triskelapps.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(primaryKeys = {"code","lineId"})
public class BusStop implements Serializable {

    public static final int CODE_NOT_FOUND = -1;

    private @NonNull String name;
    private int code;
    private int lineId;
    private String transfer;
    private String direction;

    @Ignore
    private List<Double> coordinates;

    @Ignore
    private Map<String, String> extras = new HashMap<>();

    @Ignore
    private boolean nonRegular;

    private String way;


    public String getName() {
        return name;
    }

    public String getNameComplete() {
        return name + (nonRegular ? "\n(No regular)" : "");
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public boolean hasValidCode() {
        return getCode() != CODE_NOT_FOUND;
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

    public boolean hasExtraInfo() {
        if (BuildConfig.FLAVOR.equals("jerez")) {
            return true;
        } else if (BuildConfig.FLAVOR.equals("almeria")) {
            return false;
        }

        return false;
    }

    public String getExtraInfo(Context context) {
        if (BuildConfig.FLAVOR.equals("jerez")) {
            return context.getString(
                    R.string.bus_stop_direction_transfer_format, getDirection(), getTransfer());
        } else if (BuildConfig.FLAVOR.equals("almeria")) {
            return null;
        }

        return null;
    }
}
