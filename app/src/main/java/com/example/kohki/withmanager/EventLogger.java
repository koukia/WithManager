package com.example.kohki.withmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kohki on 16/09/01.
 *
 * 記録されるもの:
 * -Team
 * -number
 * -event
 * --shoot
 * --bool
 * -movie_name
 */
public class EventLogger {
    private static final String TAG = "EventLogger";
    //DB
    private EventDbHelper mDbHelper;
    private SQLiteDatabase db;
    ListView lv_event_list;
    private static Context context;
    private String gameStartDateTime;
    private static int cnt_double_click=0;
    private static int pre_id =-1;
    public EventLogger(Context context, ListView event_list, String gameStartDateTime){
        this.context = context;
        lv_event_list = event_list ;
        this.gameStartDateTime = gameStartDateTime;

        setDB();
        // DB reset *****  If you delete here comment to reset DB, Commentout here !!
        //mDbHelper.onUpgrade(db, EventDbHelper.DATABASE_VERSION, EventDbHelper.DATABASE_VERSION);

        updateEventLog();
        event_list.setOnItemClickListener(new EventLogListItemClickListener());
        event_list.setOnItemLongClickListener(new EventLogListItemLongClickListener());
    }
    static class EventLogListItemClickListener implements ListView.OnItemClickListener {

        EventLogListItemClickListener() {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv_event_log      = (ListView) parent;
            int   id_of_event_log = (int)lv_event_log.getItemAtPosition(position);
            if(id_of_event_log == pre_id){
                cnt_double_click++;
                if(cnt_double_click >= 2) {
                    HashMap<String,String> row = EventDbHelper.getRowFromID(context,id_of_event_log);
                    Toast.makeText(context,row+"",Toast.LENGTH_SHORT).show();

                    cnt_double_click=0;
                }
            }else if (pre_id == -1){
                cnt_double_click++;
            }
            pre_id = id_of_event_log;
        }
    }
    static class EventLogListItemLongClickListener  implements ListView.OnItemLongClickListener {

        EventLogListItemLongClickListener() {
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv_event_log      = (ListView) parent;
            int   item_of_event_log = (int)lv_event_log.getItemAtPosition(position);
            HashMap<String,String> row =  EventDbHelper.getRowFromID(context,item_of_event_log);
            String   movie_name = row.get(EventContract.Event.COL_MOVIE_NAME);
            Log.d(TAG,movie_name+"を再生");

            try { //スタンドアローンかBluetooth通信中か
                if(VideoActivity.mOverLaySurfaceView != null) {
                    VideoActivity.mOverLaySurfaceView.setVisibility(SurfaceView.VISIBLE);
                    if (VideoActivity.mPreviewCallback.mMediaPlayer != null) {
                        VideoActivity.mPreviewCallback.mMediaPlayer.release();
                        VideoActivity.mPreviewCallback.mMediaPlayer = null;
                    }
                    VideoActivity.mPreviewCallback.palyVideo(movie_name);
                    VideoActivity.mPreviewCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            VideoActivity.mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);
                        }
                    });
                }else if(SynchroVideoActivity.mOverLaySurfaceView != null){
                    SynchroVideoActivity.mOverLaySurfaceView.setVisibility(SurfaceView.VISIBLE);
                    if (SynchroVideoActivity.mPreviewCallback.mMediaPlayer != null) {
                        SynchroVideoActivity.mPreviewCallback.mMediaPlayer.release();
                        SynchroVideoActivity.mPreviewCallback.mMediaPlayer = null;
                    }
                    SynchroVideoActivity.mPreviewCallback.palyVideo(movie_name);
                    SynchroVideoActivity.mPreviewCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            SynchroVideoActivity.mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);
                        }
                    });
                }
            } catch (NullPointerException e) {
                Toast.makeText(context, "ぬるぽ", Toast.LENGTH_LONG).show();
                return false;
            }
            return false;
        }
    }


    public void addEvent(int team, int number, int shoot_point, int is_success, String event_name, String movie_name, String dateTime ){
        String log = "addEvent() err";

        switch (event_name){
            case "shoot":
                log = team+"チーム"+number+"番"+"\nE:"+shoot_point+"点"+is_success+"\nMovie:"+movie_name;
                break;
            case "foul":
                log = team+"チーム"+number+"番"+"\nE:"+event_name+"\nMovie:"+movie_name;
                break;
            case "traveling":
                log = team+"チーム"+number+"番"+"\nE:"+event_name+"\nMovie:"+movie_name;
                break;
            case "steal":
                log = team+"チーム"+number+"番"+"\nE:"+event_name+"\nMovie:"+movie_name;
                break;
            case "rebound":
                log = team+"チーム"+number+"番"+"\nE:"+event_name+"\nMovie:"+movie_name;
                break;
            default:
                break;
        }
    //    Toast.makeText(context, log, Toast.LENGTH_SHORT).show();

         /* DB insert*/
        ContentValues values = new ContentValues();
        values.put(EventContract.Event.COL_TEAM,        team);
        values.put(EventContract.Event.COL_NUM,         number);
        values.put(EventContract.Event.COL_POINT,       shoot_point);
        values.put(EventContract.Event.COL_SUCCESS,     is_success );
        values.put(EventContract.Event.COL_EVENT,       event_name);
        values.put(EventContract.Event.COL_MOVIE_NAME,  movie_name);
        values.put(EventContract.Event.COL_DATETIME,    dateTime);
        values.put(EventContract.Event.COL_QUARTER_NUM, VideoActivity.current_quarter_num);

        long newRowId;
        newRowId = db.insert(
                EventContract.Event.TABLE_NAME,
                null,
                values);
        updateEventLog();
        //System.out.println("datetime:" + dateTime);
    }

    //Startが押された時に、ゲームの開始時刻を保存しておく
    public void addGameTime(String dateTime){
        ContentValues values = new ContentValues();
        values.put(EventContract.Game.COL_DATE_TIME, dateTime);
        System.out.println(dateTime + "を追加しました");
        db.insert(
            EventContract.Game.TABLE_NAME,
            null,
            values
        );
    }

    private void setDB(){
        mDbHelper = new EventDbHelper(context);
        db = mDbHelper.getWritableDatabase();
    }

    private void updateEventLog() {
        CardListAdapter adpt_eventlog = new CardListAdapter(context);
        try {
            //SQLiteCursor c = (SQLiteCursor)db.rawQuery(sql, null);
            SQLiteCursor c = (SQLiteCursor)db.query(
                    true,EventContract.Event.TABLE_NAME,
                    null,null,null,null,null,null,null);

            int rowcount = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < rowcount ; i++) {
                int id_event_db = c.getInt(c.getColumnIndex(EventContract.Event._ID));
                adpt_eventlog.insert(id_event_db, 0);//adapterにセットするしておいてclicklistenerで使う
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        lv_event_list.setAdapter(adpt_eventlog);
    }

    public ArrayList<Integer[]> getFoul(){
        ArrayList<Integer[]> foulList = new ArrayList<>();

        try {
            //SQLiteCursor c = (SQLiteCursor)db.rawQuery(sql, null);
            SQLiteCursor c = (SQLiteCursor) db.query(
                    EventContract.Event.TABLE_NAME,
                    new String[] {EventContract.Event.COL_TEAM, EventContract.Event.COL_NUM},
                    EventContract.Event.COL_EVENT + " = ?", new String[] {"foul"},
                    null, null, null, null);

            c.moveToFirst();
            System.out.println(c.getCount());
            for(int i = 0; i < c.getCount(); i++){
//                System.out.println(c.getInt(0) + " : " + c.getInt(1));
                foulList.add(new Integer[] {c.getInt(0), c.getInt(1)}); // 0:TEAM, 1:NUMBER
                c.moveToNext();
            }

        }catch(Exception e){
            Log.e("ERROR", e.toString());
        }
        return foulList;
    }

    //試合を記録しておいて、後で見返す
    public ArrayList<String> getGames(){
        ArrayList<String> games = new ArrayList<>();

        try{
            //SQLiteCursor c = (SQLiteCursor)db.rawQuery(sql, null);
            SQLiteCursor c = (SQLiteCursor) db.query(
                    EventContract.Event.TABLE_NAME,
                    new String[] {EventContract.Event.COL_DATETIME},
                    null, null,
                    null, null, null, null);

            c.moveToFirst();
            System.out.println(c.getCount());
            for(int i = 0; i < c.getCount(); i++){
                games.add(c.getString(0));
                c.moveToNext();
            }

        }catch(Exception e){
            Log.e("ERROR", e.toString());
        }
        return games;
    }
}
