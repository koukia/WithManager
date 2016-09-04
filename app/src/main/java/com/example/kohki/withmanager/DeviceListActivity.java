package com.example.kohki.withmanager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Haruka on 2016/09/03.
 */
public class DeviceListActivity extends Activity implements AdapterView.OnItemClickListener{
    private final static String BR = System.getProperty("line.separator");
    private final static int MP = LinearLayout.LayoutParams.MATCH_PARENT;
    private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

    private BluetoothAdapter btAdapter;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setResult(Activity.RESULT_CANCELED);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        adapter = new ArrayAdapter<String>(this, R.layout.rowdata);

        //ListViewの生成
        ListView listView = new ListView(this);
        listView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        listView.setAdapter(adapter);
        layout.addView(listView);
        listView.setOnItemClickListener(this);

        //ブロードキャストレシーバーの生成
        IntentFilter filter;
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                adapter.add(device.getName() + BR + device.getAddress());
            }
        }
        if(btAdapter.isDiscovering()) btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(btAdapter != null) btAdapter.cancelDiscovery();
        this.unregisterReceiver(receiver);
    }

    //クリック時に呼ばれる
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id){

        //Bluetooth端末の検索のキャンセル
        btAdapter.cancelDiscovery();

        //戻り値の指定
        String info = ((TextView)view).getText().toString();
        String address = info.substring(info.length() - 17);
        Intent intent = new Intent();
        intent.putExtra("device_address", address);
        setResult(Activity.RESULT_OK, intent);
        //System.out.println(address + "が押されました");
        finish();
    }

    //ブロードキャストレシーバー
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        //Bluetooth端末の検索結果の取得
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //Bluetooth端末発見
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) ;

            //Bluetooth端末検索完了
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

            }
        }
    };
}
