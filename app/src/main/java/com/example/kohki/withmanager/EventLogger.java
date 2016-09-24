package com.example.kohki.withmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

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
    //DB
    private EventDbHelper mDbHelper;
    private SQLiteDatabase db;
    ListView lv_event_list;
    private static Context context;

    public EventLogger(Context context, ListView event_list){

        this.context = context;
        lv_event_list = event_list ;
        setDB();
    // DB reset *****  If you delete here comment to reset DB, Commentout here !!
    //    mDbHelper.onUpgrade(db, EventDbHelper.DATABASE_VERSION, EventDbHelper.DATABASE_VERSION);

        updateEventLog();
        event_list.setOnItemClickListener(new EventLogListItemClickListener());
        event_list.setOnItemLongClickListener(new EventLogListItemLongClickListener());

    }
    static class EventLogListItemClickListener implements ListView.OnItemClickListener {

        EventLogListItemClickListener() {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            String item = (String) listView.getItemAtPosition(position);
        //    Toast.makeText(context,item+"",Toast.LENGTH_SHORT).show();
        }
    }
    static class EventLogListItemLongClickListener  implements ListView.OnItemLongClickListener {

        EventLogListItemLongClickListener() {
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            String item = (String) listView.getItemAtPosition(position);
            String[] items = item.split(",");
            String movie_name = items[items.length-1];
            Toast.makeText(context,movie_name+"を再生",Toast.LENGTH_SHORT).show();

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

     public void addEvent(int team, int number, int shoot_point, int is_success, String event_name, String movie_name){
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
         Toast.makeText(context, log, Toast.LENGTH_SHORT).show();

        /* DB insert*/
        ContentValues values = new ContentValues();
        values.put(EventContract.Event.COL_TEAM,       team);
        values.put(EventContract.Event.COL_NUM,        number);
        values.put(EventContract.Event.COL_POINT,     shoot_point);
        values.put(EventContract.Event.COL_SUCCESS,   is_success );
        values.put(EventContract.Event.COL_EVENT,      event_name);
        values.put(EventContract.Event.COL_MOVIE_NAME, movie_name);

        long newRowId;
        newRowId = db.insert(
                EventContract.Event.TABLE_NAME,
                EventContract.Event.COLUMN_NAME_NULLABLE,
                values);
        updateEventLog();
    }

    //Startが押された時に、ゲームの開始時刻を保存しておく
    public void addGameTime(String dateTime){

        ContentValues values = new ContentValues();
        values.put(EventContract.Game.COL_DATE, dateTime);

        System.out.println(dateTime + "を追加しま");
        db.insert(
                EventContract.Game.TABLE_NAME,
                null,
                values
        );
        System.out.println("した");
    }

    private void setDB(){
        mDbHelper = new EventDbHelper(context);
        db = mDbHelper.getWritableDatabase();
    }

    private void updateEventLog() {
        CardListAdapter adapter2 = new CardListAdapter(context);

        ArrayList<String> event_list = new ArrayList<>();

        try {
            //SQLiteCursor c = (SQLiteCursor)db.rawQuery(sql, null);
            SQLiteCursor c = (SQLiteCursor)db.query(
                    true,EventContract.Event.TABLE_NAME,
                    null,null,null,null,null,null,null);

            int rowcount = c.getCount();
            StringBuffer sb = new StringBuffer();
            c.moveToFirst();

            for (int i = 0; i < rowcount ; i++) {
                int id            = c.getInt(c.getColumnIndex(EventContract.Event._ID));
                int team          = c.getInt(c.getColumnIndex(EventContract.Event.COL_TEAM));
                int num           = c.getInt(c.getColumnIndex(EventContract.Event.COL_NUM));
                int point         = c.getInt(c.getColumnIndex(EventContract.Event.COL_POINT));
                int success       = c.getInt(c.getColumnIndex(EventContract.Event.COL_SUCCESS));
                String event      = c.getString(c.getColumnIndex(EventContract.Event.COL_EVENT));
                String movie_name = c.getString(c.getColumnIndex(EventContract.Event.COL_MOVIE_NAME));
                // 9/6: checked getting all column and they are correct
                String record = +id+","+
                        team + ","+
                        num + ","+
                        point + ","+
                        success+","+
                        event + ","+
                        movie_name;

                event_list.add(record);
                adapter2.add(record);

                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e("ERROR", e.toString());
        }
        /**/

     //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
     //           android.R.layout.simple_list_item_1, event_list);
        lv_event_list.setAdapter(adapter2);

    }

}
