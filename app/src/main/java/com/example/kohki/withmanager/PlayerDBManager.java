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
    ListView lv_player_list;
    private static Context context;

    public PlayerDBManager(Context context ){//ListView lv_player_list){
        this.context = context;
        //this.lv_player_list = lv_player_list;
        setDB();

        // DB reset *****  If you delete here comment to reset DB, Commentout here !!
        //mDBHelper.onUpgrade(db, PlayerDBHelper.DATABASE_VERSION, PlayerDBHelper.DATABASE_VERSION);

        //updatePlayerDB();
        //lv_player_list.setOnItemClickListener(new PlayerListItemClickListener());
    }

    private void setDB(){
        mPlayerDBHelper = new PlayerDBHelper(context);
        db = mPlayerDBHelper.getWritableDatabase();

        //テーブル作成,初期化 一回実行したら onUpgrade() でリセットできる
//        mPlayerDBHelper.onCreate(db);
//        mPlayerDBHelper.onUpgrade(db, PlayerDBHelper.DATABASE_VERSION, PlayerDBHelper.DATABASE_VERSION);
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
                    true, PlayerContract.Player.TABLE_NAME,
                    null, null, null, null, null, null, null);

            int rowCount = c.getCount();
            c.moveToFirst();

            for(int i = 0; i < rowCount; i++) {
                if(c.getInt(c.getColumnIndex(PlayerContract.Player.COL_GRADE)) == grade)
                    players.add(c.getString(c.getColumnIndex(PlayerContract.Player.COL_NAME)));

            }

        }catch(Exception e){
            System.out.println(e);
        }
        if(players.size() == 0)System.out.println("空だよ");
        return players;
    }

    static class PlayerListItemClickListener implements ListView.OnItemClickListener {

        public PlayerListItemClickListener() {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //リストに選手の一覧が出るので、選択された選手でアレします
            ListView listView = (ListView)parent;
            String player_name = (String) listView.getItemAtPosition(position); // リストからタップされた選手の名前を取得

        }
    }

}
