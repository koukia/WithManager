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
        String[] event = event_info.split(",");
        //event[] => 0:ID, 1:team, 2:number, 3:shoot_point ,4:is_success, 5:event_name

        String event_name = event[5];

        //title and sub is message on card.
        TextView tv_title = (TextView) convertView.findViewById(R.id.title);
        TextView tv_sub = (TextView) convertView.findViewById(R.id.sub);
        String sub = "";
        if (event[1].equals("0")) {
            sub += "味方チーム";
        } else if (event[1].equals("1")) {
            sub += "相手チーム";
        }
        if (event[2].equals("0")) {
            sub += "?番" + "\n";
        }else {
            sub += event[2] + "番\n";
        }
        if(event_name.equals("shoot")){
            sub += event[3] + "点";
            if(event[4].equals("0")){
                sub += "失敗...";
            }else if(event[4].equals("1")){
                sub += "成功!!";
            }
        }
        tv_sub.setText(sub);
        // Toast.makeText(getContext(),event[5],Toast.LENGTH_SHORT).show();

        ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
        switch (event_name) {
            case "shoot":
                tv_title.setText("シュート");

                if(event[1].equals("0") && event[4].equals("0")){//our team, failed
                    iv.setImageResource(R.drawable.ico_shoot_failed_white);

                }else if(event[1].equals("0") && event[4].equals("1")){//our team, success
                    iv.setImageResource(R.drawable.ico_shoot_success_white);

                }else if(event[1].equals("1") && event[4].equals("0")){//opp team, failed
                    iv.setImageResource(R.drawable.ico_shoot_failed_blue);

                }else if(event[1].equals("1") && event[4].equals("1")) {//opp team, success
                    iv.setImageResource(R.drawable.ico_shoot_success_blue);
                }

                break;
            case "foul":
                tv_title.setText("ファウル");
                sub +=  "ファウル";
                if(event[1].equals("0")){//our team
                    iv.setImageResource(R.drawable.ico_foul_white);
                }else if(event[1].equals("1")){//opp team
                    iv.setImageResource(R.drawable.ico_foul_blue);
                }
                break;

            case "rebound":
                tv_title.setText("ファウル");
                sub +=  "ファウル";
                if(event[1].equals("0")){//our team
                    iv.setImageResource(R.drawable.ico_rebound_white);
                }else if(event[1].equals("1")){//opp team
                    iv.setImageResource(R.drawable.ico_rebound_blue);
                }
                break;

            case "steal":
                tv_title.setText("ファウル");
                sub +=  "ファウル";
                if(event[1].equals("0")){//our team
                    iv.setImageResource(R.drawable.ico_steal_white);
                }else if(event[1].equals("1")){//opp team
                    iv.setImageResource(R.drawable.ico_steal_blue);
                }
                break;


            default:
                tv_title.setText("unknown");
                sub += "";
                break;
        }

        return convertView;
    }
}
