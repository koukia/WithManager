package com.example.kohki.withmanager;

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
import java.util.List;

/**
 * Created by kohki on 16/10/01.
 */
public class FoulCounter {
    private static final String TAG = "FoulCounter";

    private EventDbHelper mDbHelper;
    private SQLiteDatabase db;
    //ListView foulsheet_list;
    private static Context context;
    private String str_GameStartTime;

    public FoulCounter(Context context, String game_start_time){
        this.context = context;
        str_GameStartTime = game_start_time;
    //    this.foulsheet_list = foulsheet_list ;
        mDbHelper = new EventDbHelper(context);
        db = mDbHelper.getWritableDatabase();
        // DB reset *****  If you delete here comment to reset DB, Commentout here !!
        //mDbHelper.onUpgrade(db, EventDbHelper.DATABASE_VERSION, EventDbHelper.DATABASE_VERSION);

        //TODO:
    //    foulsheet_list.setOnItemClickListener(new FoulListItemClickListener());
    //    foulsheet_list.setOnItemLongClickListener(new FoulListItemLongClickListener());
    }
/*
    static class FoulListItemClickListener implements ListView.OnItemClickListener {

        FoulListItemClickListener() {}

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            String item = (String) listView.getItemAtPosition(position);
            //    Toast.makeText(context,item+"",Toast.LENGTH_SHORT).show();
        }
    }
    static class FoulListItemLongClickListener  implements ListView.OnItemLongClickListener {

        FoulListItemLongClickListener() {}

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            return false;
        }
    }
*/
    public List getFoulData() {
        Integer[] ourteam_counter = new Integer[VideoActivity.our_member_num-3];//[0] => ?,[1] => num4
        Integer[] oppteam_counter = new Integer[VideoActivity.opp_member_num-3];
        for(int i=0;i<ourteam_counter.length;i++){
            ourteam_counter[i]=0;
        }
        for(int i=0;i<oppteam_counter.length;i++){
            oppteam_counter[i]=0;
        }

        /*get all data from DB*/
        try {
            SQLiteCursor c = (SQLiteCursor) db.query(
                    true, EventContract.Event.TABLE_NAME,
                    null, null, null, null, null, null, null);

            int rowcount = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < rowcount; i++) {
                String time_of_event = c.getString(c.getColumnIndex(EventContract.Event.COL_DATETIME));
                String event_name    = c.getString(c.getColumnIndex(EventContract.Event.COL_EVENT));
                String is_success    = c.getString(c.getColumnIndex(EventContract.Event.COL_SUCCESS));

                if(time_of_event.equals(str_GameStartTime) && event_name.equals("foul") && is_success.equals("1")){
                    int team = c.getInt(c.getColumnIndex(EventContract.Event.COL_TEAM));
                    int num  = c.getInt(c.getColumnIndex(EventContract.Event.COL_NUM));
                    Log.d(TAG,""+num);
                    if(team == 0){//ourteam
                        if(num == 0)//num is ?
                            ourteam_counter[num] = ourteam_counter[num] + 1;
                        else
                            ourteam_counter[num - 3] = ourteam_counter[num - 3] + 1;
                    }else if(team == 1){//oppteam
                        if(num == 0)
                            oppteam_counter[num] = oppteam_counter[num] + 1;
                        else
                            oppteam_counter[num - 3] = oppteam_counter[num - 3] + 1;
                    }
                }
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        }
        ArrayList<Integer[]> foul_data_list = new ArrayList<>();
        foul_data_list.add(ourteam_counter);
        foul_data_list.add(oppteam_counter);

        return foul_data_list;
    }
}
