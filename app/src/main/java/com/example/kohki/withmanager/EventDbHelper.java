package com.example.kohki.withmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kohki on 16/09/05.
 */
public class EventDbHelper extends SQLiteOpenHelper {
    //---
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventContract.Event.TABLE_NAME + " (" +
                    EventContract.Event._ID + " INTEGER PRIMARY KEY," +
                    EventContract.Event.COL_TEAM       + INT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_NUM        + INT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_POINT      + INT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_SUCCESS    + INT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_EVENT      + TEXT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_MOVIE_NAME + TEXT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_DATETIME   + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventContract.Event.TABLE_NAME;


    //Gameのテーブル作成
    private static final String SQL_CREATE_GAME =
            "CREATE TABLE " + EventContract.Game.TABLE_NAME + " (" +
                    EventContract.Game.COL_DATE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_GAME =
            "DROP TABLE IF EXIST " + EventContract.Game.TABLE_NAME;
    //---

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EventLog.db";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createTableGame(SQLiteDatabase db){
        System.out.println(SQL_CREATE_GAME);
        db.execSQL(SQL_CREATE_GAME);
        System.out.println("テーブルを作りました");
    }
    public void upGradeTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_GAME);
        createTableGame(db);
    }
}