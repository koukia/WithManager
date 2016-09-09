package com.example.kohki.withmanager;

/**
 * Created by Kohki on 2016/06/30.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Kohki on 2016/06/28.
 */
public class VideoActivity extends Activity {
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    private Camera mCamera;
    private final static String TAG = "VideoActivity";
    private Context context;
    private int movie_time = 5000;

    //    private String sava_path  = "/storage/emulated/0/WithManager/";
    private String sava_path  = "sdcard/WithManager/";

    private VideoRecorder mRecorder;

    private SurfaceView mOverLaySurfaceView;
    private SurfaceHolder mOverLayHolder;
    private PreviewSurfaceViewCallback mPreviewCallback;

    //試合タイマー
    private GameTimer mGameTimer;
    private boolean  is_playing;

    private String[] event_who = {"", ""};

    boolean fragUndo;
    int point;
    String undoTeam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_standalone);
        context = this;
        SurfaceView surfacec_amera = (SurfaceView) findViewById(R.id.surface_camera);
        mRecorder = new VideoRecorder(this, movie_time, sava_path, surfacec_amera, getResources());

        //オーバーレイするSurfaceView
        mPreviewCallback = new PreviewSurfaceViewCallback(context);
        mOverLaySurfaceView = (SurfaceView) findViewById(R.id.surface_preview);
        mOverLayHolder = mOverLaySurfaceView.getHolder();
        mOverLayHolder.setFormat(PixelFormat.TRANSLUCENT);//ここで半透明にする
        mOverLayHolder.addCallback(mPreviewCallback);
        mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);


        ListView lv_players1;
        ListView lv_players2;



        try {
            File dir_save = new File(sava_path);
            dir_save.mkdir();
        } catch (Exception e) {
            Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
        }
        /*
        try{
            File[] listfiles = dir_save.listFiles();
            Arrays.sort(listfiles, new FileSort());
            for(File s : listfiles) {
                System.out.println(s.getName());
                if(s.getName().matches(".*Edited_.*")){
                    Toast.makeText(context,s.getAbsolutePath()+"",Toast.LENGTH_LONG).show();
                    mRecorder.editedMovies.add(s);
                }else {
                    mRecorder.originMovies.add(s);
                    Toast.makeText(context,s.getAbsolutePath()+"",Toast.LENGTH_LONG).show();
                }
            }
        }catch(Exception e){Log.v("Err","filemake failed.");}
        */


        //Start button
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder != null)
                    mRecorder.start();
            }
        });

        //Record button
        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.save();
                    mRecorder.start();
                }
            }
        });

        //Record every 5s button
        findViewById(R.id.btn_every5s).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRecorder.stop();
                mGameTimer.cancel();
                final RelativeLayout mRL = (RelativeLayout) findViewById(R.id.image_layout);
                final OverlayContent ol_playing = new OverlayContent(context);
                is_playing = false;
                ol_playing.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View v) {
                                if (!is_playing) {
                                    ol_playing.setVisibility(ol_playing.INVISIBLE);
                                    mGameTimer.start();
                                    is_playing = true;
                                    mRecorder.start();
                                }
                            }
                        }
                );
                mRL.addView(ol_playing);
                mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);
            }
        });
        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOverLaySurfaceView.setVisibility(SurfaceView.VISIBLE);
                try {
                    //    Toast.makeText(context, mRecorder.editedMovies.size() + "", Toast.LENGTH_LONG).show();
                    if (mRecorder.editedMovies.size() > 0) {
                        if (mPreviewCallback.mMediaPlayer != null) {
                            mPreviewCallback.mMediaPlayer.release();
                            mPreviewCallback.mMediaPlayer = null;
                        }
                        mPreviewCallback.palyVideo(mRecorder.editedMovies.get(mRecorder.editedMovies.size() - 1).toString());
                        //        Toast.makeText(context, mRecorder.editedMovies.get(mRecorder.editedMovies.size() - 1).toString(), Toast.LENGTH_LONG).show();
                        mPreviewCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(context, "再生する動画がない", Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(context, "ぬるぽ", Toast.LENGTH_LONG).show();
                }
            }
        });
        //ゲームタイマー
        TextView tv_timer = (TextView) findViewById(R.id.game_timer);
        mGameTimer = new GameTimer(8 * 60 * 1000, 1000, tv_timer, context);//(8 minutes, 1 second, - )

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
                        mRecorder.start();
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

        //

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

    ArrayList<String> results = new ArrayList<String>(); //スコア等の結果を打ち込む

    private void recordScore(String who_team, String who_num, int point){

        TextView tv_our_score = (TextView)findViewById(R.id.our_score);
        int our_score = Integer.parseInt(tv_our_score.getText().toString());
        TextView tv_enemies_score = (TextView)findViewById(R.id.enemies_score);
        int enemies_score = Integer.parseInt(tv_enemies_score.getText().toString());


        String result;
        switch (who_team){

            case "p1":
                int our_point = our_score + point;
                tv_our_score.setText(our_point+"");
                result = "阿南高専チーム "+ who_num + "番 得点(" + point + "点)！"; results.add(result); fragUndo = true; undoTeam = "p1";
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                break;
            case "p2":
                int ene_point = enemies_score + point;
                tv_enemies_score.setText(ene_point+"");
                result = "敵チーム "+who_num + "番 得点(" + point + "点)！"; results.add(result); fragUndo = true; undoTeam = "p2";
                Toast.makeText(context, result ,Toast.LENGTH_SHORT).show();
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

    }
    private static class FileSort implements Comparator<File> {
        public int compare(File src, File target) {
            int diff = src.getName().compareTo(target.getName());
            return diff;
        }
    }

    @Override
    public void onResume(){ //アクティビティ再び表示されたとき
        mRecorder.resume();
        super.onResume();
    }

    @Override
    protected void onPause() { //別アクティビティ起動時
        mRecorder.pause();
        super.onPause();
    }
//TODO:
// ava.lang.NullPointerException:
// Attempt to invoke interface method 'android.view.Display
// android.view.WindowManager.getDefaultDisplay()' on a null object reference

}

