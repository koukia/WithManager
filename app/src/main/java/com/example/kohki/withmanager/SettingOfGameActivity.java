package com.example.kohki.withmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kohki on 16/09/20.
 */
public class SettingOfGameActivity extends Activity {
    private Context context;

    private TextView tv_ourteam_num;
    private TextView tv_oppteam_num;
    private TextView tv_rec_time;

    private int pre_ourteam_num;
    private int pre_oppteam_num;
    private int pre_rec_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);
        context = this;

        //num of team member Setting
        pre_ourteam_num = VideoActivity.our_member_num;
        pre_oppteam_num = VideoActivity.opp_member_num;
        pre_rec_time    = VideoActivity.movie_time;

        tv_ourteam_num = (TextView) findViewById(R.id.ourteam_num);
        tv_oppteam_num = (TextView) findViewById(R.id.opposingteam_num);
        tv_rec_time    = (TextView) findViewById(R.id.rec_time);
        tv_ourteam_num.setText(Integer.toString(pre_ourteam_num));
        tv_oppteam_num.setText(Integer.toString(pre_oppteam_num));
        tv_rec_time.setText(((double)pre_rec_time/1000)+"");



        setListener(R.id.btn_ourteam_minus,      R.id.btn_ourteam_plus, tv_ourteam_num);
        setListener(R.id.btn_opposingteam_minus, R.id.btn_opposingteam_plus, tv_oppteam_num);
        setListener(R.id.btn_rec_time_minus,     R.id.btn_rec_time_plus, tv_rec_time);

        findViewById(R.id.setting_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNum();
            }
        });
        findViewById(R.id.setting_btn_back_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreNum();
            }
        });
    }

    private void setListener(int btnId_minus, int btnId_plus, final TextView tv_num){
        findViewById(btnId_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_num.getId() == tv_rec_time.getId()) {
                    Double rec_time =Double.parseDouble(tv_num.getText()+"");//mSec
                    if (rec_time >= 1.5) {
                        rec_time -= 0.5;
                        tv_num.setText(rec_time + "");
                    }
                }else { // number of team member
                    int mem_num = Integer.parseInt(tv_num.getText()+"");
                    if (mem_num > Team.min_team_members) {
                        tv_num.setText(mem_num - 1 + "");
                    }
                }
            }
        });
        findViewById(btnId_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_num.getId() == tv_rec_time.getId()) {
                    Double rec_time =Double.parseDouble(tv_num.getText()+"");
                    if (rec_time < 10.0) {
                        rec_time += 0.5;
                        tv_num.setText(rec_time + "");
                    }
                }else { // number of team member
                    int mem_num = Integer.parseInt(tv_num.getText()+"");
                    if (mem_num < Team.max_team_members) {
                        tv_num.setText(mem_num + 1 + "");
                    }
                }
            }
        });
    }

    private void savePreNum(){
        VideoActivity.our_member_num = pre_ourteam_num;
        VideoActivity.opp_member_num = pre_oppteam_num;
        VideoActivity.movie_time     = pre_rec_time;
        finish();
    }
    private void saveNum(){
        VideoActivity.our_member_num = Integer.parseInt(tv_ourteam_num.getText()+"");
        VideoActivity.opp_member_num = Integer.parseInt(tv_oppteam_num.getText()+"");
        VideoActivity.movie_time     = (int) Double.parseDouble(tv_rec_time.getText()+"")*1000;
        Toast.makeText(context,"保存しました",Toast.LENGTH_SHORT).show();

        finish();
    }
}
