package com.triskelapps.busjerez.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.triskelapps.busjerez.model.db.BusLineVisible;

import java.util.List;


@Dao
public interface BusLineVisibleDao extends BaseDao<BusLineVisible> {


    @Query("SELECT * FROM buslinevisible")
    List<BusLineVisible> getAll();

    @Query("SELECT * FROM buslinevisible WHERE lineId = :lineId")
    BusLineVisible getBusLineVisible(int lineId);


}
