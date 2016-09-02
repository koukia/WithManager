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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchro);

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

        inflater.inflate(R.menu.bluetooth_manu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case R.id.main_terminal:
                Toast.makeText(this, "メインで同期開始します", Toast.LENGTH_SHORT).show();
                isMain = true;
                break;

            case R.id.sub_terminal:
                Toast.makeText(this, "サブで同期開始します", Toast.LENGTH_SHORT).show();
                isSub = true;
                break;

            case R.id.connect:

                if(Bt.isEnabled()){
                }else{
                    Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(btOn, REQUEST_ENABLE_BLUETHOOTH);
                    Toast.makeText(this, "Bluetoothをオンにしました", Toast.LENGTH_SHORT).show();
                }


                btnStart = (Button)findViewById(R.id.btn_synchro_start);
                btnStart.setOnClickListener(btn_startClicked);

        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener btn_startClicked = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent itt_start;

//            Intent itt_start = new Intent(getApplication(), );
            if (Bt.isEnabled() && isMain) {

            } else if (Bt.isEnabled() && isSub) {

            }else{
                Toast.makeText(Synchro.this, "Bluetoothがオンになっていないか\nメイン/サブが選択されていません", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
