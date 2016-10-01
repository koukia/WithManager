package com.example.kohki.withmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haruka on 2016/09/09.
 */
public class ItemArrayAdapter extends ArrayAdapter {
    private List<String[]> scoreList = new ArrayList<>();

    static class ItemViewHolder{
        TextView name;
        TextView score;
        View left;
        View right;
    }

    public ItemArrayAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }


    public void add(String[] object){
        scoreList.add(object);
        super.add(object);
    }
    @Override
    public int getCount(){
        return this.scoreList.size();
    }
    @Override
    public String[] getItem(int index){
        return this.scoreList.get(index);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        ItemViewHolder viewHolder;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_rusult, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.name = (TextView)row.findViewById(R.id.num);
            viewHolder.score = (TextView) row.findViewById(R.id.score);
            viewHolder.left = row.findViewById(R.id.left_1);
            viewHolder.right = row.findViewById(R.id.right_1);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)row.getTag();
        }
        String[] stat = getItem(position);
        if(stat[0].equals("0")){
            viewHolder.left.setBackgroundColor(Color.parseColor("#ffc0cb"));
            viewHolder.name.setBackgroundColor(Color.parseColor("#ffc0cb"));

            viewHolder.right.setBackgroundColor(Color.parseColor("#98fb98"));
            viewHolder.score.setBackgroundColor(Color.parseColor("#98fb98"));

        }
        else{

            viewHolder.right.setBackgroundColor(Color.parseColor("#ffc0cb"));
            viewHolder.score.setBackgroundColor(Color.parseColor("#ffc0cb"));

            viewHolder.left.setBackgroundColor(Color.parseColor("#98fb98"));
            viewHolder.name.setBackgroundColor(Color.parseColor("#98fb98"));

        }
        viewHolder.name.setText(stat[1]);
        viewHolder.name.setTextColor(Color.BLACK);

        viewHolder.score.setText(stat[2]);
        viewHolder.score.setTextColor(Color.BLACK);

        return row;
    }
}
