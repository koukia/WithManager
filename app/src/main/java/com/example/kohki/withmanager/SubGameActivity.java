package com.example.kohki.withmanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Handler;
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
import android.widget.Button;
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
    private static final Handler handler = new Handler();
    private final static String TAG = "SubGameActivity";
    private Context context;
    private int movie_time = 5000;
    private String[] event_who = {"", ""};
    private byte[] buf = new byte[5];

    private OutputStream outputStream;
    private PrintWriter writer;
    int shoot_point;
    int is_success;
    boolean fragUndo;
    String undoTeam;
    private boolean is_selectedEvent;

    private Button shoot_success_free;
    private Button shoot_failed_free;
    private Button shoot_success_2p;
    private Button shoot_failed_2p;
    private Button shoot_success_3p;
    private Button shoot_failed_3p;


    private boolean  is_playing;
    private Button btnBluetoothSettiong;
    private BluetoothUtil bu;
    private BluetoothDevice targetDevice = null;
    private BluetoothStatus bluetoothStatus;
    private BluetoothAdapter ba;
    private BluetoothConnection bc;
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

    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter2;

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


        //試合開始とストップ
        is_playing = false;
        is_selectedEvent = false;



        //Player set
        String[] members1 = {"?", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"};
        lv_players1 = (ListView) findViewById(R.id.our_team_list);
        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, members1);
        lv_players1.setAdapter(adapter1);

        lv_players1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), item + " clicked",Toast.LENGTH_LONG).show();
                event_who[0] = "0";
                event_who[1] = item;
                recordScore("0", item, shoot_point, is_success);

                /*if(!item.equals("?")) {
                    adapter1.remove(item);
                    adapter1.insert(item, 1);
                    listView.setAdapter(adapter1);
                }*/
                event_who = new String[] {"", ""};
                is_selectedEvent = false;
            }
        });

        String[] members2 = {"?", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"};
        lv_players2 = (ListView) findViewById(R.id.enemies_team_list);
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, members2);
        lv_players2.setAdapter(adapter2);

        lv_players2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), item + " clicked",Toast.LENGTH_LONG).show();
                event_who[0] = "1";
                event_who[1] = item;
                recordScore("1", item, shoot_point, is_success);

                /*if(!item.equals("?")) {
                    adapter2.remove(item);
                    adapter2.insert(item, 1);
                    listView.setAdapter(adapter2);
                }*/
                event_who = new String[] {"", ""};
                is_selectedEvent = false;
            }
        });

        shoot_success_free = (Button)findViewById(R.id.shoot_succes_free);
        shoot_success_free.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shoot_point = 1;
                is_success = 1;
                //Toast.makeText(context, "1P成功", Toast.LENGTH_SHORT).show();
                is_selectedEvent = true;
                setColor(shoot_success_free);
                //recordScore(event_who[0], event_who[1], shoot_point, 1);
            }
        });
        shoot_failed_free = (Button)findViewById(R.id.shoot_failed_free);
        shoot_failed_free.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shoot_point = 0;
                is_success = 0;
                //Toast.makeText(context, "1P失敗", Toast.LENGTH_SHORT).show();
                is_selectedEvent = true;
                setColor(shoot_failed_free);
                //recordScore(event_who[0], event_who[1], shoot_point, 0);
            }
        });
        shoot_success_2p = (Button)findViewById(R.id.shoot_success_2p);
        shoot_success_2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoot_point = 2;
                is_success = 1;
                //Toast.makeText(context, "2P成功", Toast.LENGTH_SHORT).show();
                is_selectedEvent = true;
                setColor(shoot_success_2p);
                //recordScore(event_who[0], event_who[1], shoot_point, 1);
            }
        });
        shoot_failed_2p = (Button)findViewById(R.id.shoot_failed_2p);
        shoot_failed_2p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shoot_point = 0;
                is_success = 0;
                //Toast.makeText(context, "2P失敗", Toast.LENGTH_SHORT).show();
                is_selectedEvent = true;
                setColor(shoot_failed_2p);
                //recordScore(event_who[0], event_who[1], shoot_point, 0);
            }
        });
        shoot_success_3p = (Button)findViewById(R.id.shoot_success_3p);
        shoot_success_3p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shoot_point = 3;
                is_success = 1;
                //Toast.makeText(context, "3P成功", Toast.LENGTH_SHORT).show();
                is_selectedEvent = true;
                setColor(shoot_success_3p);
                //recordScore(event_who[0], event_who[1], shoot_point, 1);
            }
        });
        shoot_failed_3p = (Button)findViewById(R.id.shoot_failed_3p);
        shoot_failed_3p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                shoot_point = 0;
                is_success = 0;
                //Toast.makeText(context, "3P失敗", Toast.LENGTH_SHORT).show();
                is_selectedEvent = true;
                setColor(shoot_failed_3p);
                //recordScore(event_who[0], event_who[1], shoot_point, 0);
            }
        });

        findViewById(R.id.miss_undo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(fragUndo);
                    //undo(undoTeam, -shoot_point);
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
                Intent itt_result = new Intent(context, ResultGame.class);

                writer.append("0");
                writer.close();

                startActivity(itt_result);
                return true;

            case R.id.connect:
                showBluetoothSelectDialog();
                return true;

            case R.id.server :
                //startConnect();
                startSendConnect();
                return true;

            case R.id.guest:
                startSendConnect();
                return true;
        }
        return false;
    }

    ArrayList<String> results = new ArrayList<String>(); //スコア等の結果を打ち込む
    private void recordScore(String who_team, String who_num, int point, int is_success){
        if(bluetoothStatus != BluetoothStatus.CONNECTED) return ;
        if(!is_selectedEvent) return ;

        TextView tv_our_score = (TextView)findViewById(R.id.our_score);
        int our_score = Integer.parseInt(tv_our_score.getText().toString());
        TextView tv_enemies_score = (TextView)findViewById(R.id.enemies_score);
        int enemies_score = Integer.parseInt(tv_enemies_score.getText().toString());

        //Bluetooth通信用のbyte配列
        buf[0] = Byte.parseByte(who_team);
        buf[1] = who_num.equals("?") ? 0 : Byte.parseByte(who_num);
        buf[2] = Byte.parseByte(Integer.toString(point));
        buf[3] = Byte.parseByte(Integer.toString(is_success));
        buf[4] = 111;
        bc.writeObject(buf);


        writer.append("0\n");
        String result;
        switch (who_team){

            case "0":
                int our_point = our_score + point;
                tv_our_score.setText(our_point+"");

                result = "阿南高専チーム "+ who_num + "番 得点(" + point + "点)！"; results.add(result); fragUndo = true; undoTeam = "p1";
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

//                writer.append(0 + "," + who_num + "," + point + "\n");
                writer.append(0 + "," + who_num + "," + point + ",");
                break;
            case "1":
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
    }
    private void startConnect(){
        bluetoothStatus = BluetoothStatus.CONNECTING;

        bc = new BluetoothConnection();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("接続中");
        progressDialog.setCancelable(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener(){
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

                refreshProgressMessage(progressDialog);
            }
        }).start();

    }
    private void startSendConnect(){
        if(targetDevice == null){
            Toast.makeText(this, "接続するデバイスを選択してください", Toast.LENGTH_SHORT).show();
            return ;
        }
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
        int[] j = new int[4];
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
    public void bluetoothRecordScore(int[] param){
        //param[0] = チーム, param[1] = 背番号, param[2] = イベントID : スティール->1, リバウンド->2, ファウル->3
        String log = "";
        if(param[0] == 0)       log = "阿南高専チーム ";
        else if(param[0] == 1)  log = "敵チーム ";

        System.out.println(param[2]);
        log += param[1] + "番";
        switch(param[2]){
            case 1:
                log += "スティール";
                break;
            case 2:
                log += "リバウンド";
                break;
            case 3:
                log += "ファウル";
                break;
        }
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }
    private void showErrorDialog(String message){
        DialogBuilder.showErrorDialog(this, message);
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


    public void setColor(Button selectedBtn){
        /*shoot_success_free.setBackgroundColor(Color.rgb(204,204,204));
        shoot_failed_free.setBackgroundColor(Color.rgb(204,204,204));
        shoot_success_2p.setBackgroundColor(Color.rgb(204,204,204));
        shoot_failed_2p.setBackgroundColor(Color.rgb(204,204,204));
        shoot_success_3p.setBackgroundColor(Color.rgb(204,204,204));
        shoot_failed_3p.setBackgroundColor(Color.rgb(204,204,204));

        selectedBtn.setBackgroundColor(Color.rgb(0,0,204));  */
    }
//TODO:
// ava.lang.NullPointerException:
// Attempt to invoke interface method 'android.view.Display
// android.view.WindowManager.getDefaultDisplay()' on a null object reference

}

