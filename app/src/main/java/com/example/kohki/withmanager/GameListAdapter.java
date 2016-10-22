package com.example.kohki.withmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kohki on 16/10/08.
 */
public class GameListAdapter extends ArrayAdapter<Integer> {
    LayoutInflater mInflater;
    ArrayList al_eventLog;
    private EventDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context context;
    private String gameStartTime;
    private AlertDialog.Builder builder;

    public GameListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_game_list, parent, false);
        }
        Integer game_id = getItem(position);
        mDbHelper = new EventDbHelper(context);
        mDb = mDbHelper.getReadableDatabase();
       HashMap<String, String> row = EventDbHelper.getGameRowFromID(mDb, game_id);

        if(row.size() == 0){
            return convertView;
        }
        gameStartTime         = row.get(EventContract.Game.COL_DATE_TIME);
        String gamename         = row.get(EventContract.Game.COL_GAME_NAME);
        String gamenotes        = row.get(EventContract.Game.COL_GAME_NOTES);
        //title and sub is message on card.
        TextView tv_title = (TextView) convertView.findViewById(R.id.title);
        TextView tv_sub = (TextView) convertView.findViewById(R.id.sub);
     //   tv_title.setText(gamename);
        tv_title.setText(gameStartTime);
        tv_sub.setText(gamenotes);
        convertView.findViewById(R.id.btn_game_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent ittSelect = new Intent(context, VideoActivity.class);
                    ittSelect.putExtra("mode", "single");
                    ittSelect.putExtra("ref", gameStartTime+"");
                    context.startActivity(ittSelect);
                } catch (Exception e) {
                    Log.v("IntentErr:", e.getMessage() + "," + e);
                }
            }
        });
        convertView.findViewById(R.id.btn_game_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("削除しますか？");
                builder.setCancelable(true);
                builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mDbHelper.deleteGameRecordofGame(mDb, gameStartTime);
                        } catch (Exception e) {
                            Log.v("IntentErr:", e.getMessage() + "," + e);
                        }
                        SelectRecordModeActivity.alertDialog.dismiss();
                    }
                });
                builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectRecordModeActivity.alertDialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        return convertView;
    }
}

