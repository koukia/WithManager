package com.example.kohki.withmanager;


import android.provider.BaseColumns;

/**
 * Created by Haruka on 2016/09/19.
 */
public final class PlayerContract {

    public PlayerContract(){}

    public static abstract class Player implements BaseColumns{
        public static final String TABLE_NAME      = "player";
        public static final String COL_NAME        = "name";
        public static final String COL_GRADE       = "grade";
        public static final String COL_CLASS       = "class";
        public static final String COL_POSITOIN    = "position";

        //以下 ステータス
        public static final String COL_SHOOT       = "shoot"; //シュート力
        public static final String COL_STRENGTH    = "strength"; //体力
        public static final String COL_JUMP        = "jump"; //ジャンプ力
        public static final String COL_JUDGEMENT   = "judgement";//判断力
        public static final String COL_STAMINA     = "stamina";//持久力
        public static final String COL_INSTANTANEOUS = "instantaneous";//瞬発力
        public static final String COL_COLUMN_NAME_NULLABLE = null;
    }
}
