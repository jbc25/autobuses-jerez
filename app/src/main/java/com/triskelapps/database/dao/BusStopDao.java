package com.triskelapps.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.triskelapps.model.BusStop;

import java.util.List;


@Dao
public interface BusStopDao extends BaseDao<BusStop> {


    @Query("SELECT * FROM busstop")
    List<BusStop> getAll();

    @Query("SELECT * FROM busstop WHERE code = :code AND lineId = :lineId")
    BusStop getBusBusStop(int code, int lineId);


}
