package com.example.kohki.withmanager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kohki on 16/09/01.
 *
 */
public class EventLogger {
    private static final String TAG = "EventLogger";
    private static Context context;
    private EventDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static int cntDoubleClick=0;
    private static int preId =-1;

    private int record_id;
    private int team;
    private int num;
    private int point;
    private int success;
    private String event;
    private AlertDialog.Builder mADBuilder;

    private static float preLat =0;
    private int cntLat = 0;
    private static int trgNum = 5;

    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private static boolean flg_edit = true;

    public EventLogger(Context context){
        this.context = context;

        mDbHelper = new EventDbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }
    public void updateEventLog(Context context, ListView lv_event_list) {
        lv_event_list.setOnItemClickListener(new EventLogListItemClickListener());
        //    lv_event_list.setOnTouchListener(new EventLogListItemTouchListener());
        lv_event_list.setOnItemLongClickListener(new EventLogListItemLongClickListener());
  /*      gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        lv_event_list.setOnTouchListener(gestureListener);
*/
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

    class EventLogListItemClickListener implements ListView.OnItemClickListener {

        EventLogListItemClickListener() {}

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv_event_log      = (ListView) parent;
            int   id_of_event_log = (int)lv_event_log.getItemAtPosition(position);
            if(flg_edit)
                editEvent(id_of_event_log);
            /*
            if(id_of_event_log == preId){
                cntDoubleClick++;
                if(cntDoubleClick >= 2) {

                    cntDoubleClick=0;
                }
            }else if (preId == -1){
                cntDoubleClick++;
            }
            preId = id_of_event_log;
            */
            flg_edit = true;
        }
    }
    static class EventLogListItemLongClickListener  implements ListView.OnItemLongClickListener {

        VideoRecorder mRecorder;

        EventLogListItemLongClickListener() {}

            @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv_event_log      = (ListView) parent;
            int   item_of_event_log = (int)lv_event_log.getItemAtPosition(position);
            HashMap<String,String> row =  EventDbHelper.getRowFromID(context,item_of_event_log);
            String   movie_name = row.get(EventContract.Event.COL_MOVIE_NAME);
            Log.d(TAG,movie_name+"を再生");
                flg_edit=false;
            try { //スタンドアローンかBluetooth通信中か
                if(VideoActivity.mSubSurface != null) {

                    VideoActivity.mSubSurface.setVisibility(SurfaceView.VISIBLE);
                    if (VideoActivity.mSubSurfaceCallback.mMediaPlayer != null) {
                        VideoActivity.mSubSurfaceCallback.mMediaPlayer.release();
                        VideoActivity.mSubSurfaceCallback.mMediaPlayer = null;
                    }
                    VideoActivity.mSubSurfaceCallback.palyVideo(movie_name);
                    VideoActivity.mSubSurfaceCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            VideoActivity.mSubSurface.setVisibility(SurfaceView.INVISIBLE);


                   //         VideoActivity.mRecorder = new VideoRecorder(context, VideoActivity.saveDir,
                   //                 VideoActivity.mMainSurface, context.getResources());
                   //         VideoActivity.mSmallSurface.setVisibility(SurfaceView.INVISIBLE);

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
                Toast.makeText(context, "ぬるぽ"+e, Toast.LENGTH_LONG).show();
                return false;
            }
            return false;
        }
    }
    /*
    class EventLogListItemTouchListener implements ListView.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                float crt_lat = event.getX();
                if(preLat == 0)
                    preLat = event.getX();
                if(crt_lat < preLat ){
                    cntLat++;
                }
                if(cntLat > trgNum)
                    editEvent();
            }

        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv_event_log      = (ListView) parent;
            int   id_of_event_log = (int)lv_event_log.getItemAtPosition(position);
            if(id_of_event_log == preId){
                cntDoubleClick++;
                if(cntDoubleClick >= 2) {

                    cntDoubleClick=0;
                }
            }else if (preId == -1){
                cntDoubleClick++;
            }
            preId = id_of_event_log;
        }
    }
*/
    public void addEvent(int team, int number){
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
        Log.d(TAG,"insert_values:"+values);
        mDb.insert(
                EventContract.Event.TABLE_NAME,
                null,
                values);
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
    private void editEvent(int evevt_id){
        HashMap<String,String> row = EventDbHelper.getRowFromID(context,evevt_id);
        //      Toast.makeText(context,row+"",Toast.LENGTH_SHORT).show();
        //--- edit eventlog
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.event_editor_view,null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("イベント編集");
        builder.setView(layout);
        builder.setCancelable(true);

        record_id = evevt_id;
        team      = Integer.parseInt(row.get(EventContract.Event.COL_TEAM));
        num       = Integer.parseInt(row.get(EventContract.Event.COL_NUM));
        point     = Integer.parseInt(row.get(EventContract.Event.COL_POINT));
        success   = Integer.parseInt(row.get(EventContract.Event.COL_SUCCESS));
        event     = row.get(EventContract.Event.COL_EVENT);
        Log.d("edit","id:"+record_id+",team:"+team+",num:"+num+",point:"+point+
                ",success:"+success+",event:"+event);

        RadioButton rdobtn;
        if(team == 0) {
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_ourteam);
            rdobtn.setChecked(true);
        }else if(team == 1){
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_oppteam);
            rdobtn.setChecked(true);
        }
        if(event.equals("foul")){
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_foul);
            rdobtn.setChecked(true);
        }else if(event.equals("rebound")){
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_rebound);
            rdobtn.setChecked(true);
        }else if(event.equals("shoot")){
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_shoot);
            rdobtn.setChecked(true);
            if(point == 1){
                rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_shoot01);
                rdobtn.setChecked(true);
            }else if(point == 2){
                rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_shoot02);
                rdobtn.setChecked(true);
            }else if(point == 3){
                rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_shoot03);
                rdobtn.setChecked(true);
            }
        }
        if(success == 1){
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_success);
            rdobtn.setChecked(true);
        }else if(success == 0){
            rdobtn = (RadioButton)layout.findViewById(R.id.rdobtn_failed);
            rdobtn.setChecked(true);
        }

        Spinner spinner = (Spinner) layout.findViewById(R.id.spn_num);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(int i=0;i<Team.sMaxMembers;i++){
            adapter.add((i+4)+"");
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(String.valueOf(num)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                try {
                    num = Integer.parseInt(item);
                }catch (NumberFormatException e){
                    Log.d("edit_spinerlistener",e+"");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //--- radiobtn listenner---
        builder.setNeutralButton("戻る", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup rg = (RadioGroup) layout.findViewById(R.id.rdog_team);
                int id = rg.getCheckedRadioButtonId();
                if(id == R.id.rdobtn_ourteam) {
                    team = 0;
                }else if(id == R.id.rdobtn_oppteam){
                    team = 1;
                }

                rg = (RadioGroup) layout.findViewById(R.id.rdog_event);
                id = rg.getCheckedRadioButtonId();
                switch (id){
                    case R.id.rdobtn_foul:
                        event = "foul";
                        point=0;
                        success=1;
                        break;
                    case R.id.rdobtn_steal:
                        event = "steal";
                        point=0;
                        success=1;
                        break;
                    case R.id.rdobtn_rebound:
                        event = "rebound";
                        point=0;
                        success=1;
                        break;
                    case R.id.rdobtn_shoot:
                        event = "shoot";
                        rg = (RadioGroup) layout.findViewById(R.id.rdog_success);
                        id = rg.getCheckedRadioButtonId();
                        if(id == R.id.rdobtn_success){
                            success = 1;
                        }else if(id == R.id.rdobtn_failed){
                            success = 0;
                        }
                        rg = (RadioGroup) layout.findViewById(R.id.rdog_shoot);
                        id = rg.getCheckedRadioButtonId();
                        if(id == R.id.rdobtn_shoot01){
                            point = 1;
                        }else if(id == R.id.rdobtn_shoot02){
                            point = 2;
                        }else if(id == R.id.rdobtn_shoot03) {
                            point = 3;
                        }
                        break;
                }
                EventDbHelper mDbHelper = new EventDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Log.d("---from edit save btn", "id:"+record_id+",team:"+team+",num:"+num
                        +",point:"+point+",success:"+success+",event:"+event);
                Boolean result = EventDbHelper.updateColumn(db,record_id,team,num,point,success,event);
                Log.d("---edit_save_result:",""+result);

                EventLogger ev = new EventLogger(context);

                ev.updateEventLog(context, VideoActivity.lv_eventLog);
                VideoActivity.updateScoreView();
            }
        });
        builder.setNegativeButton("削除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Boolean result = EventDbHelper.deleteRow(context,record_id);
                Log.d("btn_edit_delete",result+"");
                EventLogger ev = new EventLogger(context);
                ev.updateEventLog(context, VideoActivity.lv_eventLog);
                VideoActivity.updateScoreView();
            }
        });
        builder.show();
    }
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d("TAG", "ダブルタップが発生した。");

            return super.onDoubleTap(event);
        }
    }
}
