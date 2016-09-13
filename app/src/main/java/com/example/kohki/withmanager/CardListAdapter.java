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

        String event_name = event[5];
        TextView tv = (TextView) convertView.findViewById(R.id.title);
        tv.setText(event_name);

        String sub = "Team:" + event[1] + " " + event[2] + "番\n";
       // Toast.makeText(getContext(),event[5],Toast.LENGTH_SHORT).show();

        ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
        switch (event_name) {
            case "shoot":
                if (event[4].equals("1")) { //success
                    if(event[1].equals("0")){//our team
                        iv.setImageResource(R.drawable.icon_01success);
                    }else if(event[1].equals("1")){//opp team
                        iv.setImageResource(R.drawable.icon_02success);
                    }
                    sub += event[3] + "点 成功";

                }else{
                    if(event[1].equals("0")){//our team
                        iv.setImageResource(R.drawable.icon_01fail);
                    }else if(event[1].equals("1")){
                        iv.setImageResource(R.drawable.icon_02fail);
                    }
                    sub += event[3] + "点 失敗";
                }
                break;

            case "foul":
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
