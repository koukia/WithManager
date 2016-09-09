package com.example.kohki.withmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Result_game extends AppCompatActivity {
    private final int HOME = 0;
    //AlertDialog.Builder alert = new AlertDialog.Builder(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_game);

       /* alert.setTitle("popup");
        alert.setMessage("ホーム画面に戻ります");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent itt_home = new Intent(getApplication(), HomeActivity.class);
                startActivity(itt_home);
            }
        });*/
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
 //               alert.show();
                return true;
        }
        return false;
    }
}
