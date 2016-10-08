package com.example.kohki.withmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kohki on 2016/06/28.
 */
public class VideoActivity extends Activity {

    private static final String TAG = "VideoAct";
    private static Context context;

    private EventDbHelper cDbHelper;
    protected static SQLiteDatabase mDB;
    public static VideoRecorder mRecorder = null;
    protected EventLogger mEventLogger;

    public static SurfaceView mMainSurface;

    public static SurfaceView   mSubSurface;
    public static SurfaceHolder mSubHolder;
    public static PreviewSurfaceViewCallback mSubSurfaceCallback;

    public static SurfaceView mSmallSurface;
    public static SurfaceHolder mSmallHolder;

    private ListView lv_mOurTeamList;
    private ListView lv_mOppTeamList;

    private Button btn_start;
    private Button btn_stop;
    protected static ListView lv_eventLog;

    private static TextView tv_ourScore;
    private static TextView tv_oppScore;
    protected static TextView tv_sMessageBar;

    private SimpleDateFormat sdf;

    protected static int sPoint;
    protected static int sSuccess;
    protected static String sEventName;
    protected static String sMovieName;

    protected static String sGameStartDateTime;
    protected static int sCurrentQuarterNum = 1;

    public static int sMovieTime = 5000;
    public static int sOurMemberNum = 15;
    public static int sOppMemberNum = 15;

    private boolean isPlaying;
    protected static boolean isSaving = false;
    protected static int flg_eventMenu = 0;  //0:eventlog, 1:score, 2:foul

    //public static String saveDir  = "/storage/emulated/legacy/WithManager/";
    public static String saveDir = "sdcard/WithManager/";

    /* Synchro only */
    private static final Handler handler = new Handler();
    private Button btnBluetoothSettiong;
    private BluetoothUtil bu;
    protected static BluetoothDevice targetDevice = null;
    private BluetoothStatus bluetoothStatus;
    private BluetoothAdapter ba;
    protected BluetoothConnection bc;
    protected static byte buf[];
    private enum BluetoothStatus{
        ERROR("Bluetooth接続に失敗しました"),
        CONNECTING("Bluetooth接続 : 接続中"),
        CONNECTED("Bluetooth接続 : OK");

        private String message;

        private BluetoothStatus(String message){
            this.message = message;
        }

        public String toString(){
            return this.message;
        }
    }

    private String activityMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent itt =getIntent();
        String mode = itt.getStringExtra("mode");
        if(mode == null){
            Toast.makeText(this,"a",Toast.LENGTH_SHORT).show();
        }else {
            activityMode = mode;
        }
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        Log.v("widthPixels",    String.valueOf(displayMetrics.widthPixels));
        Log.v("heightPixels",   String.valueOf(displayMetrics.heightPixels));
        Log.v("xdpi",           String.valueOf(displayMetrics.xdpi));
        Log.v("ydpi",           String.valueOf(displayMetrics.ydpi));
        Log.v("density",        String.valueOf(displayMetrics.density));
        Log.v("scaledDensity",  String.valueOf(displayMetrics.scaledDensity));

        Log.v("width",          String.valueOf(display.getWidth()));       // 非推奨
        Log.v("height",         String.valueOf(display.getHeight()));      // 非推奨
        Log.v("orientation",    String.valueOf(display.getOrientation())); // 非推奨
        Log.v("refreshRate",    String.valueOf(display.getRefreshRate()));
        Log.v("pixelFormat",    String.valueOf(display.getPixelFormat()));
        Log.v("rotation",       String.valueOf(display.getRotation()));
        /* zenpad7
        V/widthPixels: 1280
        V/heightPixels: 736
10-07 14:12:55.512 16292-16292/com.example.kohki.withmanager V/xdpi: 160.0
10-07 14:12:55.512 16292-16292/com.example.kohki.withmanager V/ydpi: 160.15764
10-07 14:12:55.512 16292-16292/com.example.kohki.withmanager V/density: 1.3312501
10-07 14:12:55.512 16292-16292/com.example.kohki.withmanager V/scaledDensity: 1.3312501
10-07 14:12:55.513 16292-16292/com.example.kohki.withmanager V/width: 1280
10-07 14:12:55.514 16292-16292/com.example.kohki.withmanager V/height: 736
10-07 14:12:55.515 16292-16292/com.example.kohki.withmanager V/orientation: 1
10-07 14:12:55.517 16292-16292/com.example.kohki.withmanager V/refreshRate: 60.003002
10-07 14:12:55.517 16292-16292/com.example.kohki.withmanager V/pixelFormat: 1
10-07 14:12:55.519 16292-16292/com.example.kohki.withmanager V/rotation: 1
    */
        /*
        * phonepad6
          V/widthPixels: 1800
          /heightPixels: 1080
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/xdpi: 365.76
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/ydpi: 366.676
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/density: 3.0
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/scaledDensity: 3.0
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/width: 1800
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/height: 1080
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/orientation: 1
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/refreshRate: 60.000004
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/pixelFormat: 1
10-07 14:15:39.451 2066-2066/com.example.kohki.withmanager V/rotation: 1

         */
/*
        Log.d("---screen_size_to:",(double)width*0.8+":"+(double)height*0.8);
        mSubHolder.setFixedSize((int)((double)width*0.8),(int)((double)height*0.8));
        Log.d("---mSubHolder:",":"+width);

        phonepad6: 1800,1080
        zenpad7:
*/

        if (mode.equals("single")) {
            if (displayMetrics.widthPixels > 1500 && displayMetrics.heightPixels > 900){
                Log.d("---single--",">>>>>>>>>>");
                setContentView(R.layout.activity_record_standalone_small);
            }else {
                Log.d("---single--","<<<<<<<<<<");
                setContentView(R.layout.activity_record_standalone);
            }
        }else if(mode.equals("dual")){
            if(displayMetrics.widthPixels > 1500 && displayMetrics.heightPixels > 900)
                setContentView(R.layout.activity_record_synchro);
            else
                setContentView(R.layout.activity_record_standalone_small);
             /* Synchro only */
            buf = new byte[4];
            buf[3] = 111;
            /* --- */
        }

        context = this;
        mEventLogger = new EventLogger(context,sGameStartDateTime);

        String ref = itt.getStringExtra("ref");
        if(ref.equals("new")){
            Date date = new Date();
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sGameStartDateTime = sdf.format(date);
            mEventLogger.addGameTime(sGameStartDateTime);
        }else {
            sGameStartDateTime = ref;
        }

        //    System.out.println("Game start at "+sGameStartDateTime);
        lv_eventLog = (ListView) findViewById(R.id.event_log);
        mEventLogger.updateEventLog(context, lv_eventLog, sGameStartDateTime);

        cDbHelper = new EventDbHelper(context);
        mDB       = cDbHelper.getWritableDatabase();
        //TODO:
    //    cDbHelper.onUpgrade(mDB, EventDbHelper.DATABASE_VERSION, EventDbHelper.DATABASE_VERSION);

        //main surfaceview
        mMainSurface = (SurfaceView) findViewById(R.id.main_surface);
        //mMainSurface.setVisibility(SurfaceView.INVISIBLE);
        mRecorder = new VideoRecorder(context, saveDir, mMainSurface, getResources());

        //sub surfaceview
        mSubSurface = (SurfaceView) findViewById(R.id.sub_surface);
        mSubHolder = mSubSurface.getHolder();
        mSubSurfaceCallback = new PreviewSurfaceViewCallback(context);
        mSubHolder.addCallback(mSubSurfaceCallback);
        mSubHolder.setFormat(PixelFormat.TRANSLUCENT);//ここで半透明にする
        mSubSurface.setVisibility(SurfaceView.INVISIBLE);


        tv_ourScore    = (TextView) findViewById(R.id.our_score);
        tv_oppScore    = (TextView) findViewById(R.id.opposing_score);

        try {
            File dir_save = new File(saveDir);
            if(!dir_save.exists())
                dir_save.mkdir();
        } catch (Exception e) {
            Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
        }

        //Start button
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--- set
                if (mRecorder != null) {
                    btn_start.setVisibility(View.INVISIBLE);
                    btn_stop.setVisibility(View.VISIBLE);
                    mRecorder.start();
                    isPlaying = true;
                }

            }
        });

        //Recording stop
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = false;
                btn_start.setVisibility(View.VISIBLE);
                btn_stop.setVisibility(View.INVISIBLE);
                mRecorder.stop();
            }
        });

        switch (activityMode){
            case "single":
                findViewById(R.id.shoot_success_1p).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "1P成功", Toast.LENGTH_SHORT).show();
                            //recordEvent(1,1,"shoot");//1:point,2:is success?,3:event name
                            recordEvent(1,1,"shoot");
                        }
                    }
                });
                findViewById(R.id.shoot_success_2p).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "2P成功", Toast.LENGTH_SHORT).show();
                            recordEvent(2,1,"shoot");
                        }
                    }
                });
                findViewById(R.id.shoot_success_3p).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "3P成功", Toast.LENGTH_SHORT).show();
                            recordEvent(3,1,"shoot");
                        }
                    }
                });
                findViewById(R.id.shoot_failed_1p).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "1P失敗", Toast.LENGTH_SHORT).show();
                            recordEvent(1,0,"shoot");
                        }
                    }
                });
                findViewById(R.id.shoot_failed_2p).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "2P失敗", Toast.LENGTH_SHORT).show();
                            recordEvent(2,0,"shoot");
                        }
                    }
                });
                findViewById(R.id.shoot_failed_3p).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "3P失敗", Toast.LENGTH_SHORT).show();
                            recordEvent(3,0,"shoot");
                        }
                    }
                });
                findViewById(R.id.foul).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying && !isSaving) {
                            Toast.makeText(context, "ファウル", Toast.LENGTH_SHORT).show();
                            recordEvent(0,1,"foul");
                        }
                    }
                });
                break;
            case "dual":
                findViewById(R.id.steal).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buf[2] = 1;
                        Toast.makeText(context, "スティール", Toast.LENGTH_SHORT).show();
                        recordEvent(0,1,"steal"); //1:point,2:is success?,3:event name
                    }
                });
                findViewById(R.id.rebound).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buf[2] = 2;
                        Toast.makeText(context, "リバウンド", Toast.LENGTH_SHORT).show();
                        recordEvent(0,1,"rebound"); //1:point,2:is success?,3:event name
                    }
                });
                findViewById(R.id.foul).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buf[2] = 3;
                        Toast.makeText(context, "ファウル", Toast.LENGTH_SHORT).show();
                        recordEvent(0,1,"foul");
                    }
                });
                showBluetoothSelectDialog();
                //startConnect();
                break;
        }

        findViewById(R.id.btn_chenge_scoresheet_and_eventlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
                flg_eventMenu++;
                if(flg_eventMenu >= 3)
                    flg_eventMenu = 0;

                switch (flg_eventMenu){
                    case 0://eventlog
                        LinearLayout foulsheet = (LinearLayout) findViewById(R.id.foulsheet);
                        menu.removeView(foulsheet);
                        getLayoutInflater().inflate(R.layout.event_log, menu);
                        lv_eventLog = (ListView) findViewById(R.id.event_log);
                        mEventLogger.updateEventLog(context, lv_eventLog,sGameStartDateTime);
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
                    Log.v(TAG, e.getMessage() + "," + e);
                }
            }
        });

        findViewById(R.id.btn_save_or_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ADB_quarter_save = new AlertDialog.Builder(context);
                ADB_quarter_save.setTitle("第"+sCurrentQuarterNum+"Q の記録を完了しますか？");
            //    alertDialogBuilder.setMessage("メッセージ");
                if(sCurrentQuarterNum == 4) {
                    sCurrentQuarterNum = 1;
                }else if(sCurrentQuarterNum <= 3) {
                    ADB_quarter_save.setNeutralButton("第"+(sCurrentQuarterNum+1)+"Qの記録をする", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sCurrentQuarterNum++;
                            Toast.makeText(context,"第"+sCurrentQuarterNum+"Q 記録開始",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                ADB_quarter_save.setNegativeButton("保存して終了する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRecorder.stop();
                        mRecorder.pause();
                        Intent itt = new Intent(context, GameResultActivity.class);
                        itt.putExtra("record_mode", "single");
                        itt.putExtra("game_start_date_time", sGameStartDateTime);
                        startActivity(itt);
                    }
                });
                ADB_quarter_save.setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
                    // 何もしなくていい
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                // アラートダイアログのキャンセルが可能かどうかを設定します
                ADB_quarter_save.setCancelable(true);
                AlertDialog alertDialog = ADB_quarter_save.create();
                // アラートダイアログを表示します
                alertDialog.show();
            }
        });
    }

    protected void setScoresheet(){
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

        ScoreDataGenerater cScoreData = new ScoreDataGenerater(context, sGameStartDateTime);
        List<String[]> scoreList = cScoreData.getScoreData();
        for (String[] scoreData : scoreList) {
            if (scoreData[0].equals("0")) {
                adpt_our.add(scoreData);

            } else if (scoreData[0].equals("1")) {
                String tmp   = scoreData[1];
                scoreData[1] = scoreData[2];
                scoreData[2] = tmp;
                adpt_opt.add(scoreData);
            }
        }
        //(TextView)findViewById(R.id.name).setBackgroundColor();
    }

    protected void setFoulsheet(){
        ListView lv_ourfoul;
        ListView lv_oppfoul;

        lv_ourfoul = (ListView) findViewById(R.id.our_foul_list);
        lv_oppfoul = (ListView) findViewById(R.id.opp_foul_list);
        //リストに追加するためのアダプタ
        FoulsheetArrayAdapter adpt_our_foulsheet = new FoulsheetArrayAdapter(getApplicationContext(), R.layout.foul_sheet_row);
        FoulsheetArrayAdapter adpt_opp_foulsheet = new FoulsheetArrayAdapter(getApplicationContext(), R.layout.foul_sheet_row);

        //REVIEW: Instance state is needed ?
        Parcelable state_our = lv_ourfoul.onSaveInstanceState();
        lv_ourfoul.setAdapter(adpt_our_foulsheet);
        lv_ourfoul.onRestoreInstanceState(state_our);

        Parcelable state_opt = lv_oppfoul.onSaveInstanceState();
        lv_oppfoul.setAdapter(adpt_opp_foulsheet);
        lv_oppfoul.onRestoreInstanceState(state_opt);

        List<Integer[]> foulList = FoulCounter.getFoulData(mDB, sGameStartDateTime);

        Integer[] ourmember_foul = foulList.get(0);//[0]is?,[1]is4,[2]is5...
        Integer[] ourteam_foul   = foulList.get(1);
        Integer[] oppmember_foul = foulList.get(2);
        Integer[] oppteam_foul   = foulList.get(3);

        //ourteam
        adpt_our_foulsheet.add(new String[]{"0","ourteam"});
        adpt_opp_foulsheet.add(new String[]{"0","oppteam"});

        adpt_our_foulsheet.add(new String[]{"1", String.valueOf(ourteam_foul[sCurrentQuarterNum-1])});
        adpt_opp_foulsheet.add(new String[]{"1", String.valueOf(oppteam_foul[sCurrentQuarterNum-1]) });

        for(int i=0; i<ourmember_foul.length; i++){
            adpt_our_foulsheet.add(new String[]{String.valueOf(i+4), String.valueOf(ourmember_foul[i])});
        }
        for(int i=0; i<oppmember_foul.length; i++){
            adpt_opp_foulsheet.add(new String[]{String.valueOf(i+4), String.valueOf(oppmember_foul[i])});
        }
    }

    public void recordEvent(int point, int is_success,String event_name) {
        if (!isPlaying) return;

        sPoint       = point;
        sSuccess     = is_success;
        sEventName   = event_name;

        mRecorder.stop();
        sMovieName = mRecorder.save();
        mRecorder.start();

        isSaving = true;
    }

    public static void updateScoreView(){ //is accessed from Team.java
        isSaving = false;
        //---点数更新fromDB
        ArrayList column = EventDbHelper.getRowFromSuccessShoot(mDB, sGameStartDateTime);
    //    Toast.makeText(context,"column:"+column.size(),Toast.LENGTH_SHORT).show();
        int our_score = 0;
        int opp_score = 0;
        for(int i=0;i<column.size();i++){
            try {
                Integer[] row = (Integer[]) column.get(i);
            //    Toast.makeText(context,"ID"+row[0]+",TEAM:"+row[1]+",POINT"+row[2],Toast.LENGTH_SHORT).show();
                if(row[1] == 0){
                    our_score = our_score + row[2];
                }else if(row[1] == 1){
                    opp_score = opp_score + row[2];
                }
            }catch (NumberFormatException e){
                Log.w(TAG,e+"");
            }
        }
        tv_ourScore.setText(our_score+"");
        tv_oppScore.setText(opp_score+"");
        //---
    }

    @Override
    public void onResume(){
        super.onResume();
        mRecorder.resume();

        //---Team 更新
        lv_mOurTeamList = (ListView) findViewById(R.id.our_team_list);
        lv_mOppTeamList = (ListView) findViewById(R.id.opposing_team_list);

        Team cOurTeam = new Team(context, lv_mOurTeamList,  sOurMemberNum);
        Team cOppTeam = new Team(context, lv_mOppTeamList , sOppMemberNum);

        Team.TeamSelectListener our_team_lisener = cOurTeam.new TeamSelectListener();
        Team.TeamSelectListener opp_team_lisener = cOppTeam.new TeamSelectListener();

        lv_mOurTeamList.setOnItemClickListener(our_team_lisener);
        lv_mOppTeamList.setOnItemClickListener(opp_team_lisener);
        //---
    }
    @Override
    protected void onPause() { //別アクティビティ起動時
        mRecorder.pause();
        super.onPause();
    }

    /*
    *
    * bluetoot method
    *
    *
    * */

    private void showBluetoothSelectDialog(){
        this.bu = new BluetoothUtil();

        if (!this.bu.isSpported()) // 非対応デバイス
            DialogBuilder.showErrorDialog(this, "Bluetooth非対応デバイスです。");
        else if (!this.bu.isEnabled()) // 設定無効
            DialogBuilder.showErrorDialog(this, "Bluetooth有効にしてください。");
        else if (this.bu.getPairingCount() == 0) // ペアリング済みデバイスなし
            DialogBuilder.showErrorDialog(this, "ペアリング済みのBluetooth設定がありません。");
        else{
            new DialogBuilder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Bluetoothデバイス選択")
                    .setItems(bu.getDeviceNames(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            decideBluetoothDevice(bu.getDevices()[which]);
                        }
                    })
                    .setNegativeButton("キャンセル", null)
                    .show("Bluetoothデバイス選択");

        }
    }
    private void decideBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.targetDevice = bluetoothDevice;
        Toast.makeText(context, targetDevice.getName() + "が選択されました", Toast.LENGTH_SHORT).show();

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(targetDevice.getName() + "が選択されました")
                .setMessage("同期を開始します")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startConnect();
                    }
                }).show();
    }
    private void startConnect() {
        bluetoothStatus = BluetoothStatus.CONNECTING;

        bc = new BluetoothConnection();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("接続中");
        progressDialog.setCancelable(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (bc != null) {
                    bc.close();
                    bc = null;
                }
            }
        });
        progressDialog.show();
        //Bluetooth接続スレッド
        new Thread(new Runnable(){
            @Override
            public void run(){

            }
        }).start();

        //接続待機
        new Thread(new Runnable(){
            @Override
            public void run(){
                ba = BluetoothAdapter.getDefaultAdapter();
                bluetoothStatus = bc.makeServer(ba) ? BluetoothStatus.CONNECTED : BluetoothStatus.ERROR;

                System.out.println("refresh");
                refreshProgressMessage(progressDialog);
            }
        }).start();
    }
    private void startSendConnect(){
        bluetoothStatus = BluetoothStatus.CONNECTING;

        bc = new BluetoothConnection();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("接続中");
        progressDialog.setCancelable(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(bc != null){
                    bc.close();
                    bc = null;
                }
            }
        });
        progressDialog.show();

        //Bluetooth接続スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接続
                while(progressDialog.isShowing() && bluetoothStatus != BluetoothStatus.CONNECTED){

                    bluetoothStatus = bc.connectToServer(targetDevice) ? BluetoothStatus.CONNECTED : BluetoothStatus.CONNECTING;

                    if(bluetoothStatus == BluetoothStatus.CONNECTED)
                        refreshProgressMessage(progressDialog);
                    else
                        Util.sleep(2000);
                }
            }
        }).start();
    }
    private void refreshProgressMessage(final ProgressDialog dialog) {
        System.out.println("refreshきたよ");
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (dialog) {
                    if(!dialog.isShowing()){

                    }
                    else if(bluetoothStatus == BluetoothStatus.CONNECTED){ //両方接続完了
                        dialog.dismiss();
                        new Thread(bluetoothReceiveRunnable).start();
                        //startInside();
                    }
                    else if(bluetoothStatus == BluetoothStatus.ERROR){ //Bluetooth接続エラー
                        dialog.cancel();
                        showErrorDialog(bluetoothStatus.toString());
                    }
                    else{ //接続中
                        dialog.setMessage(bluetoothStatus.toString());
                    }
                }
            }
        });
    }
    private final Runnable bluetoothReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            try{
                Log.d("MaA","El03");
                while(true){
                    bluetoothReceive();
                }
            }
            catch(Exception e){
                Log.d("MaA","El01");
            }
            //endCheck();
        }
    };
    public void bluetoothReceive(){
        int i;
        int[] j = new int[5];
        int x = 0;
        while((i = bc.readObject()) != -1){
            System.out.println(i);
            j[x] = i;
            if(i == 111){
                final int[] t = j;
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        bluetoothRecordScore(t);
                    }
                });

                break;
            }
            x++;
        }
    }
    private void showErrorDialog(String message){
        DialogBuilder.showErrorDialog(this, message);
    }

    public void bluetoothRecordScore(int[] buf){
        //buf[0] = team: 0 or 1, buf[1] = actor: 4 ~ 18 , buf[2] = point: 1 or 2 or 3, buf[3] = is_success: 0,1
        if(!isPlaying) return ;
        //TODO:録画中でないとエラー
        String file_name = "no file";
        //   if(mRecorder.mCamera != null) {
        mRecorder.stop();
        file_name = mRecorder.save();
        mRecorder.start();

        int team  = buf[0];
        int actor = buf[1];
        int point = buf[2];
        int is_success = buf[3];

        if(is_success == 1) {
            final TextView tv_our_score = (TextView) findViewById(R.id.our_score);
            int our_score = Integer.parseInt(tv_our_score.getText().toString());

            final TextView tv_opp_score = (TextView) findViewById(R.id.opposing_score);
            int opp_score = Integer.parseInt(tv_opp_score.getText().toString());

            switch (team) {
                case 0:
                    int our_point = our_score + point;
                    tv_our_score.setText(Integer.toString(our_point));//intをsetText()すると落ちる
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
                return ;  */
            }
        }
        mEventLogger.addEvent(team, actor, point, is_success, "shoot",
                file_name, sGameStartDateTime, sCurrentQuarterNum);
        if (flg_eventMenu == 0) {
            mEventLogger.updateEventLog(context, VideoActivity.lv_eventLog,sGameStartDateTime);
        } else if (flg_eventMenu == 1){
            setScoresheet();
        }else if(flg_eventMenu == 2) {
            setFoulsheet();
        }
        updateScoreView();
    }
}