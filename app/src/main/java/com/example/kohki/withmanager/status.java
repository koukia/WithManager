package com.example.kohki.withmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;

import java.util.ArrayList;
import java.util.Arrays;

public class Status extends AppCompatActivity {
    private PlayerDBManager mPlayerDBManager;
    private int[] status = new int[6];
    private String[] detail = new String[3];
    TextView textView_name;
    TextView textView_class;
    TextView textView_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        String playerName = intent.getStringExtra("name");
        mPlayerDBManager = new PlayerDBManager(this);
        status = mPlayerDBManager.getStatus(playerName);
        detail = mPlayerDBManager.getDetail(playerName); // 0:学年, 1:クラス, 2:ポジション

        textView_name = (TextView)findViewById(R.id.player_name);
        textView_name.setText(playerName);

        textView_class = (TextView)findViewById(R.id.player_class);
        textView_class.setText(detail[0] + "-" + detail[1]);

        textView_position = (TextView)findViewById(R.id.position);
        textView_position.setText(detail[2]);

        //レーダーチャート作成
        final RadarChart chart = (RadarChart) findViewById(R.id.chart);
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i < status.length; i++) entries.add(new Entry(status[i], i));

        RadarDataSet dataSet1 = new RadarDataSet(entries, "能力値");

        ArrayList<RadarDataSet> dataSets = new ArrayList<RadarDataSet>();
        dataSets.add(dataSet1);

        ArrayList<String> labels = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ARRAY_STR)));

        RadarData data = new RadarData(labels, dataSets);
        chart.setData(data);
        chart.setDescription("");
        chart.setRotationEnabled(false);
        chart.invalidate();
        chart.getYAxis().setDrawLabels(false);


    }
}
