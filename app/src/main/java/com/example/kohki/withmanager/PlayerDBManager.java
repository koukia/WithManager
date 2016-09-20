package com.example.kohki.withmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Haruka on 2016/09/19.
 */
public class PlayerDBManager {
    // DB
    private PlayerDBHelper mPlayerDBHelper;
    private SQLiteDatabase db;
    private Context context;


    public PlayerDBManager(Context context ){
        this.context = context;
        setDB();

        //テーブル作成,初期化 一回実行したら onUpgrade() でリセットできる
        //mPlayerDBHelper.onCreate(db);
        // DB reset *****  If you delete here comment to reset DB, Commentout here !!
        //mPlayerDBHelper.onUpgrade(db, PlayerDBHelper.DATABASE_VERSION, PlayerDBHelper.DATABASE_VERSION);

    }

    private void setDB(){
        mPlayerDBHelper = new PlayerDBHelper(context);
        db = mPlayerDBHelper.getWritableDatabase();
    }

    public void addPlayer(String name, int grade, String cls, String position){
        String log = "addPlayer() error";

        ContentValues values = new ContentValues();
        values.put(PlayerContract.Player.COL_NAME,      name);
        values.put(PlayerContract.Player.COL_GRADE,     grade);
        values.put(PlayerContract.Player.COL_CLASS,     cls);
        values.put(PlayerContract.Player.COL_POSITOIN,  position);

        values.put(PlayerContract.Player.COL_SHOOT,         10); // ステータスは10で初期化, 変更はupdateで
        values.put(PlayerContract.Player.COL_STRENGTH,      10);
        values.put(PlayerContract.Player.COL_JUMP,          10);
        values.put(PlayerContract.Player.COL_JUDGEMENT,     10);
        values.put(PlayerContract.Player.COL_STAMINA,       10);
        values.put(PlayerContract.Player.COL_INSTANTANEOUS, 10);

        try {
            db.insert(
                    PlayerContract.Player.TABLE_NAME,
                    PlayerContract.Player.COL_COLUMN_NAME_NULLABLE,
                    values);
        }catch(Exception e){
            System.out.println(log +"\n"+ e);
        }
    }

    public ArrayList<String> getPlayers(int grade){  //学年を引数で受け取り、その学年の生徒の名前のリストを返す
        ArrayList<String> players = new ArrayList<>();
        String name;


        try {
            SQLiteCursor c = (SQLiteCursor)db.query(
                    PlayerContract.Player.TABLE_NAME, new String[] {PlayerContract.Player.COL_NAME},
                    PlayerContract.Player.COL_GRADE + " = ?", new String[] {Integer.toString(grade)},
                    null, null, null);

            int rowCount = c.getCount();
            c.moveToFirst();

            for(int i = 0; i < rowCount; i++){
                name = c.getString(0);
                players.add(name);
                c.moveToNext();
            }
            /*for(int i = 0; i < rowCount; i++) {
                if(c.getInt(c.getColumnIndex(PlayerContract.Player.COL_GRADE)) == grade)
                    players.add(c.getString(c.getColumnIndex(PlayerContract.Player.COL_NAME)));
                c.moveToNext();
            }*/

        }catch(Exception e){
            System.out.println(e);
        }
        if(players.size() == 0)System.out.println("空だよ");
        return players;
    }
    public int[] getStatus(String playerName){
        int status[] = new int[6];
        try{
            SQLiteCursor c = (SQLiteCursor) db.query(
                    PlayerContract.Player.TABLE_NAME,
                    new String[] {
                            PlayerContract.Player.COL_SHOOT,         PlayerContract.Player.COL_JUMP,
                            PlayerContract.Player.COL_JUDGEMENT,     PlayerContract.Player.COL_STAMINA,
                            PlayerContract.Player.COL_INSTANTANEOUS, PlayerContract.Player.COL_STAMINA
                    },
                    PlayerContract.Player.COL_NAME + " = ?", new String[] {playerName},
                    null, null, null, null);

            c.moveToFirst();
            for(int i = 0; i < status.length; i++){
                status[i] = c.getInt(i);
                System.out.println(c.getInt(i));
            }

        }catch(Exception e) {
            return status;
        }
        return status;
    }
    public String[] getDetail(String playerName){
        String[] detail = new String[3];

        try {
            SQLiteCursor c = (SQLiteCursor) db.query(
                    PlayerContract.Player.TABLE_NAME,
                    new String[]{
                            PlayerContract.Player.COL_GRADE,
                            PlayerContract.Player.COL_CLASS,
                            PlayerContract.Player.COL_POSITOIN
                    },
                    PlayerContract.Player.COL_NAME + " = ?", new String[]{playerName},
                    null, null, null, null);

            c.moveToFirst();
            for (int i = 0; i < detail.length; i++) {
                detail[i] = c.getString(i);
                System.out.println(c.getString(i));
            }
        }catch(Exception e){
            return detail;
        }
        return detail;
    }
}
