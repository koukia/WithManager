package com.example.kohki.withmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Player extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView textView;
    Button btnNewPlayer;
    Button btnPlayerDetail;















    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        btnPlayerDetail = (Button)findViewById(R.id.player);
        btnPlayerDetail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent itt_player = new Intent(getApplication(), Status.class);
                startActivity(itt_player);
            }
        });
        btnPlayerDetail.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =
                (DrawerLayout)findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView Listener
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);















    }
    /* オプションメニューの表示、ボタンがタップされた時のリスナー
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
    }*/


    //ナビゲーションドロワーのボタンが押された時の処理
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.menu_grade1: //First Gradeが押された時
                btnPlayerDetail.setVisibility(View.VISIBLE);
                break;

            case R.id.menu_grade2:  //Second Gradeが押された時
                btnPlayerDetail.setVisibility(View.INVISIBLE);
                break;

            case R.id.menu_grade3: //Third Gradeが押された時
                btnPlayerDetail.setVisibility(View.INVISIBLE);
                break;

            case R.id.review_page:
                Intent itt_review = new Intent(getApplication(), Webview.class);
                startActivity(itt_review);
                break;

            case R.id.add_player:

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
