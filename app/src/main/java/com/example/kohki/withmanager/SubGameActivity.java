package com.example.kohki.withmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;

public class SubGameActivity extends AppCompatActivity {

    private final static String TAG = "SubGameActivity";
    private Context context;
    private int movie_time = 5000;

    private OutputStream outputStream;
    private PrintWriter writer;
    private String ene_team = "";
    int point;
    boolean fragUndo;
    String undoTeam;

    //試合タイマー
    private GameTimer mGameTimer;
    private boolean  is_playing;

    private String[] event_who = {"", ""};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_game);
        context = this;

        ListView lv_players1;
        ListView lv_players2;

        try {
            outputStream = openFileOutput("scoreLog.csv", Context.MODE_PRIVATE);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        writer = new PrintWriter(new OutputStreamWriter(outputStream));


        //ゲームタイマー
        TextView tv_timer = (TextView) findViewById(R.id.game_timer);
        mGameTimer = new GameTimer(1 * 60 * 1000, 1000, tv_timer, context);//(8 minutes, 1 second, - )

        //試合開始とストップ
        is_playing = false;

        final RelativeLayout mRL = (RelativeLayout) findViewById(R.id.image_layout);
        final OverlayContent ol_playing = new OverlayContent(this);

        ol_playing.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (is_playing) {
                            ol_playing.setVisibility(ol_playing.VISIBLE);
                            mGameTimer.cancel();
                            is_playing = false;
                            //        Toast.makeText(context, "is_playing true", Toast.LENGTH_SHORT).show();
                        } else {
                            ol_playing.setVisibility(ol_playing.INVISIBLE);
                            mGameTimer.start();
                            is_playing = true;
                            //         Toast.makeText(context, "is_playing false", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );
        mRL.addView(ol_playing);

        //Player set
        String[] members1 = {"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        lv_players1 = (ListView) findViewById(R.id.our_team_list);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, members1);
        lv_players1.setAdapter(adapter1);

        lv_players1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), item + " clicked",Toast.LENGTH_LONG).show();
                event_who[0] = "p1";
                event_who[1] = item;
            }
        });

        String[] members2 = {"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        lv_players2 = (ListView) findViewById(R.id.enemies_team_list);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, members2);
        lv_players2.setAdapter(adapter2);

        lv_players2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), item + " clicked",Toast.LENGTH_LONG).show();
                event_who[0] = "p2";
                event_who[1] = item;
            }
        });


        findViewById(R.id.shoot_succes_free).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                point = 1;
                recordScore(event_who[0], event_who[1], point);
            }
        });
        findViewById(R.id.shoot_success_2p).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point = 2;
                recordScore(event_who[0], event_who[1], point);
            }
        });
        findViewById(R.id.shoot_success_3p).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                point = 3;
                recordScore(event_who[0], event_who[1], point);
            }
        });
        findViewById(R.id.miss_undo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(fragUndo)
                    undo(undoTeam, -point);
            }
        });
        findViewById(R.id.steal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (event_who[0]){
                    case "p1":
                        Toast.makeText(context,"味方チーム"+event_who[1]+"番 スティール！",Toast.LENGTH_SHORT).show();
                        break;
                    case "p2":
                        Toast.makeText(context,"敵チーム"+event_who[1]+"番 スティール！",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context,"選手を選択してください",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.finish, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case R.id.finish:
                Intent itt_result = new Intent(context, Result_game.class);

                mGameTimer.cancel();
                is_playing = false;

                writer.close();
                startActivity(itt_result);

                writer.append("0");
                return true;
        }
        return false;
    }

    ArrayList<String> results = new ArrayList<String>(); //スコア等の結果を打ち込む

    private void recordScore(String who_team, String who_num, int point){

        TextView tv_our_score = (TextView)findViewById(R.id.our_score);
        int our_score = Integer.parseInt(tv_our_score.getText().toString());
        TextView tv_enemies_score = (TextView)findViewById(R.id.enemies_score);
        int enemies_score = Integer.parseInt(tv_enemies_score.getText().toString());

        writer.append("0\n");


        String result;
        switch (who_team){

            case "p1":
                int our_point = our_score + point;
                tv_our_score.setText(our_point+"");

                result = "阿南高専チーム "+ who_num + "番 得点(" + point + "点)！"; results.add(result); fragUndo = true; undoTeam = "p1";
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

//                writer.append(0 + "," + who_num + "," + point + "\n");
                writer.append(0 + "," + who_num + "," + point + ",");
                break;
            case "p2":
                int ene_point = enemies_score + point;
                tv_enemies_score.setText(ene_point+"");

                result = "敵チーム "+who_num + "番 得点(" + point + "点)！"; results.add(result); fragUndo = true; undoTeam = "p2";
                Toast.makeText(context, result ,Toast.LENGTH_SHORT).show();

//                writer.append(1 + "," + who_num + "," + point + "\n");
                writer.append(1 + "," + who_num + "," + point + ",");
                break;
            default:
                Toast.makeText(context,"選手を選択してください",Toast.LENGTH_SHORT).show();
                break;
        }

        for(String hoge : results){ //resultsに追加された結果を表示する
            System.out.println(hoge);
        }System.out.println();

    }
    public void undo(String who_team, int point){
        TextView tv_our_score = (TextView)findViewById(R.id.our_score);
        int our_score = Integer.parseInt(tv_our_score.getText().toString());
        TextView tv_enemies_score = (TextView)findViewById(R.id.enemies_score);
        int enemies_score = Integer.parseInt(tv_enemies_score.getText().toString());

        if(results.size() > 0) results.remove(results.size()-1);
        fragUndo = false;
        undoTeam = "";

        switch (who_team) {
            case "p1":
                tv_our_score.setText(our_score + point + "");
                break;
            case "p2":
                tv_enemies_score.setText(enemies_score + point + "");
                break;
        }
        writer.append("1,");
    }


    private static class FileSort implements Comparator<File> {
        public int compare(File src, File target) {
            int diff = src.getName().compareTo(target.getName());
            return diff;
        }
    }
    @Override
    public void onResume(){ //アクティビティ再び表示されたとき
        //mRecorder.resume();
        super.onResume();
    }
    @Override
    protected void onPause() { //別アクティビティ起動時
        //mRecorder.pause();
        super.onPause();
    }
//TODO:
// ava.lang.NullPointerException:
// Attempt to invoke interface method 'android.view.Display
// android.view.WindowManager.getDefaultDisplay()' on a null object reference

}

