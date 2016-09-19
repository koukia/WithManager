package com.example.kohki.withmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Haruka on 2016/09/19.
 */
public class PlayerDBHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = "TEXT";
    private static final String INT_TYPE  = "INTEGER";
    private static final String COMMA_SEP = ",";

    //-------------------------
    // テーブル作成のクエリ
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlayerContract.Player.TABLE_NAME + " (" +
//                    PlayerContract.Player._ID + " INTEGER PRIMARY KEY," +
                    PlayerContract.Player.COL_NAME          + TEXT_TYPE + COMMA_SEP + // 学生名
                    PlayerContract.Player.COL_GRADE         + INT_TYPE  + COMMA_SEP + // 学年
                    PlayerContract.Player.COL_CLASS         + TEXT_TYPE + COMMA_SEP + // クラス
                    PlayerContract.Player.COL_POSITOIN      + TEXT_TYPE + COMMA_SEP + // ポジション

                    PlayerContract.Player.COL_SHOOT         + INT_TYPE + COMMA_SEP + // シュート力
                    PlayerContract.Player.COL_STRENGTH      + INT_TYPE + COMMA_SEP + // 体力
                    PlayerContract.Player.COL_JUMP          + INT_TYPE + COMMA_SEP + // ジャンプ力
                    PlayerContract.Player.COL_JUDGEMENT     + INT_TYPE + COMMA_SEP + // 判断力
                    PlayerContract.Player.COL_STAMINA       + INT_TYPE + COMMA_SEP + // 持久力
                    PlayerContract.Player.COL_INSTANTANEOUS + INT_TYPE + // 瞬発力
                    " )";

    // テーブル削除のクエリ
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PlayerContract.Player.TABLE_NAME;

    //-------------------------

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Player.db";

    //コンストラクタ
    public PlayerDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //テーブル作成クエリ実行
    public void onCreate(SQLiteDatabase db) {
        System.out.println("データベースを新たに作成します");
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
}
