package com.example.kohki.withmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by kohki on 16/10/08.
 */
public class SelectRecordModeActivity extends AppCompatActivity {
    Context context = this;
    private ArrayList<String> mGameList;

    private GameListAdapter gameListAdpt;
    private EventDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_synchro);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //---
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.record_menu,null);
                ListView game_list = (ListView)layout.findViewById(R.id.game_log_ist);
                gameListAdpt = new GameListAdapter(context);
                mDbHelper = new EventDbHelper(context);
                mDb = mDbHelper.getWritableDatabase();
                try {
                    //SQLiteCursor c = (SQLiteCursor)mDb.rawQuery(sql, null);
                    SQLiteCursor c = (SQLiteCursor)mDb.query(
                            true,EventContract.Game.TABLE_NAME,
                            null,null,null,null,null,null,null);
                    int rowcount = c.getCount();
                    if(rowcount != 0) {
                        c.moveToFirst();
                        for (int i = 0; i < rowcount; i++) {
                            int game_start_list_id = c.getInt(c.getColumnIndex(EventContract.Game._ID));
                            gameListAdpt.insert(game_start_list_id, 0);//adapterにセットするしておいてclicklistenerで使う
                            c.moveToNext();
                        }
                    }else {
                    }
                } catch (SQLException e) {
                    Log.e("ERROR", e.toString());
                }
                game_list.setAdapter(gameListAdpt);
                //---Dialog
                builder = new AlertDialog.Builder(context);
                builder.setTitle("試合記録メニュー");
                builder.setView(layout);
                builder.setCancelable(true);
                builder.setPositiveButton("新しく記録する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent ittSelect = new Intent(context, VideoActivity.class);
                            ittSelect.putExtra("mode", "single");
                            ittSelect.putExtra("ref", "new");
                            startActivity(ittSelect);
                        } catch (Exception e) {
                            Log.v("IntentErr:", e.getMessage() + "," + e);
                        }
                    }
                });
                builder.show();

            }
        });

        findViewById(R.id.synchro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    BluetoothAdapter Bt = BluetoothAdapter.getDefaultAdapter();
                    if(!Bt.equals(null)){
                        System.out.println("BlueToothがサポートされています"); //confirm available BlueTooth
                    }
                    else System.out.println("BlueToothがサポートされていません");

                    Intent ittSynchro = new Intent(context, Synchro.class);
                    startActivity(ittSynchro);
                }catch(Exception e){
                    Log.v("IntentErr:", e.getMessage() + "," + e);
                }
            }
        });

    }
}
