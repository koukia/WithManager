package com.example.kohki.withmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kohki on 16/09/05.
 */
public class EventDbHelper extends SQLiteOpenHelper {
    //---
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_EVENT_ENTRIES =
            "CREATE TABLE " + EventContract.Event.TABLE_NAME + " (" +
                    EventContract.Event._ID + " INTEGER PRIMARY KEY," +
                    EventContract.Event.COL_TEAM        + INT_TYPE  + COMMA_SEP +
                    EventContract.Event.COL_NUM         + INT_TYPE  + COMMA_SEP +
                    EventContract.Event.COL_POINT       + INT_TYPE  + COMMA_SEP +
                    EventContract.Event.COL_SUCCESS     + INT_TYPE  + COMMA_SEP +
                    EventContract.Event.COL_EVENT       + TEXT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_MOVIE_NAME  + TEXT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_DATETIME    + TEXT_TYPE + COMMA_SEP +
                    EventContract.Event.COL_QUARTER_NUM + INT_TYPE  +" )";

    private static final String SQL_DELETE_EVENT_ENTRIES =
            "DROP TABLE IF EXISTS " + EventContract.Event.TABLE_NAME;

    //Gameのテーブル作成
    private static final String SQL_CREATE_GAME_ENTRIES =
            "CREATE TABLE " + EventContract.Game.TABLE_NAME + " (" +
                    EventContract.Game._ID + " INTEGER PRIMARY KEY," +
                    EventContract.Game.COL_DATE_TIME  + TEXT_TYPE + COMMA_SEP +
                    EventContract.Game.COL_GAME_NAME  + TEXT_TYPE + COMMA_SEP +
                    EventContract.Game.COL_GAME_NOTES + TEXT_TYPE  +" )";

    private static final String SQL_DELETE_GAME_ENTRIES =
            "DROP TABLE IF EXISTS " + EventContract.Game.TABLE_NAME;
    //---

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EventLog.db";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENT_ENTRIES);
        db.execSQL(SQL_CREATE_GAME_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_EVENT_ENTRIES);
        db.execSQL(SQL_DELETE_GAME_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static HashMap<String,String> getRowFromID(Context context, int id){
        EventDbHelper mDbHelper = new EventDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ EventContract.Event.TABLE_NAME+" WHERE _id = ?", new String[]{String.valueOf(id)});
        HashMap<String,String> row = new HashMap<>();
        try {
            if (cursor.moveToNext()) {
                row.put(EventContract.Event.COL_TEAM,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_TEAM)));
                row.put(EventContract.Event.COL_NUM,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_NUM)));
                row.put(EventContract.Event.COL_POINT,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_POINT)));
                row.put(EventContract.Event.COL_SUCCESS,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_SUCCESS)));
                row.put(EventContract.Event.COL_EVENT,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_EVENT)));
                row.put(EventContract.Event.COL_MOVIE_NAME,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_MOVIE_NAME)));
                row.put(EventContract.Event.COL_DATETIME,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_DATETIME)));
                row.put(EventContract.Event.COL_QUARTER_NUM,
                        cursor.getString(cursor.getColumnIndex(EventContract.Event.COL_QUARTER_NUM)));
            }
        } finally {
            cursor.close();
        }
        return row;
    }
    
    public static ArrayList<String> getRowFromSuccessShoot(SQLiteDatabase db, String game_start_time){
        Cursor cursor = db.rawQuery("SELECT * FROM "+ EventContract.Event.TABLE_NAME+" WHERE "+
                EventContract.Event.COL_SUCCESS+" = '1' and "+
                EventContract.Event.COL_EVENT+" = 'shoot' and "+
                EventContract.Event.COL_DATETIME+" = ?", new String[]{String.valueOf(game_start_time)});
        ArrayList column = new ArrayList();
        Integer[] row;
        try {
            while (cursor.moveToNext()) {
                row = new Integer[3];
                int id = cursor.getInt(cursor.getColumnIndex(EventContract.Event._ID));
                int team = cursor.getInt(cursor.getColumnIndex(EventContract.Event.COL_TEAM));
                int point = cursor.getInt(cursor.getColumnIndex(EventContract.Event.COL_POINT));
                Log.d("getRowFromS","id:"+id+",team:"+team+",point:"+point);
                row[0]=id;
                row[1]=team;
                row[2]=point;
                column.add(row);
            }
        } finally {
            cursor.close();
        }
        return column;
    }
    public static boolean updateColumn(SQLiteDatabase db, int id, int team, int num,
                                       int point, int success, String event){
        Log.d("update","id:"+id+",team:"+team+"num:"+num+",point:"+point+",event:"+event);
        ContentValues values = new ContentValues();
        values.put(EventContract.Event.COL_TEAM,    team);
        values.put(EventContract.Event.COL_NUM,     num);
        values.put(EventContract.Event.COL_POINT,   point);
        values.put(EventContract.Event.COL_SUCCESS, success);
        values.put(EventContract.Event.COL_EVENT,   event);
     /*   String sql = "update " + EventContract.Event.TABLE_NAME + " set "+
                EventContract.Event.COL_TEAM+"="+team+", "+
                EventContract.Event.COL_NUM+"="+num+", "+
                EventContract.Event.COL_POINT+"="+point+", "+
                EventContract.Event.COL_SUCCESS+"="+success+", "+
                EventContract.Event.COL_EVENT+"='"+event+
                "' where "+ EventContract.Event._ID+"="+id+";";
     */   try {
         //   db.execSQL(sql);
            int result = db.update(EventContract.Event.TABLE_NAME,values,
                    EventContract.Event._ID+" = ?",new String[]{String.valueOf(id)});
            if(result == -1)
                Log.e("SQL_update", "failed");
            else
                Log.e("SQL_update", "success:"+result);
        } catch (SQLException e) {
            Log.e("SQL_update", e.toString());
            return false;
        }
        return true;
    }
    public static boolean deleteRow(Context context, int id){
        EventDbHelper dbHelper = null;
        SQLiteDatabase db = null;
        try {
            dbHelper = new EventDbHelper(context);
            db = dbHelper.getWritableDatabase();
            db.delete(EventContract.Event.TABLE_NAME, "_id=?", new String[]{id + ""});
        }catch (SQLException e){
            Log.w("deleteRow",e);
            return false;
        }finally {
            if (db != null) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
            return true;
        }
    }
    public static ArrayList<String> getRowFromFoul(SQLiteDatabase db, String game_start_time){
        Cursor cursor = db.rawQuery("SELECT * FROM "+ EventContract.Event.TABLE_NAME+" WHERE "+
                EventContract.Event.COL_SUCCESS+" = '1' and "+
                EventContract.Event.COL_EVENT+" = 'foul' and "+
                EventContract.Event.COL_DATETIME+" = ?", new String[]{String.valueOf(game_start_time)});
        ArrayList column = new ArrayList();
        Integer[] row;
        try {
            while (cursor.moveToNext()) {
                row = new Integer[4];
                int id = cursor.getInt(cursor.getColumnIndex(EventContract.Event._ID));
                int team = cursor.getInt(cursor.getColumnIndex(EventContract.Event.COL_TEAM));
                int num = cursor.getInt(cursor.getColumnIndex(EventContract.Event.COL_NUM));
                int quo = cursor.getInt(cursor.getColumnIndex(EventContract.Event.COL_QUARTER_NUM));
                Log.d("getRowFromS","id:"+id+",team:"+team+",num:"+num);
                row[0]=id;
                row[1]=team;
                row[2]=num;
                row[3]=quo;
                column.add(row);
            }
        } finally {
            cursor.close();
        }
        return column;
    }
    public static HashMap<String,String> getGameRowFromID(SQLiteDatabase db, int id){
        Cursor cursor = db.rawQuery("SELECT * FROM "+ EventContract.Game.TABLE_NAME+" WHERE _id = ?", new String[]{String.valueOf(id)});
        HashMap<String,String> row = new HashMap<>();
        try {
            if (cursor.moveToNext()) {
                row.put(EventContract.Game.COL_DATE_TIME,
                        cursor.getString(cursor.getColumnIndex(EventContract.Game.COL_DATE_TIME)));
                row.put(EventContract.Game.COL_GAME_NAME,
                        cursor.getString(cursor.getColumnIndex(EventContract.Game.COL_GAME_NAME)));
                row.put(EventContract.Game.COL_GAME_NOTES,
                        cursor.getString(cursor.getColumnIndex(EventContract.Game.COL_GAME_NOTES)));
            }
        } finally {
            cursor.close();
        }
        return row;
    }
    public ArrayList<Integer> getRowFromDateTime(SQLiteDatabase db, String date_time){
        Cursor cursor = db.rawQuery("SELECT * FROM "+ EventContract.Event.TABLE_NAME+" WHERE "+
                EventContract.Event.COL_DATETIME+" = ?", new String[]{String.valueOf(date_time)});
        ArrayList column = new ArrayList();
        try {
            while (cursor.moveToNext()) {
                column.add(cursor.getInt(cursor.getColumnIndex(EventContract.Event._ID)));
            }
        } finally {
            cursor.close();
        }
        return column;
    }
    public boolean deleteGameRecordofGame(SQLiteDatabase db, String game_start_time){
        try {
            db.delete(EventContract.Game.TABLE_NAME,
                    EventContract.Game.COL_DATE_TIME+" = ?", new String[]{String.valueOf(game_start_time)});
            db.delete(EventContract.Event.TABLE_NAME,
                    EventContract.Event.COL_DATETIME+" = ?", new String[]{String.valueOf(game_start_time)});
        } catch (SQLException e){
            return false;
        }
        return true;
    }
}