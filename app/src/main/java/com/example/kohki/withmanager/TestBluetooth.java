package com.example.kohki.withmanager;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class TestBluetooth extends AppCompatActivity {
    private static final Handler handler = new Handler();

    private Button bluetoothSettingButton;
    private Button serverButton;
    private Button guestButton;
    private Button button01;
    private Button button02;
    private long currentTimeMillis;
    private Calendar calendar = Calendar.getInstance();
    private byte[] buf = new byte[6];


    private BluetoothUtil bu;
    private BluetoothDevice targetDevice = null;
    private BluetoothStatus bluetoothStatus;
    private BluetoothConnection bc;
    private BluetoothAdapter ba;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bluetooth);
        bluetoothSettingButton = (Button) findViewById(R.id.BluetoothButton);
        serverButton = (Button) findViewById(R.id.ServerButton);
        guestButton = (Button) findViewById(R.id.GuestButton);
        button01 = (Button) findViewById(R.id.Button01);
        button02 = (Button) findViewById(R.id.Button02);

        bluetoothSettingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showBluetoothSelectDialog();
            }
        });
        serverButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startConnect();
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startsendConnect();
            }
        });

        button01.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTimeMillis = System.currentTimeMillis();
                calendar.setTimeInMillis(currentTimeMillis);
                Log.d("MaA","LOG" + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":"+ calendar.get(Calendar.MINUTE)
                        + ":"+ calendar.get(Calendar.SECOND)
                        + ":" + calendar.get(Calendar.MILLISECOND));
                buf[0] = (byte)(calendar.get(Calendar.HOUR_OF_DAY));
                buf[1] = (byte)(calendar.get(Calendar.MINUTE));
                buf[2] = (byte)(calendar.get(Calendar.SECOND));
                buf[3] = 0;
                buf[4] = 1;
                buf[5] = (byte)(111);
				/*bc.write(1)*/
                bc.writeObject(buf);/*((Calendar.HOUR_OF_DAY << 8)
						+ (Calendar.MINUTE << 6)
						+ (Calendar.SECOND << 4)
						+ (Calendar.MILLISECOND << 2) + 1)*/;
            }
        });

        button02.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTimeMillis = System.currentTimeMillis();
                calendar.setTimeInMillis(currentTimeMillis);
                Log.d("MaA","LOG" + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":"+ calendar.get(Calendar.MINUTE)
                        + ":"+ calendar.get(Calendar.SECOND)
                        + ":" + calendar.get(Calendar.MILLISECOND));
                buf[0] = (byte)(calendar.get(Calendar.HOUR_OF_DAY));
                buf[1] = (byte)(calendar.get(Calendar.MINUTE));
                buf[2] = (byte)(calendar.get(Calendar.SECOND));
                buf[3] = 0 ;
                buf[4] = 2;
                buf[5] = (byte)(111);
				/*bc.write(2)*/
                bc.writeObject(buf);/*((Calendar.HOUR_OF_DAY << 8)
						+ (Calendar.MINUTE << 6)
						+ (Calendar.SECOND << 4)
						+ (Calendar.MILLISECOND << 2) + 1)*/;
            }
        });
    }
    private void startsendConnect() {
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

        // Bluetooth接続スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 接続
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


    /***************************
     * BluetoothとWi-Fiの通信確立
     ***************************/
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
                if(bc != null){
                    bc.close();
                    bc = null;
                }
            }
        });
        progressDialog.show();

        // Bluetooth接続スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 接続
				/*4/21 コメントアウト

				while(progressDialog.isShowing() && bluetoothStatus != BluetoothStatus.CONNECTED){
					bluetoothStatus = bc.connectToServer(targetDevice) ? BluetoothStatus.CONNECTED : BluetoothStatus.CONNECTING;

					if(bluetoothStatus == BluetoothStatus.CONNECTED)
						refreshProgressMessage(progressDialog);
					else
						Util.sleep(2000);
				}
				*/
            }

        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 接続待機
                ba = BluetoothAdapter.getDefaultAdapter();
                bluetoothStatus = bc.makeServer(ba) ? BluetoothStatus.CONNECTED : BluetoothStatus.ERROR;

                refreshProgressMessage(progressDialog);
            }
        }).start();
    }

    /***************************
     * Bluetoothデバイス選択
     ***************************/
    private void showBluetoothSelectDialog() {
        this.bu = new BluetoothUtil();

        if (!this.bu.isSpported()) // 非対応デバイス
            DialogBuilder.showErrorDialog(this, "Bluetooth非対応デバイスです。");
        else if (!this.bu.isEnabled()) // 設定無効
            DialogBuilder.showErrorDialog(this, "Bluetooth有効にしてください。");
        else if (this.bu.getPairingCount() == 0) // ペアリング済みデバイスなし
            DialogBuilder.showErrorDialog(this, "ペアリング済みのBluetooth設定がありません。");
        else {
            new DialogBuilder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Bluetoothデバイス選択")
                    .setItems(bu.getDeviceNames(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            decideBluetoothDevice(bu.getDevices()[which]);
                        }
                    })
                    .setNegativeButton("キャンセル", null)
                    .show("Bluetoothデバイス選択");
        }
    }

    private void decideBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothSettingButton.setText("Bluetooth設定\n\n対象 : " + bluetoothDevice.getName());
        this.targetDevice = bluetoothDevice;
    }

    private void refreshProgressMessage(final ProgressDialog dialog){
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
    private void bluetoothReceive(){
        int i;
        int[] j = new int[6];
        int x = 0;
        while((i = bc.readObject()) != -1){
            j[x] = i;
            if(i == 111){
                final int[] t = j;
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        NumDialog(t);
                    }
                });
                break;
            }
            x++;
        }
    }
    public void show(){

    }
    /*
    private void startInside(){
        GrobalData gd = Util.getSharedVariable(this);
        gd.bluetoothConnection = this.bc;
        startService(new Intent(this, ConnectionService.class));
    */
    private void NumDialog(int[] i){
        int j = i[4];
        String G = String.valueOf(i[0]) + "時"  + String.valueOf(i[1]) +"分" + String.valueOf(i[2]) + "秒";
        String T = String.valueOf(j);
        new DialogBuilder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(G)
		/*
		.setTitle(String.valueOf(j >> 15)
				+ "." +String.valueOf(j >> 13)
				+ "." +String.valueOf(j >> 11)
				+ "." +String.valueOf(j >> 9))
				*/
                .setMessage(T + "が完了しました。")
                .setCanceledOnTouchOutside(false)
                .setOnDismissListener(new DialogBuilder.OnDismissListener(){
                    @Override
                    protected void onDismiss(CustomDialog dialog){
                        //finish();
                    }
                })
                .setPositiveButton("OK", null)
                .show(G + "完了");
        Toast.makeText(this, "押されたンゴ", Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog(String message){
        DialogBuilder.showErrorDialog(this, message);
    }

}
