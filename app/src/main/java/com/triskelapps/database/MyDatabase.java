package com.triskelapps.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.triskelapps.database.dao.BusLineVisibleDao;
import com.triskelapps.database.dao.BusStopDao;
import com.triskelapps.model.BusStop;
import com.triskelapps.model.db.BusLineVisible;

@Database(entities = {BusLineVisible.class, BusStop.class}, version = 5, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public abstract BusLineVisibleDao busLineVisibleDao();

    public abstract BusStopDao busStopDao();


}
