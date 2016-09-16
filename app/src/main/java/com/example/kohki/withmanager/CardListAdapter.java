package com.example.kohki.withmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kohki on 16/09/08.
 */
public class CardListAdapter extends ArrayAdapter<String> {
    LayoutInflater mInflater;
    ArrayList al_eventLog;

    public CardListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_list_item_card, parent, false);
        }
        String event_info = getItem(position);
        String[] event= event_info.split(",");
        //event[] => 0:ID, 1:team, 2:number, 3:shoot_point ,4:is_success, 5:event_name
        String event_name = event[5];
        TextView tv = (TextView) convertView.findViewById(R.id.title);

        String sub="";
        switch (event[1]){
            case "0":
                sub += "味方";;
                break;
            case "1":
                sub += "相手";
                break;
            default:
                break;
        }
        if(event[2].equals("0"))
            sub+="?番"+"\n";
        else
            sub+=event[2] + "番\n";
       // Toast.makeText(getContext(),event[5],Toast.LENGTH_SHORT).show();

        ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
        switch (event_name) {
            case "shoot":
                tv.setText("シュート");
                if (event[4].equals("1")) { //success
                    if(event[1].equals("0")){//our team
                        iv.setImageResource(R.drawable.icon_01success);
                    }else if(event[1].equals("1")){//opp team
                        iv.setImageResource(R.drawable.icon_02success);
                    }
                }else{
                    if(event[1].equals("0")){//our team
                        iv.setImageResource(R.drawable.icon_01fail);
                    }else if(event[1].equals("1")){
                        iv.setImageResource(R.drawable.icon_02fail);
                    }
                }
                sub += event[3] + "点 成功";

                break;

            case "foul":
                tv.setText("ファウル");
                sub +=  "ファウル";
                iv.setImageResource(R.drawable.icon_foul);
                break;
            default:
                sub += "def";
                break;
        }
        tv = (TextView) convertView.findViewById(R.id.sub);
        tv.setText(sub);

        return convertView;
    }
}
