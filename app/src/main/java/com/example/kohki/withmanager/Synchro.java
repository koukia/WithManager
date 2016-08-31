package com.example.kohki.withmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class Synchro extends AppCompatActivity {
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
}
