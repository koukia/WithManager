package com.example.kohki.withmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by kohki on 16/10/03.
 */
public class GameResultActivity extends AppCompatActivity {

    private final int HOME = 0;
    AlertDialog.Builder alert;

    private ListView listView_our, listView_ene;
    private ItemArrayAdapter adpt_our, adpt_ene;
    private InputStream inputStream;

    private String gameStartDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_game);

        Intent itt = getIntent();
        gameStartDateTime = itt.getStringExtra("gamestartdatetime");
        if(gameStartDateTime != null){
            Toast.makeText(this, "to-re-ta", Toast.LENGTH_SHORT).show();
        }
/*
        listView_our = (ListView) findViewById(R.id.listView_our);
        listView_ene = (ListView) findViewById(R.id.listView_ene);

        adpt_our = new ItemArrayAdapter(getApplicationContext(), R.layout.item_rusult);
        adpt_ene = new ItemArrayAdapter(getApplicationContext(), R.layout.item_rusult);

        Parcelable state_our = listView_our.onSaveInstanceState();
        Parcelable state_ene = listView_ene.onSaveInstanceState();

        listView_our.setAdapter(adpt_our);
        listView_our.onRestoreInstanceState(state_our);
        listView_ene.setAdapter(adpt_ene);
        listView_ene.onRestoreInstanceState(state_ene);


///        inputStream = getResources().openRawResource(R.raw.scorelog);
        try {
            inputStream = openFileInput("scoreLog.csv");
        }catch(IOException e){
            throw new RuntimeException("Error in reading CSV file" + e);
        }

        CSVFile csvFile = new CSVFile(inputStream);
        List<String[]> scoreList = csvFile.read();
        for (String[] scoreData : scoreList) {
            if (scoreData[0].equals("0")) {
                adpt_our.add(scoreData);

            } else if (scoreData[0].equals("1")) {
                String tmp = scoreData[1];
                scoreData[1] = scoreData[2];
                scoreData[2] = tmp;
                adpt_ene.add(scoreData);
            }
        }
*/
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
