package com.example.kohki.withmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haruka on 2016/09/09.
 */
public class CSVFile {
    private InputStream inputStream;

    public CSVFile(InputStream inputStream){
        this.inputStream = inputStream;
    }
    public List<String[]> read(){
        List<String[]> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int point, sum, now_our = 1, now_ene = 1;
        try{
            String csvLine;
            reader.readLine();
            while((csvLine = reader.readLine()) != null){
                String[] row = csvLine.split(",");
                if(!row[3].equals("0")) continue;
                point = Integer.parseInt(row[2]);
                String mark;

                switch(point){
                    case 1:
                        mark = "・";
                        break;
                    case 2:
                    case 3:
                        mark = "/";
                        break;
                    default:
                        mark = " ";
                }
                if(row[0].equals("0")){
                    sum = now_our + point - 1;
                    while(now_our < sum){
                        String[] obj = {"0", "", "" + now_our};
                        resultList.add(obj);
                        now_our++;
                    }
                    row[2] = now_our + mark;
                    now_our++;

                }else if(row[0].equals("1")){
                    sum = now_ene + point - 1;
                    while(now_ene < sum){
                        String[] obj = {"1", "", "" + now_ene};
                        resultList.add(obj);
                        now_ene++;
                    }
                    row[2] = now_ene + mark;
                    now_ene++;

                }

                resultList.add(row);
            }

        }catch(IOException e){
            throw new RuntimeException("Error in reading CSV file" + e);

        }finally{
                try{
                    inputStream.close();

                }catch(IOException ex){
                    throw new RuntimeException("Error in closing input stream" + ex);
                }
        }
        return resultList;
    }
}
