package com.example.kohki.withmanager;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.InputStream;
import java.util.List;

public class Result_game extends AppCompatActivity {
    private final int HOME = 0;
    AlertDialog.Builder alert;

    private ListView listView_our, listView_ene;
    private ItemArrayAdapter adpt_our, adpt_ene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_game);

        listView_our = (ListView)findViewById(R.id.listView_a);
        listView_ene = (ListView)findViewById(R.id.listView_b);

        adpt_our = new ItemArrayAdapter(getApplicationContext(), R.layout.item_rusult);
        adpt_ene = new ItemArrayAdapter(getApplicationContext(), R.layout.item_rusult);

        Parcelable state_our = listView_our.onSaveInstanceState();
        Parcelable state_ene = listView_ene.onSaveInstanceState();

        listView_our.setAdapter(adpt_our);
        listView_our.onRestoreInstanceState(state_our);
        listView_ene.setAdapter(adpt_ene);
        listView_ene.onRestoreInstanceState(state_ene);

        InputStream stm = getResources().openRawResource(R.raw.scorelog);
        CSVFile csvFile = new CSVFile(stm);
        List<String[]> scoreList = csvFile.read();

        for(String[] scoreData : scoreList){
            if(scoreData[0].equals("0")){
                adpt_our.add(scoreData);

            }else if(scoreData[0].equals("1")){
                String tmp = scoreData[1];
                scoreData[1] = scoreData[2];
                scoreData[2] = tmp;

                adpt_ene.add(scoreData);
            }
        }

        alert = new AlertDialog.Builder(this);
        alert.setTitle("通知");
        alert.setMessage("ホーム画面に戻ります\nよろしいですか？");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent itt_home = new Intent(getApplication(), HomeActivity.class);
                startActivity(itt_home);
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        menu.add(Menu.NONE, HOME, Menu.NONE, "トップへ戻る");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case HOME:
                alert.show();
                return true;
        }
        return false;
    }
}
