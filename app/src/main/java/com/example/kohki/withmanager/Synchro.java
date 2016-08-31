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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Set;

public class Synchro extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBtDevice;
    private BluetoothSocket mBtsocket;
    private OutputStream mOutput;

    Button btnStart;
    boolean isMain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchro);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.synchro_menu, menu);
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
                isMain = false;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener btn_startClicked = new View.OnClickListener(){
        @Override
        public void onClick(View view){
//            Intent itt_start = new Intent(getApplication(), );
        }
    };
}
