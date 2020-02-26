package com.triskelapps.busjerez.database;

import androidx.room.RoomDatabase;

import com.triskelapps.busjerez.database.dao.CategoryDao;

//@Database(entities = {}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();


//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//
//            database.execSQL("CREATE TABLE 'product' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , 'name' VARCHAR(50), 'name_normalized' VARCHAR(50), 'idCategory' INTEGER DEFAULT 0, 'months_season' VARCHAR(12) DEFAULT 222222222222, 'marked' INTEGER DEFAULT 0, 'added' INTEGER DEFAULT 0, 'completed' INTEGER DEFAULT 0, 'amount' FLOAT DEFAULT 0, 'g_kg_ud' INTEGER DEFAULT 1, 'order_weight' INTEGER DEFAULT 0, 'remains' INTEGER DEFAULT 0);");
////            database.execSQL("INSERT INTO 'product'('_id', 'name', 'category', 'months_season', 'marked', 'added', 'completed', 'amount', 'g_kg_ud', 'order_weight', 'remains') SELECT * FROM 'frutas';");
//            database.execSQL("CREATE TABLE 'category' ('id' INTEGER, 'name' TEXT, 'color' INTEGER, 'order' INTEGER, PRIMARY KEY('id'));");
//
//        }
//    };

}
