package com.example.kohki.withmanager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
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
public class Team extends VideoActivity {
    private static final String TAG = "TeamCls";
    private static Context context;

    private ListView lv_mTeamList;
    private static ArrayAdapter<String> adpt_teamList;
    private ArrayList<String> al_mMembers;
    private static int sTeamAndNum[] = {-1, -1};//[0] is team,[1] is num,-1 is '?'

    public static int sMinMembers = 5;
    public static int sMaxMembers = 30;

    public Team(Context context, final ListView team_list, int num)  {
        this.context = context;
        lv_mTeamList = team_list;

        al_mMembers = new ArrayList<>();
        al_mMembers.add("?");
        for(int i = 0; i < num; i++){
            al_mMembers.add((4+i)+"");
        }
        adpt_teamList = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, al_mMembers);
        lv_mTeamList.setAdapter(adpt_teamList);

    }
    public void resetStaticValues(){
        sTeamAndNum[0] = -1;
        sTeamAndNum[1] = -1;
    }
    class TeamSelectListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isSaving) {
                ListView lv_team_list = (ListView) parent;
                String item = (String) lv_team_list.getItemAtPosition(position);
                String id_name = lv_team_list.getResources().getResourceEntryName(lv_team_list.getId());

                switch (id_name) {
                    case "our_team_list":
                        //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                        sTeamAndNum[0] = 0;
                        break;
                    case "opposing_team_list":
                        //    Toast.makeText(context_, item+"@"+id_name , Toast.LENGTH_SHORT).show();
                        sTeamAndNum[0] = 1;
                        break;
                    default:
                        Log.d(TAG, "TeamClickListener, switch_default");
                        sTeamAndNum[0] = -1;
                        break;
                }
                if (item.equals("?"))
                    sTeamAndNum[1] = 0;
                else{
                    sTeamAndNum[1] = Integer.parseInt(item);

                    adpt_teamList.remove(item);
                    adpt_teamList.insert(item, 1);
                    lv_team_list.setAdapter(adpt_teamList);
                    sortAdapater();
                }
                //記録
                EventLogger.addEvent(mDB, sTeamAndNum[0],sTeamAndNum[1]);
                EventLogger.updateEventLog(context,lv_EventLog);
                updateScoreView();
            }else{
                tv_sMessageBar.setText("イベントボタンを先に押してください");
                tv_sMessageBar.setTextSize(12);
                tv_sMessageBar.setBackgroundColor(88990000);
                tv_sMessageBar.setGravity(Gravity.CENTER);
            }
        }
    }
    private void sortAdapater(){
        String sort_tmp;
        for(int i = 6; i <= al_mMembers.size()-2; i++){
            for(int j = i + 1; j <= al_mMembers.size()-1; j++){
                if(Integer.parseInt(al_mMembers.get(i)) > Integer.parseInt(al_mMembers.get(j))){
                    sort_tmp = al_mMembers.get(i);
                    al_mMembers.remove(i);
                    al_mMembers.add(j, sort_tmp);
                }
            }
        }
        adpt_teamList = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, al_mMembers);
        lv_mTeamList.setAdapter(adpt_teamList);
    }
}
