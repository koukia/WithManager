package com.example.kohki.withmanager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kohki on 16/10/01.
 */
public class FoulsheetArrayAdapter extends ArrayAdapter {
    private List<String[]> foulList = new ArrayList<>();
    private static final String TAG = "FoulsheetArrayAdapter";
    static class ItemViewHolder{
        TextView tv_mem_num;
        TextView tv_foul_sum;
        View left;
        View right;
    }

    public FoulsheetArrayAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    public void add(String[] object){
        foulList.add(object);
        super.add(object);
    }
    @Override
    public int getCount(){
        return this.foulList.size();
    }
    @Override
    public String[] getItem(int index){
        return this.foulList.get(index);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        ItemViewHolder viewHolder;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.foul_sheet_row, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.tv_mem_num  = (TextView) row.findViewById(R.id.num);
            viewHolder.tv_foul_sum = (TextView) row.findViewById(R.id.foul_sum);
            viewHolder.left        = row.findViewById(R.id.left_1);
            viewHolder.right       = row.findViewById(R.id.right_1);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)row.getTag();
        }
        String[] stat = getItem(position);
    //    Log.d("[0]","stat[0]"+stat[0]);
    //    Log.d("[1]","stat[1]"+stat[1]);

        viewHolder.left.setBackgroundColor(Color.parseColor("#ffffff"));
        viewHolder.tv_mem_num.setBackgroundColor(Color.parseColor("#ffffff"));
        viewHolder.right.setBackgroundColor(Color.parseColor("#ffffff"));
        viewHolder.tv_foul_sum.setBackgroundColor(Color.parseColor("#ffffff"));
        viewHolder.tv_mem_num.setTextColor(Color.BLACK);
        viewHolder.tv_foul_sum.setTextColor(Color.BLACK);

        if (stat[0].equals("team_kind")) {
            if (stat[1].equals("ourteam")) {
                viewHolder.tv_mem_num.setText("味方");
                viewHolder.tv_mem_num.setGravity(Gravity.CENTER);
                viewHolder.tv_foul_sum.setText("Foul");
                viewHolder.tv_foul_sum.setGravity(Gravity.CENTER);
            } else if (stat[1].equals("oppteam")) {
                viewHolder.tv_mem_num.setText("相手");
                viewHolder.tv_mem_num.setGravity(Gravity.CENTER);
                viewHolder.tv_foul_sum.setText("Foul");
                viewHolder.tv_foul_sum.setGravity(Gravity.CENTER);
            }
        }else if (stat[0].equals("T")) {
            viewHolder.tv_mem_num.setText("T");
            viewHolder.tv_mem_num.setGravity(Gravity.CENTER);
            try {
                setFoulSum(viewHolder.tv_foul_sum, Integer.parseInt(stat[1]) );
            } catch (NumberFormatException e) {
                Log.d(TAG, e + "");
            }
        }else if (!stat[0].equals("?")) {
            viewHolder.left.setBackgroundColor(Color.parseColor("#ffc0cb"));
            viewHolder.tv_mem_num.setBackgroundColor(Color.parseColor("#ffc0cb"));
            viewHolder.right.setBackgroundColor(Color.parseColor("#98fb98"));
            viewHolder.tv_foul_sum.setBackgroundColor(Color.parseColor("#98fb98"));
            viewHolder.tv_mem_num.setTextColor(Color.BLACK);
            viewHolder.tv_mem_num.setText(stat[0] + "番");

            try {
                setFoulSum(viewHolder.tv_foul_sum, Integer.parseInt(stat[1]));
            } catch (NumberFormatException e) {
                Log.d(TAG, e + "");
            }
        }
        return row;
    }

    private void setFoulSum(TextView tv, int foul_sum){
        if (foul_sum >= 5) {
            tv.setText(foul_sum + "回");
            tv.setTextColor(Color.parseColor("#ff0000"));
            tv.setGravity(Gravity.CENTER);
        } else if (foul_sum >= 3 && foul_sum <= 4) {
            tv.setText(foul_sum + "回");
            tv.setTextColor(Color.parseColor("#ff8cff"));
            tv.setGravity(Gravity.CENTER);
        } else if (foul_sum >= 1) {
            tv.setText(foul_sum + "回");
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.CENTER);
        }else if (foul_sum == 0) {
            tv.setText(" - ");
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.CENTER);
        }
    }
}
