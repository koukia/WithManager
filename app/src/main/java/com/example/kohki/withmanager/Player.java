package com.example.kohki.withmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Player extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView textView;
    Button btnNewPlayer;
    ListView player;
    ArrayAdapter<String> playerName;

    private PlayerDBManager mPlayerManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // new add
        mPlayerManage = new PlayerDBManager(this);


        player = (ListView) findViewById(R.id.players);
        player.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String name = (String) player.getItemAtPosition(position);
                Intent intent = new Intent(getApplication(), Status.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });


        // ナビーゲーションドロワーの設定
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

    //ナビゲーションドロワーのボタンが押された時の処理
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){

           case R.id.menu_grade1: //First Gradeが押された時
//                playerName = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
//                                                        getResources().getStringArray(R.array.grade1));

               playerName = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
                       mPlayerManage.getPlayers(1));


                player.setAdapter(playerName);
                break;

            case R.id.menu_grade2:  //Second Gradeが押された時
//                playerName = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
//                                                        getResources().getStringArray(R.array.grade2));

                playerName = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
                        mPlayerManage.getPlayers(2));

                player.setAdapter(playerName);
                break;

            case R.id.menu_grade3: //Third Gradeが押された時
//                playerName = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
//                                                        getResources().getStringArray(R.array.grade1));

                playerName = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
                        mPlayerManage.getPlayers(3));

                player.setAdapter(playerName);
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
