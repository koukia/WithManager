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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        TextView textView = (TextView) findViewById(R.id.textView6);
        textView.setText(name);

        final RadarChart chart = (RadarChart) findViewById(R.id.chart);

        ArrayList<Entry> entries = new ArrayList<Entry>();
        int[] own = getResources().getIntArray(R.array.ARRAY_INT);
        for(int i=0; i<own.length; i++) entries.add(new Entry(own[i], i));

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
