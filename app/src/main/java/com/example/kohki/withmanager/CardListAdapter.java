package com.example.kohki.withmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kohki on 16/09/08.
 */
public class CardListAdapter extends ArrayAdapter<Integer> {
    LayoutInflater mInflater;
    ArrayList al_eventLog;
    private EventDbHelper mDbHelper;
    private Context context;

    public CardListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_list_item_card, parent, false);
        }
        Integer id_event_db = getItem(position);
        HashMap<String, String> row = EventDbHelper.getRowFromID(context, id_event_db);

        if(row.size() == 0){
            return convertView;
        }
        String team         = row.get(EventContract.Event.COL_TEAM);
        String num          = row.get(EventContract.Event.COL_NUM);
        String point        = row.get(EventContract.Event.COL_POINT);
        String success      = row.get(EventContract.Event.COL_SUCCESS);
        String event        = row.get(EventContract.Event.COL_EVENT);
        String movie_name   = row.get(EventContract.Event.COL_MOVIE_NAME);;
        String datetime     = row.get(EventContract.Event.COL_DATETIME);
        String quarter_num  = row.get(EventContract.Event.COL_QUARTER_NUM);

        //title and sub is message on card.
        TextView tv_title = (TextView) convertView.findViewById(R.id.title);
        TextView tv_sub = (TextView) convertView.findViewById(R.id.sub);
        String sub = "";
        if (team.equals("0")) {
            sub += "味方チーム";
        } else if (team.equals("1")) {
            sub += "相手チーム";
        }
        if (num.equals("0")) {
            sub += "?番" + "\n";
        }else {
            sub += num + "番\n";
        }
        if(event.equals("shoot")){
            sub += point + "点";
            if(success.equals("0")){
                sub += " 失敗...";
            }else if(success.equals("1")){
                sub += " 成功!!";
            }
        }
        tv_sub.setText(sub);
        // Toast.makeText(getContext(),event[5],Toast.LENGTH_SHORT).show();

        ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
        switch (event) {
            case "shoot":
                tv_title.setText("シュート(第"+quarter_num+"Q)");

                if(team.equals("0") && success.equals("0")){//our team, failed
                    iv.setImageResource(R.drawable.ico_shoot_failed_white);

                }else if(team.equals("0") && success.equals("1")){//our team, success
                    iv.setImageResource(R.drawable.ico_shoot_success_white);

                }else if(team.equals("1") && success.equals("0")){//opp team, failed
                    iv.setImageResource(R.drawable.ico_shoot_failed_blue);

                }else if(team.equals("1") && success.equals("1")) {//opp team, success
                    iv.setImageResource(R.drawable.ico_shoot_success_blue);
                }

                break;
            case "foul":
                tv_title.setText("ファウル (第"+quarter_num+"Q)");
                if(team.equals("0")){//our team
                    iv.setImageResource(R.drawable.ico_foul_white);
                }else if(team.equals("1")){//opp team
                    iv.setImageResource(R.drawable.ico_foul_blue);
                }
                break;

            case "rebound":
                tv_title.setText("リバウンド (第"+quarter_num+"Q)");
                if(team.equals("0")){//our team
                    iv.setImageResource(R.drawable.ico_rebound_white);
                }else if(team.equals("1")){//opp team
                    iv.setImageResource(R.drawable.ico_rebound_blue);
                }
                break;

            case "steal":
                tv_title.setText("スティール (第"+quarter_num+"Q)");
                if(team.equals("0")){//our team
                    iv.setImageResource(R.drawable.ico_steal_white);
                }else if(team.equals("1")){//opp team
                    iv.setImageResource(R.drawable.ico_steal_blue);
                }
                break;
            default:
                break;
        }
        return convertView;
    }
}
