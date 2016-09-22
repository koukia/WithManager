package com.example.kohki.withmanager;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by kohki on 16/09/02.
 */
public class Team {
    public String[] event_who = {"", ""};
    private Context context_;
    private static ListView team_lv;
    public static String[] members =
            {"?","4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"};
    public static int who_is_actor[] = {-1, -1};
    public static String event_name = null;

    public Team(Context context, ListView team_list) {
        this.context_ = context;

        team_lv = team_list;


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context_,
                android.R.layout.simple_list_item_1, members);
        team_lv.setAdapter(adapter1);

        team_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);

                String id_name = context_.getResources().getResourceEntryName(listView.getId());

                switch (id_name){
                    case "our_team_list":
                    //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //    VideoActivity.who_is_acter[0] = 0;
                        who_is_actor[0] = 0;
                        break;
                    case "opposing_team_list":
                    //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                    //    VideoActivity.who_is_acter[0] = 1;
                        who_is_actor[0] = 1;
                        break;
                    default:
                        Toast.makeText(context_, "e:"+item+"@"+id_name , Toast.LENGTH_SHORT).show();
                     //   VideoActivity.who_is_acter[0] = -1;
                        who_is_actor[0] = -1;
                        break;
                }
                if(item.equals("?"))
                //   VideoActivity.who_is_acter[1] = 0;
                    who_is_actor[1] = 0;
                else
                //    VideoActivity.who_is_acter[1] = Integer.parseInt(item);
                    who_is_actor[1] = Integer.parseInt(item);
            }
        });
    }
    public static void resetWhoIsAct(){
        who_is_actor[0] = -1;
        who_is_actor[1] = -1;
        event_name = null;
    }
}
