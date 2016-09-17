package com.example.kohki.withmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Player extends AppCompatActivity {
    TextView textView;
    Button btnNewPlayer;
    Button btnPlayerDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        textView = (TextView)findViewById(R.id.hello);
        textView.setText("Change here!");

        btnNewPlayer = (Button)findViewById(R.id.btn_new);
        btnNewPlayer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent itt_new = new Intent(getApplication(), NewPlayer.class);
                startActivity(itt_new);
            }
        });
        btnPlayerDetail = (Button)findViewById(R.id.player);
        btnPlayerDetail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent itt_player = new Intent(getApplication(), Status.class);
                startActivity(itt_player);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id =  item.getItemId();

        switch(id){
            case R.id.first:
                textView.setText("pushed First");
                break;

            case R.id.second:
                textView.setText("pushed Second");
                break;

            case R.id.third:
                textView.setText("pushed Third");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
