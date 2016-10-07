package com.example.kohki.withmanager;

import android.content.Context;
import android.content.Intent;
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
    public static List getFoulData(SQLiteDatabase db, String gameStartDateTime) {
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
        ArrayList column = EventDbHelper.getRowFromFoul(db,gameStartDateTime);
        for(int i=0;i<column.size();i++){
            Integer[] row = (Integer[]) column.get(i);
            int team = row[1];
            int num = row[2];
            int quarter_num = row[3];
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

        ArrayList<Integer[]> foul_data_list = new ArrayList<>();
        foul_data_list.add(our_memberfoul_counter);
        foul_data_list.add(our_teamfoul_counter);
        foul_data_list.add(opp_memberfoul_counter);
        foul_data_list.add(opp_teamfoul_counter);

        return foul_data_list;
    }
}
