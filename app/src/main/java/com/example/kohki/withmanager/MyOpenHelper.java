package com.example.kohki.withmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Haruka on 2016/08/20.
 */
public class MyOpenHelper extends SQLiteOpenHelper{

    public MyOpenHelper(Context context){
        super(context, "PlayerDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
  //      db.execSQL(""); // write SQL syntax
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
