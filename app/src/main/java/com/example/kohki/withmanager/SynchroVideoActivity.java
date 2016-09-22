package com.example.kohki.withmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SynchroVideoActivity extends Activity {
    private static final Handler handler = new Handler();
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    //to reset DB when start Activity
    private EventDbHelper mDbHelper;
    private SQLiteDatabase db;

    private Camera mCamera;
    private final static String TAG = "SynchroVideoActivity";
    private Context context;
    private int movie_time = 5000;

    //    private String sava_path  = "/storage/emulated/legacy/WithManager/";
    private String sava_dir = "sdcard/WithManager/";

    private VideoRecorder mRecorder = null;

    public static SurfaceView mOverLaySurfaceView;
    public static SurfaceHolder mOverLayHolder;
    public static PreviewSurfaceViewCallback mPreviewCallback;

    private boolean is_playing;
    private boolean is_scoresheetview;

    private EventLogger mEventLogger;

    public static int[] who_is_acter = {-1,-1};
    //[0] is team.-1:? 0:myteam 1:enemyteam
    //[1] is number, -1 is ? 4...

    private Button btn_start;
    private Button btn_stop;

    private Button btnBluetoothSettiong;
    private BluetoothUtil bu;
    private BluetoothDevice targetDevice = null;
    private BluetoothStatus bluetoothStatus;
    private BluetoothAdapter ba;
    private BluetoothConnection bc;
    private byte buf[];
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchro_video);

        context = this;
        buf = new byte[4]; buf[3] = 111;

        //main surfaceview
        SurfaceView main_surface = (SurfaceView) findViewById(R.id.main_surface);
        mRecorder = new VideoRecorder(this, movie_time, sava_dir, main_surface, getResources());

        //sub surfaceview
        mOverLaySurfaceView = (SurfaceView) findViewById(R.id.sub_surface);
        mOverLayHolder = mOverLaySurfaceView.getHolder();
        mOverLayHolder.setFormat(PixelFormat.TRANSLUCENT);//ここで半透明にする
        mPreviewCallback = new PreviewSurfaceViewCallback(context);
        mOverLayHolder.addCallback(mPreviewCallback);
        mOverLaySurfaceView.setVisibility(SurfaceView.INVISIBLE);

        mDbHelper = new EventDbHelper(context);
        db = mDbHelper.getWritableDatabase();
        mDbHelper.onUpgrade(db, EventDbHelper.DATABASE_VERSION, EventDbHelper.DATABASE_VERSION);

        try {
            File dir_save = new File(sava_dir);
            dir_save.mkdir();
        } catch (Exception e) {
            Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
        }

        //Start button
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder != null) {
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



        Team mTeam1 = new Team(context, (ListView) findViewById(R.id.our_team_list));
        Team mTeam2 = new Team(context, (ListView) findViewById(R.id.opposing_team_list));
        mEventLogger = new EventLogger(context,(ListView) findViewById(R.id.event_log));

        findViewById(R.id.steal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buf[2] = 1;
                recordEvent(0,1,"steal"); //1:point,2:is success?,3:event name
            }
        });
        findViewById(R.id.rebound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buf[2] = 2;
                recordEvent(0,1,"rebound"); //1:point,2:is success?,3:event name
            }
        });
        findViewById(R.id.foul).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buf[2] = 3;
                recordEvent(0,1,"foul");
            }
        });

        is_scoresheetview = false;
        findViewById(R.id.chenge_scoresheet_and_eventlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
                if(!is_scoresheetview) {
                    LinearLayout eventlog = (LinearLayout) findViewById(R.id.menu_log);
                    menu.removeView(eventlog);
                    getLayoutInflater().inflate(R.layout.score_sheet, menu);

                    setScoresheet();
                    is_scoresheetview = true;
                }else{
                    LinearLayout scoresheet = (LinearLayout) findViewById(R.id.scoresheet);
                    menu.removeView(scoresheet);
                    getLayoutInflater().inflate(R.layout.event_log, menu);
                    mEventLogger = new EventLogger(context,(ListView) findViewById(R.id.event_log));
                    is_scoresheetview = false;
                }
            }
        });
        showBluetoothSelectDialog();
        //startConnect();
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
    public void recordEvent(int point, int is_success, String event_name) {
        if(!is_playing || !(bluetoothStatus == BluetoothStatus.CONNECTED)) return ;
        //録画中でないとエラー
        //Bluetooth接続していない状態で実行できない

        String file_name = "no file";
        //   if(mRecorder.mCamera != null) {
        mRecorder.stop();
        file_name = mRecorder.save();
        mRecorder.start();
        //   }
        if (event_name.equals("shoot") && is_success == 1) {
            final TextView tv_our_score = (TextView)findViewById(R.id.our_score);
            int our_score = Integer.parseInt(tv_our_score.getText().toString());

            final TextView tv_opp_score = (TextView)findViewById(R.id.opposing_score);
            int opp_score = Integer.parseInt(tv_opp_score.getText().toString());

            switch (Team.who_is_actor[0]) {
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
                case -1:
                    Toast.makeText(context, "(score)team isnt be selected", Toast.LENGTH_SHORT).show();
                    return ;
                default:
                    Toast.makeText(context, "(score)team cant be specified", Toast.LENGTH_SHORT).show();
                    return ;
            }
        }else{
            buf[0] = Byte.parseByte(Integer.toString(Team.who_is_actor[0]));
            buf[1] = Byte.parseByte(Integer.toString(Team.who_is_actor[1]));
            bc.writeObject(buf);
        }
        mEventLogger.addEvent(Team.who_is_actor[0], Team.who_is_actor[1], point,is_success, event_name, file_name);
        Team.resetWhoIsAct();


    }

    private static class FileSort implements Comparator<File> {
        public int compare(File src, File target) {
            int diff = src.getName().compareTo(target.getName());
            return diff;
        }
    }


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

        new AlertDialog.Builder(this)
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
        if(!is_playing) return ;
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
        mEventLogger.addEvent(team, actor, point, is_success, "shoot", file_name);
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
}
