package com.example.kohki.withmanager;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Kohki on 2016/06/28.
 */
public class VideoActivity extends Activity {
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    private ArrayAdapter<String> adptList;


    //to reset DB when start Activity
    private EventDbHelper mDbHelper;
    private SQLiteDatabase db;

    private Camera mCamera;
    private final static String TAG = "VideoActivity";
    private Context context;
    public static int movie_time = 5000;

    private String sava_dir  = "/storage/emulated/legacy/WithManager/";
//    private String sava_dir = "sdcard/WithManager/";

    private int shoot_point;
    private int is_success; //True:1, False:0

    private VideoRecorder mRecorder = null;

    public static SurfaceView mOverLaySurfaceView;
    public static SurfaceHolder mOverLayHolder;
    public static PreviewSurfaceViewCallback mPreviewCallback;

    private boolean is_playing;
    private int mode_of_menu = 0;  //0:eventlog, 1:score, 2:foul

    private EventLogger mEventLogger;

    public static int[] who_is_actor = {-1,-1};
    //[0] is team.-1:? 0:myteam 1:enemyteam
    //[1] is number, -1 is ? 4...

    private Button btn_start;
    private Button btn_stop;
    private Button shoot_success1p;
    private Button shoot_success2p;
    private Button shoot_success3p;
    private Button shoot_failed1p;
    private Button shoot_failed2p;
    private Button shoot_failed3p;
    private Button foul;

    private ListView our_team;
    private ListView opt_team;
    private SimpleDateFormat sdf;
    private String gameStartDateTime;

    private Team mTeam1;
    private Team mTeam2;
    public static int our_member_num = 18;
    public static int opp_member_num = 18;



    private FrameLayout mainLayout;
    private SurfaceView mainSurfaceview;
    private ImageView image_menu;
    boolean flg_image = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_standalone);

        context = this;

        mDbHelper = new EventDbHelper(context);
        db = mDbHelper.getWritableDatabase();
        //TODO:
        mDbHelper.onUpgrade(db, EventDbHelper.DATABASE_VERSION, EventDbHelper.DATABASE_VERSION);
        //mDbHelper.createTableGame(db);


        //main surfaceview
        final SurfaceView main_surface = (SurfaceView) findViewById(R.id.main_surface);
        mRecorder = new VideoRecorder(context, sava_dir, main_surface, getResources());

        //--- experiment
        mainLayout = (FrameLayout) findViewById(R.id.camera_screen);
        mainSurfaceview = (SurfaceView) findViewById(R.id.main_surface) ;
    //    AsyncTaskListener atl = new AsyncTaskListener(this, mainLayout, mainSurfaceview);
    //    atl.execute("para");

        mainSurfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //   Log.d(TAG,event.getX()+":"+event.getY());
                FrameLayout.LayoutParams mLayoutParms = new FrameLayout.LayoutParams(300, 300);

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("TouchEvent", "getAction()" + "ACTION_DOWN");

                    image_menu = new ImageView(context);
                    image_menu.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hitagi));

                    mLayoutParms.leftMargin = (int) event.getX() - 500;
                    mLayoutParms.topMargin  = (int) event.getY() - 500;

                    mainLayout.addView(image_menu, mLayoutParms);
                    flg_image = true;
                    image_menu.setTag(1);

               /*     image_menu.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(context,"image touch!",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
                 */   /*
                    FrameLayout fl = (FrameLayout) findViewById(R.id.camera_screen);

                    for(int i=0;i<fl.getChildCount();i++){
                        //     if(fl.getChildAt(i).getTag(10) != null)
                        Log.d("TouchEvent", "V:"+i+fl.getChildAt(i));
                    }
                    ImageView im = (ImageView)fl.getChildAt(8);
                    im.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(context,"image touch!",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
                    */
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("TouchEvent", "getAction()" + "ACTION_UP");
                    mainLayout.removeView(image_menu);
                    flg_image = false;

                }else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    //   Log.d("TouchEvent", "getAction()" + "ACTION_MOVE");

                }

                // MotionEvent.ACTION_CANCEL:
                // Log.d("TouchEvent", "getAction()" + "ACTION_CANCEL");

                return false;
            }
        });




        //---
        //sub surfaceview
        mOverLaySurfaceView = (SurfaceView) findViewById(R.id.sub_surface);
        mOverLayHolder = mOverLaySurfaceView.getHolder();
        mOverLayHolder.setFormat(PixelFormat.TRANSLUCENT);//ここで半透明にする
        mPreviewCallback = new PreviewSurfaceViewCallback(context);
        mOverLayHolder.addCallback(mPreviewCallback);
        mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);

        try {
            File dir_save = new File(sava_dir);
            if(!dir_save.exists())
                dir_save.mkdir();
        } catch (Exception e) {
            Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
        }

        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //Start button
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder != null) {
                    Date date = new Date();
                    gameStartDateTime = sdf.format(date);
                    System.out.println("Game start at "+gameStartDateTime);
                    mEventLogger.addGameTime(gameStartDateTime);
                    is_playing = true;
                    btn_start.setVisibility(View.INVISIBLE);
                    btn_stop.setVisibility(View.VISIBLE);
                    mRecorder.start();
                }
            }
        });

        //Recording stop
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_playing = false;
                btn_start.setVisibility(View.VISIBLE);
                btn_stop.setVisibility(View.INVISIBLE);
                mRecorder.stop();
            }
        });

        mEventLogger = new EventLogger(context, (ListView) findViewById(R.id.event_log));

        shoot_success1p = (Button)findViewById(R.id.shoot_success_1p);
        shoot_success1p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "1P成功", Toast.LENGTH_SHORT).show();
                    //recordEvent(1,1,"shoot");//1:point,2:is success?,3:event name
                    Team.event_name = "shoot";
                    shoot_point = 1;
                    is_success = 1;
                }
            }
        });
        shoot_success2p = (Button)findViewById(R.id.shoot_success_2p);
        shoot_success2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "2P成功", Toast.LENGTH_SHORT).show();
                    //recordEvent(2,1,"shoot");//1:point,2:is success?,3:event name
                    Team.event_name = "shoot";
                    shoot_point = 2;
                    is_success = 1;
                }
            }
        });
        shoot_success3p = (Button)findViewById(R.id.shoot_success_3p);
        shoot_success3p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "3P成功", Toast.LENGTH_SHORT).show();
                    //recordEvent(3,1,"shoot");//1:point,2:is success?,3:event name
                    Team.event_name = "shoot";
                    shoot_point = 3;
                    is_success = 1;
                }
            }
        });
        shoot_failed1p = (Button)findViewById(R.id.shoot_failed_1p);
        shoot_failed1p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "1P失敗", Toast.LENGTH_SHORT).show();
                    //recordEvent(1,0,"shoot");//1:point,2:is success?,3:event name
                    Team.event_name = "shoot";
                    shoot_point = 1;
                    is_success = 0;
                }
            }
        });
        shoot_failed2p = (Button)findViewById(R.id.shoot_failed_2p);
        shoot_failed2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "2P失敗", Toast.LENGTH_SHORT).show();
                    //recordEvent(2,0,"shoot");//1:point,2:is success?,3:event name
                    Team.event_name = "shoot";
                    shoot_point = 2;
                    is_success = 0;
                }
            }
        });
        shoot_failed3p = (Button)findViewById(R.id.shoot_failed_3p);
        shoot_failed3p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "3P失敗", Toast.LENGTH_SHORT).show();
                    //recordEvent(3,0,"shoot");//1:point,2:is success?,3:event name
                    Team.event_name = "shoot";
                    shoot_point = 3;
                    is_success = 0;
                }
            }
        });
        foul = (Button)findViewById(R.id.foul);
        foul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_playing) {
                    Toast.makeText(context, "ファウル", Toast.LENGTH_SHORT).show();
                    //recordEvent(0,1,"foul");
                    Team.event_name = "foul";
                    shoot_point = 0;
                    is_success = 1;
                }
            }
        });

        findViewById(R.id.btn_chenge_scoresheet_and_eventlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
                mode_of_menu++;
                if(mode_of_menu >= 3)
                    mode_of_menu = 0;

                switch (mode_of_menu){
                    case 0://eventlog
                        LinearLayout foulsheet = (LinearLayout) findViewById(R.id.foulsheet);
                        menu.removeView(foulsheet);
                        getLayoutInflater().inflate(R.layout.event_log, menu);
                        mEventLogger = new EventLogger(context, (ListView) findViewById(R.id.event_log));

                        break;
                    case 1://scoresheet
                        LinearLayout eventlog = (LinearLayout) findViewById(R.id.menu_log);
                        menu.removeView(eventlog);
                        getLayoutInflater().inflate(R.layout.score_sheet, menu);
                        setScoresheet();

                        break;
                    case 2:
                        LinearLayout scoresheet = (LinearLayout) findViewById(R.id.scoresheet);
                        menu.removeView(scoresheet);
                        getLayoutInflater().inflate(R.layout.foul_sheet, menu);
                        setFoulsheet();

                        break;
                    default:
                        break;
                }
            }
        });

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent itt_setting = new Intent(context, SettingOfGameActivity.class);
                    startActivity(itt_setting);
                }catch (Exception e) {
                    Log.v("IntentErr:", e.getMessage() + "," + e);
                }
            }
        });
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

        ScoreDataGenerater cScoreData = new ScoreDataGenerater(context);
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
        //(TextView)findViewById(R.id.name).setBackgroundColor();
    }

    private void setFoulsheet(){

        ListView listView_our = (ListView) findViewById(R.id.our_foul_list);
        ListView listView_opt = (ListView) findViewById(R.id.opp_foul_list);
        //リストに追加するためのアダプタ
        FoulsheetArrayAdapter adpt_our = new FoulsheetArrayAdapter(getApplicationContext(), R.layout.foul_sheet_row);
        FoulsheetArrayAdapter adpt_opt = new FoulsheetArrayAdapter(getApplicationContext(), R.layout.foul_sheet_row);

        Parcelable state_our = listView_our.onSaveInstanceState();
        Parcelable state_opt = listView_opt.onSaveInstanceState();

        listView_our.setAdapter(adpt_our);
        listView_our.onRestoreInstanceState(state_our);
        listView_opt.setAdapter(adpt_opt);
        listView_opt.onRestoreInstanceState(state_opt);

        FoulCounter cFoulCounter = new FoulCounter(context, gameStartDateTime);
        List<Integer[]> foulList = cFoulCounter.getFoulData();
        Integer[] ourteam_foul = foulList.get(0);//[0]is?,[1]is4,[2]is5...
        Integer[] oppteam_foul = foulList.get(1);
        //ourteam
        int foulsum=0;
        for(int foulcount : ourteam_foul){//先にチームファウルを出力
            foulsum += foulcount;
        }
        adpt_our.add(new String[]{"team_kind","ourteam"});
        adpt_our.add(new String[]{"T", String.valueOf(foulsum)});
        for(int i=1;i<ourteam_foul.length;i++){
            adpt_our.add(new String[]{String.valueOf(i+3), String.valueOf(ourteam_foul[i])});
        }

        //oppteam
        foulsum=0;
        for(int foulcount : oppteam_foul){
            foulsum += foulcount;
        }
        adpt_opt.add(new String[]{"team_kind","oppteam"});
        adpt_opt.add(new String[]{"T",String.valueOf(foulsum)});
        for(int i=1;i<ourteam_foul.length;i++){
            adpt_opt.add(new String[]{String.valueOf(i+3),String.valueOf(oppteam_foul[i])});
        }
    }

    public boolean replay(String movie_name){
        mOverLaySurfaceView.setVisibility(SurfaceView.VISIBLE);
        try {
            if (mPreviewCallback.mMediaPlayer != null) {
                mPreviewCallback.mMediaPlayer.release();
                mPreviewCallback.mMediaPlayer = null;
            }
            mPreviewCallback.palyVideo(movie_name);
            mPreviewCallback.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(context, "ぬるぽ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public void recordEvent(int point, int is_success,String event_name) {
        if (!is_playing) return;
        //TODO:録画中でないとエラー
        String file_name = "no file";
        //   if(mRecorder.mCamera != null) {
        mRecorder.stop();
        file_name = mRecorder.save();
        mRecorder.start();
        //   }
        if (event_name.equals("shoot") && is_success == 1) {
            final TextView tv_our_score = (TextView) findViewById(R.id.our_score);
            int our_score = Integer.parseInt(tv_our_score.getText().toString());

            final TextView tv_opp_score = (TextView) findViewById(R.id.opposing_score);
            int opp_score = Integer.parseInt(tv_opp_score.getText().toString());

            switch (Team.who_is_actor[0]) {
                case 0:
                    int our_point = our_score + point;
                    tv_our_score.setText(Integer.toString(our_point));
                    //    Toast.makeText(context,"味方チーム"+ who_is_acter[1]+"番 得点！",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    int opp_point = opp_score + point;
                    tv_opp_score.setText(Integer.toString(opp_point));
                    //    Toast.makeText(context,"敵チーム"+who_is_acter[1]+"番 得点！",Toast.LENGTH_SHORT).show();
                    break;
                /*case -1:
                    Toast.makeText(context, "(score)team isnt be selected", Toast.LENGTH_SHORT).show();
                    return ;
                default:
                    Toast.makeText(context, "(score)team cant be specified", Toast.LENGTH_SHORT).show();
                    return ; */
            }
        }
        mEventLogger.addEvent(Team.who_is_actor[0], Team.who_is_actor[1], point, is_success, event_name, file_name, gameStartDateTime);
        Team.resetWhoIsAct();
        if (mode_of_menu == 1) {
            setScoresheet();
        } else if (mode_of_menu == 2){
            setFoulsheet();
        }

        //mEventLogger.getFoul();
    }

    private static class FileSort implements Comparator<File> {
        public int compare(File src, File target) {
            int diff = src.getName().compareTo(target.getName());
            return diff;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        mRecorder.resume();

        our_team = (ListView) findViewById(R.id.our_team_list);
        mTeam1 = new Team(context, our_team, our_member_num);
        opt_team = (ListView) findViewById(R.id.opposing_team_list);
        mTeam2 = new Team(context, opt_team , opp_member_num);

        our_team.setOnItemClickListener(adptSelectListener1);
        opt_team.setOnItemClickListener(adptSelectListener2);
    }
    @Override
    protected void onPause() { //別アクティビティ起動時
        mRecorder.pause();
        super.onPause();
    }

    private AdapterView.OnItemClickListener adptSelectListener1 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            String item = (String) listView.getItemAtPosition(position);

            String id_name = context.getResources().getResourceEntryName(listView.getId());

            switch (id_name){
                case "our_team_list":
                    //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //    VideoActivity.who_is_acter[0] = 0;
                    Team.who_is_actor[0] = 0;
                    break;
                case "opposing_team_list":
                    //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //    VideoActivity.who_is_acter[0] = 1;
                    Team.who_is_actor[0] = 1;
                    break;
                default:
                    Toast.makeText(context, "e:"+item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //   VideoActivity.who_is_acter[0] = -1;
                    Team.who_is_actor[0] = -1;
                    break;
            }
            if(item.equals("?"))
                //   VideoActivity.who_is_acter[1] = 0;
                Team.who_is_actor[1] = 0;
            else
                //    VideoActivity.who_is_acter[1] = Integer.parseInt(item);
                Team.who_is_actor[1] = Integer.parseInt(item);

            if(Team.event_name != null) recordEvent(shoot_point, is_success, Team.event_name);

            if(!item.equals("?")) {
                adptList = mTeam1.getAdapter();
                adptList.remove(item);
                adptList.insert(item, 1);
                listView.setAdapter(adptList);
                mTeam1.sortAdapater();
            }
        }
    };
    private AdapterView.OnItemClickListener adptSelectListener2 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            String item = (String) listView.getItemAtPosition(position);

            String id_name = context.getResources().getResourceEntryName(listView.getId());

            switch (id_name){
                case "our_team_list":
                    //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //    VideoActivity.who_is_acter[0] = 0;
                    Team.who_is_actor[0] = 0;
                    break;
                case "opposing_team_list":
                    //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //    VideoActivity.who_is_acter[0] = 1;
                    Team.who_is_actor[0] = 1;
                    break;
                default:
                    Toast.makeText(context, "e:"+item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //   VideoActivity.who_is_acter[0] = -1;
                    Team.who_is_actor[0] = -1;
                    break;
            }
            if(item.equals("?"))
                //   VideoActivity.who_is_acter[1] = 0;
                Team.who_is_actor[1] = 0;
            else
                //    VideoActivity.who_is_acter[1] = Integer.parseInt(item);
                Team.who_is_actor[1] = Integer.parseInt(item);

            if(Team.event_name != null) recordEvent(shoot_point, is_success, Team.event_name);

            if(!item.equals("?")) {
                adptList = mTeam2.getAdapter();
                adptList.remove(item);
                adptList.insert(item, 1);
                listView.setAdapter(adptList);
                mTeam2.sortAdapater();
            }
        }
    };
}