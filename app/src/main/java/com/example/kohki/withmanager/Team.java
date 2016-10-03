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
    private ListView team_lv;
    public ArrayList<String> members;
    public static int who_is_actor[] = {0, 0};
    public static int min_team_members = 5;
    public static int max_team_members = 30;

    public static String event_name = null;
    ArrayAdapter<String> adapter_teamlist;

    public Team(Context context, final ListView team_list, int mem_num) {
        this.context_ = context;

        team_lv = team_list;
        members = new ArrayList<>();
        members.add("?");
        for(int i = 0; i < mem_num; i++){
            members.add((4+i)+"");
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

    public void sortAdapater(){
        String sort_tmp;
        for(int i = 6; i <= members.size()-2; i++){
            for(int j = i + 1; j <= members.size()-1; j++){
                if(Integer.parseInt(members.get(i)) > Integer.parseInt(members.get(j))){
                    sort_tmp = members.get(i);
                    members.remove(i);
                    members.add(j, sort_tmp);
                }
            }
        }
        adapter_teamlist = new ArrayAdapter<String>(context_,
                android.R.layout.simple_list_item_1, members);
        team_lv.setAdapter(adapter_teamlist);
    }
}
