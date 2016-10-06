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
    private String gameStartDateTime;

    public FoulCounter(Context context, String gameStartDateTime){
        this.context = context;
        this.gameStartDateTime = gameStartDateTime;
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
        Integer[] our_memberfoul_counter = new Integer[Team.sMaxMembers];//[0] => num4,[1] => num5
        Integer[] opp_memberfoul_counter = new Integer[Team.sMaxMembers];
        Integer[] our_teamfoul_counter   = new Integer[4];
        Integer[] opp_teamfoul_counter   = new Integer[4];
        for(int i=0;i<our_memberfoul_counter.length;i++){
            our_memberfoul_counter[i]=0;
        }
        for(int i=0;i<opp_memberfoul_counter.length;i++){
            opp_memberfoul_counter[i]=0;
        }
        for(int i=0;i<our_teamfoul_counter.length;i++){
            our_teamfoul_counter[i]=0;
        }
        for(int i=0;i<opp_teamfoul_counter.length;i++){
            opp_teamfoul_counter[i]=0;
        }
        /*get all data from DB*/
        try {
            SQLiteCursor c = (SQLiteCursor) db.query(
                    true, EventContract.Event.TABLE_NAME,
                    null, null, null, null, null, null, null);

            int rowcount = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < rowcount; i++) {
                String event_name = c.getString(c.getColumnIndex(EventContract.Event.COL_EVENT));
                String is_success = c.getString(c.getColumnIndex(EventContract.Event.COL_SUCCESS));
                String start_time = c.getString(c.getColumnIndex(EventContract.Event.COL_DATETIME));

                if(event_name.equals("foul") && is_success.equals("1") && start_time.equals(gameStartDateTime)){
                    int team        = c.getInt(c.getColumnIndex(EventContract.Event.COL_TEAM));
                    int num         = c.getInt(c.getColumnIndex(EventContract.Event.COL_NUM));
                    int quarter_num = c.getInt(c.getColumnIndex(EventContract.Event.COL_QUARTER_NUM));

                    if(team == 0){//ourteam
                    //    if(num == 0)//num is ?
                    //        ourteam_counter[num] = ourteam_counter[num] + 1;
                        if(num >= 4) {
                            our_memberfoul_counter[num-4] = our_memberfoul_counter[num-4] + 1;
                        }
                        our_teamfoul_counter[quarter_num-1] = our_teamfoul_counter[quarter_num-1] + 1;
                    }else if(team == 1){//oppteam
                        if(num >= 4) {
                            opp_memberfoul_counter[num-4] = opp_memberfoul_counter[num-4] + 1;
                        }
                        opp_teamfoul_counter[quarter_num-1] = opp_teamfoul_counter[quarter_num-1] + 1;
                    }
                }
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        }
        ArrayList<Integer[]> foul_data_list = new ArrayList<>();
        foul_data_list.add(our_memberfoul_counter);
        foul_data_list.add(our_teamfoul_counter);
        foul_data_list.add(opp_memberfoul_counter);
        foul_data_list.add(opp_teamfoul_counter);

        return foul_data_list;
    }
}
