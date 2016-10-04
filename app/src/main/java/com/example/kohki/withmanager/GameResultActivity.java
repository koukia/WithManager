package com.example.kohki.withmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

/**
 * Created by kohki on 16/10/03.
 */
public class GameResultActivity extends AppCompatActivity {

    private static final String TAG = "GameResultAct";
    private static final int HOME = 0;

    private String gameStartDateTime;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Intent itt = getIntent();
        String mode = itt.getStringExtra("record_mode");
        if (mode != null) {
            switch (mode) {
                case "single":
                    setContentView(R.layout.activity_gameresult_main);
                //    Toast.makeText(this,"single",Toast.LENGTH_LONG).show();
                    break;
                case "synchronized_main":
                    setContentView(R.layout.activity_gameresult_main);
                    break;
                case "synchronized_sub":
                    setContentView(R.layout.activity_gameresult_sub);
                    break;
            }
        }else
            Log.d(TAG, "intent of record_mode is null");

        gameStartDateTime= itt.getStringExtra("game_start_date_time");
        if(gameStartDateTime == null){
            Toast.makeText(this, "試合記録はありません", Toast.LENGTH_SHORT).show();
        }else {
            LinearLayout event_log   = (LinearLayout) findViewById(R.id.event_log_layout);
            LinearLayout score_sheet = (LinearLayout) findViewById(R.id.score_sheet_layout);
            LinearLayout foul_sheet  = (LinearLayout) findViewById(R.id.foul_sheet_layout);
            getLayoutInflater().inflate(R.layout.event_log,   event_log);
            getLayoutInflater().inflate(R.layout.score_sheet, score_sheet);
            getLayoutInflater().inflate(R.layout.foul_sheet,  foul_sheet);
            EventDbHelper  cDbHelper = new EventDbHelper(context);
            SQLiteDatabase mDB       = cDbHelper.getWritableDatabase();
            EventLogger.updateEventLog(context, (ListView) findViewById(R.id.event_log));
            setScoresheet();
            setFoulsheet();

        }
    }

    private void setScoresheet(){
        ListView listView_our, listView_opt;
        ItemArrayAdapter adpt_our, adpt_opt;

        //スコアシートのリストビュー
        listView_our = (ListView) findViewById(R.id.listView_our);
        listView_opt = (ListView) findViewById(R.id.listView_opt);
        //リストに追加するためのアダプタ
        adpt_our = new ItemArrayAdapter(getApplicationContext(), R.layout.item_rusult);
        adpt_opt = new ItemArrayAdapter(getApplicationContext(), R.layout.item_rusult);


        Parcelable state_our = listView_our.onSaveInstanceState();
        Parcelable state_opt = listView_opt.onSaveInstanceState();

        listView_our.setAdapter(adpt_our);
        listView_our.onRestoreInstanceState(state_our);
        listView_opt.setAdapter(adpt_opt);
        listView_opt.onRestoreInstanceState(state_opt);

        ScoreDataGenerater cScoreData = new ScoreDataGenerater(context, gameStartDateTime);
        List<String[]> scoreList = cScoreData.getScoreData();
        for (String[] scoreData : scoreList) {
            if (scoreData[0].equals("0")) {
                adpt_our.add(scoreData);

            } else if (scoreData[0].equals("1")) {
                String tmp = scoreData[1];
                scoreData[1] = scoreData[2];
                scoreData[2] = tmp;
                adpt_opt.add(scoreData);
            }
        }

    }

    private void setFoulsheet(){

        ListView lv_ourfoul = (ListView) findViewById(R.id.our_foul_list);
        ListView lv_oppfoul = (ListView) findViewById(R.id.opp_foul_list);
        //リストに追加するためのアダプタ
        FoulsheetArrayAdapter adpt_our_foulsheet = new FoulsheetArrayAdapter(getApplicationContext(), R.layout.foul_sheet_row);
        FoulsheetArrayAdapter adpt_opp_foulsheet = new FoulsheetArrayAdapter(getApplicationContext(), R.layout.foul_sheet_row);

        Parcelable state_our = lv_ourfoul.onSaveInstanceState();
        lv_ourfoul.setAdapter(adpt_our_foulsheet);
        lv_ourfoul.onRestoreInstanceState(state_our);

        Parcelable state_opt = lv_oppfoul.onSaveInstanceState();
        lv_oppfoul.setAdapter(adpt_opp_foulsheet);
        lv_oppfoul.onRestoreInstanceState(state_opt);

        FoulCounter cFoulCounter = new FoulCounter(context, gameStartDateTime);
        List<Integer[]> foulList = cFoulCounter.getFoulData();
        Integer[] ourmember_foul = foulList.get(0);//[0]is?,[1]is4,[2]is5...
        Integer[] ourteam_foul   = foulList.get(1);
        Integer[] oppmember_foul = foulList.get(2);
        Integer[] oppteam_foul   = foulList.get(3);

        //ourteam
        adpt_our_foulsheet.add(new String[]{"team_kind","ourteam"});
        int quarter_num=1;
        while (ourteam_foul[quarter_num-1] != 0) {
            adpt_our_foulsheet.add(new String[]{"T", String.valueOf(ourteam_foul[quarter_num-1])});
            quarter_num++;
        }
        for(int i=0; i<ourmember_foul.length; i++){
            adpt_our_foulsheet.add(new String[]{String.valueOf(i+4), String.valueOf(ourmember_foul[i])});
        }

        //oppteam
        adpt_opp_foulsheet.add(new String[]{"team_kind","oppteam"});
        quarter_num=1;
        while (oppteam_foul[quarter_num-1] != 0) {
            adpt_our_foulsheet.add(new String[]{"T", String.valueOf(oppteam_foul[quarter_num-1])});
            quarter_num++;
        }
        for(int i=0;i<oppmember_foul.length;i++){
            adpt_opp_foulsheet.add(new String[]{String.valueOf(i+4), String.valueOf(oppmember_foul[i])});
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE, HOME, Menu.NONE, "トップへ戻る");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case HOME:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("通知");
                alert.setMessage("ホーム画面に戻ります\nよろしいですか？");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent itt_home = new Intent(getApplication(), HomeActivity.class);
                        startActivity(itt_home);
                    }
                });
                alert.show();
                return true;
        }
        return false;
    }
}
