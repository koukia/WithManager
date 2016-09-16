package com.example.kohki.withmanager;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kohki on 16/09/15.
 */
public class ScoreDataGenerater {
    private static EventDbHelper mDbHelper;
    private static SQLiteDatabase db;
    private List<String[]> l_scoredata;
    int sum, now_our, now_opt;

    public ScoreDataGenerater(Context context){
        mDbHelper = new EventDbHelper(context);
        db = mDbHelper.getWritableDatabase();
        l_scoredata = new ArrayList<>();
        now_our = 1;
        now_opt = 1;
    }
    public List getScoreData() {


        /*get all data from DB*/
        try {
            SQLiteCursor c = (SQLiteCursor) db.query(
                    true, EventContract.Event.TABLE_NAME,
                    null, null, null, null, null, null, null);

            int rowcount = c.getCount();
            StringBuffer sb = new StringBuffer();
            c.moveToFirst();

            for (int i = 0; i < rowcount; i++) {
                int id = c.getInt(c.getColumnIndex(EventContract.Event._ID));
                int team = c.getInt(c.getColumnIndex(EventContract.Event.COL_TEAM));
                int num = c.getInt(c.getColumnIndex(EventContract.Event.COL_NUM));
                int point = c.getInt(c.getColumnIndex(EventContract.Event.COL_POINT));
                int success = c.getInt(c.getColumnIndex(EventContract.Event.COL_SUCCESS));
                String event = c.getString(c.getColumnIndex(EventContract.Event.COL_EVENT));
                String movie_name = c.getString(c.getColumnIndex(EventContract.Event.COL_MOVIE_NAME));
                // 9/6: checked getting all column and they are correct
                String record = +id + "," +
                        team + "," +
                        num + "," +
                        point + "," +
                        success + "," +
                        event + "," +
                        movie_name;
                //合count data related to scoresheet
                if(event.equals("shoot") && success == 1){
                    String[] db_row = {team+"", num+"", point+""};
                    addRowData(db_row);
                }
                //合count data related to foulsheet
                if(event.equals("foul") && success == 1) {
                    //TODO:foul list
                }
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        return l_scoredata;
    }
    private void addRowData(String[] row){
        int point = Integer.parseInt(row[2]);
        String mark;

        switch(point){
            case 1:
                mark = "・";
                break;
            case 2:
            case 3:
                mark = "/";
                break;
            default:
                mark = " ";
        }
        if(row[0].equals("0")){
            sum = now_our + point -1;
            while(now_our < sum){
                String[] obj = {"0", "", Integer.toString(now_our)};
                l_scoredata.add(obj);
                now_our++;
            }
            row[2] = now_our + mark;
            now_our++;

        }else if(row[0].equals("1")){
            sum = now_opt + point -1;
            while(now_opt < sum){
                String[] obj = {"1", "", Integer.toString(now_opt)};
                l_scoredata.add(obj);
                now_opt++;
            }
            row[2] = now_opt + mark;
            now_opt++;

        }

        l_scoredata.add(row);
    }
}
