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
 */
public class EventLogger {
    private static final String TAG = "EventLogger";
    private EventDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ListView lv_event_list;
    private static Context context;
    private String gameStartDateTime;
    private static int cnt_double_click=0;
    private static int pre_id =-1;

    public EventLogger(Context context, ListView event_list){
        this.context = context;
        lv_event_list = event_list ;

        mDbHelper = new EventDbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
        updateEventLog(context, event_list);
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
                if(VideoActivity.sv_sPlayBackView != null) {
                    VideoActivity.sv_sPlayBackView.setVisibility(SurfaceView.VISIBLE);
                    if (VideoActivity.mPreviewCallback.mMediaPlayer != null) {
                        VideoActivity.mPreviewCallback.mMediaPlayer.release();
                        VideoActivity.mPreviewCallback.mMediaPlayer = null;
                    }
                    VideoActivity.mPreviewCallback.palyVideo(movie_name);
                    VideoActivity.mPreviewCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            VideoActivity.sv_sPlayBackView.setVisibility(SurfaceView.INVISIBLE);
                        }
                    });
                }else if(SynchroVideoActivity.sv_sPlayBackView != null){
                    SynchroVideoActivity.sv_sPlayBackView.setVisibility(SurfaceView.VISIBLE);
                    if (SynchroVideoActivity.mPreviewCallback.mMediaPlayer != null) {
                        SynchroVideoActivity.mPreviewCallback.mMediaPlayer.release();
                        SynchroVideoActivity.mPreviewCallback.mMediaPlayer = null;
                    }
                    SynchroVideoActivity.mPreviewCallback.palyVideo(movie_name);
                    SynchroVideoActivity.mPreviewCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            SynchroVideoActivity.sv_sPlayBackView.setVisibility(SurfaceView.INVISIBLE);
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

    public static void addEvent(SQLiteDatabase db, int team, int number){
         /* DB insert*/
        ContentValues values = new ContentValues();
        values.put(EventContract.Event.COL_TEAM,        team);
        values.put(EventContract.Event.COL_NUM,         number);
        values.put(EventContract.Event.COL_POINT,       VideoActivity.sPoint);
        values.put(EventContract.Event.COL_SUCCESS,     VideoActivity.sSuccess );
        values.put(EventContract.Event.COL_EVENT,       VideoActivity.sEventName);
        values.put(EventContract.Event.COL_MOVIE_NAME,  VideoActivity.sMovieName);
        values.put(EventContract.Event.COL_DATETIME,    VideoActivity.sGameStartDateTime);
        values.put(EventContract.Event.COL_QUARTER_NUM, VideoActivity.sCurrentQuarterNum);

        Toast.makeText(context,team+","+VideoActivity.sMovieName+","+VideoActivity.sGameStartDateTime,Toast.LENGTH_SHORT).show();
        long newRowId;
        newRowId = db.insert(
                EventContract.Event.TABLE_NAME,
                null,
                values);
        //System.out.println("datetime:" + dateTime);
    }
    public void addEvent(int team, int number, int point, int is_success, String event,
                         String movie_name, String start_time, int quarter_num){
         /* DB insert*/
        ContentValues values = new ContentValues();
        values.put(EventContract.Event.COL_TEAM,        team);
        values.put(EventContract.Event.COL_NUM,         number);
        values.put(EventContract.Event.COL_POINT,       point);
        values.put(EventContract.Event.COL_SUCCESS,     is_success );
        values.put(EventContract.Event.COL_EVENT,       event);
        values.put(EventContract.Event.COL_MOVIE_NAME,  movie_name);
        values.put(EventContract.Event.COL_DATETIME,    start_time);
        values.put(EventContract.Event.COL_QUARTER_NUM, quarter_num);

        long newRowId;
        newRowId = mDb.insert(
                EventContract.Event.TABLE_NAME,
                null,
                values);
        //System.out.println("datetime:" + dateTime);
    }
    //Startが押された時に、ゲームの開始時刻を保存しておく
    public void addGameTime(String dateTime){
        ContentValues values = new ContentValues();
        values.put(EventContract.Game.COL_DATE_TIME, dateTime);
        System.out.println(dateTime + "を追加しました");
        mDb.insert(
            EventContract.Game.TABLE_NAME,
            null,
            values
        );
    }


    public static void updateEventLog(Context context, ListView lv_event_list) {
        CardListAdapter adpt_eventlog = new CardListAdapter(context);
        EventDbHelper mDbHelper = new EventDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            //SQLiteCursor c = (SQLiteCursor)mDb.rawQuery(sql, null);
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
            //SQLiteCursor c = (SQLiteCursor).rawQuery(sql, null);
            SQLiteCursor c = (SQLiteCursor) mDb.query(
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
            //SQLiteCursor c = (SQLiteCursor)mDb.rawQuery(sql, null);
            SQLiteCursor c = (SQLiteCursor) mDb.query(
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
