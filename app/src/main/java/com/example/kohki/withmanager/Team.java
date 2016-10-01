package com.example.kohki.withmanager;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kohki on 16/09/02.
 */
public class Team {
    public String[] event_who = {"", ""};
    private Context context_;
    private static ListView team_lv;
    public ArrayList<String> members;
    public static int who_is_actor[] = {0, 0};
    public static String event_name = null;
    ArrayAdapter<String> adapter_teamlist;

    public Team(Context context, final ListView team_list, int mem_num) {
        this.context_ = context;

        team_lv = team_list;
        members = new ArrayList<>();
        members.add("?");
        for(int i = 4; i <= mem_num; i++){
            members.add(i+"");
        }
        adapter_teamlist = new ArrayAdapter<String>(context_,
                android.R.layout.simple_list_item_1, members);
        team_lv.setAdapter(adapter_teamlist);


    }
    public static void resetWhoIsAct(){
        who_is_actor[0] = 0;
        who_is_actor[1] = 0;
        event_name = null;
    }
    public ArrayAdapter<String> getAdapter(){
        return adapter_teamlist;
    }

}
