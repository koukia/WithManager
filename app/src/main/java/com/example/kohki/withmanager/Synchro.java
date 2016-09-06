package com.example.kohki.withmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Set;

public class Synchro extends AppCompatActivity {

    Button btnStart;
    boolean isMain;
    boolean isSub;

    BluetoothAdapter Bt = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BLUETHOOTH = 1;
    private static final int RQ_CONNECT_DEVICE = 1;

    private BluetoothManager mBluetothManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchro);

        btnStart = (Button)findViewById(R.id.btn_synchro_start);
        btnStart.setOnClickListener(btn_startClicked);

        /*
        boolean btEnable = Bt.isEnabled();

        if(btEnable){
        }else{
            Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOn, REQUEST_ENABLE_BLUETHOOTH);
            Toast.makeText(this, "Bluetoothをオンにしました", Toast.LENGTH_SHORT).show();
        }

        btnStart = (Button)findViewById(R.id.btn_synchro_start);
        btnStart.setOnClickListener(btn_startClicked);
*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.synchro_menu, menu);
        inflater.inflate(R.menu.bluetooth_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case R.id.main_terminal:
                Toast.makeText(this, "メインで同期開始します", Toast.LENGTH_SHORT).show();
                isMain = true; isSub = false;
                return true;

            case R.id.sub_terminal:
                Toast.makeText(this, "サブで同期開始します", Toast.LENGTH_SHORT).show();
                isSub = true; isMain = false;
                return true;

            case R.id.connect:

                //connectを押した時の処理
                if(Bt.isEnabled()){ //Bluetoothがオンなら、接続可能なデバイスを発見する
                    Intent itt_DeviceDiscovery = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(itt_DeviceDiscovery, RQ_CONNECT_DEVICE);

                }else{ //Bluetoothがオフなら、オンにするように促す
                    Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(btOn, REQUEST_ENABLE_BLUETHOOTH);
                }
                return true;

            case R.id.discoverable:
                ensureDiscoverable();
                return true;


        }
        return false;
    }

    //その端末のBluetooth通信使用の発見有効
    private void ensureDiscoverable(){
        if(Bt.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 480);
            startActivity(intent);
        }
    }

    //Goが押された時のリスナー
    private final View.OnClickListener btn_startClicked = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent itt_start;
//          Intent itt_start = new Intent(getApplication(), );
            if (Bt.isEnabled() && isMain) {
                System.out.println("Bluetoothがオン、メインで動きます");

            } else if (Bt.isEnabled() && isSub) {
                System.out.println("Bluetoothがオン、サブで動きます");
                itt_start = new Intent(getApplication(), SubGameActivity.class); //サブ用のアクティビティ
                startActivity(itt_start);
            }else{
                Toast.makeText(Synchro.this, "Bluetoothがオンになっていないか\nメイン/サブが選択されていません", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
