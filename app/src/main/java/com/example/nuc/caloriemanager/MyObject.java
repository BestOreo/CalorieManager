package com.example.nuc.caloriemanager;

/**
 * Created by NUC on 2017/11/4.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.nuc.caloriemanager.Translate;




/**
 * Created by geshuaiqi on 2017/10/11.
 */
public class MyObject {
    String data[]= new String[3];
    MyObject(){

    }
    /*
     * 该函数用于解析json
     * jsonStr为calorie mama API返回的json
     */
    public String[] calorieCoubt(String jsonStr) throws JSONException {
        JSONObject res = new JSONObject(jsonStr); // 创建jsonobject对象
        JSONArray t; // json数组
//         这块对照返回的json格式看笔记容易理解
        t=res.getJSONArray("results");
//
//        Log.d("trans1","apple");
        Translate tanslateModule = new Translate();
        data[0]="apple";
        data[1]="orange";
        data[2]=("duck");
        for(int i=0;i<1;i++){
            JSONObject o = t.getJSONObject(i);
            JSONArray oo = o.getJSONArray("items");
            int len=oo.length()>3?3:oo.length();
            for(int j=0;j<len;j++){
                JSONObject q = oo.getJSONObject(j).getJSONObject("nutrition");
                String calory =  q.get("calories").toString();
//                int format_calory = Integer.parseInt(calory);
                String ChineseName = tanslateModule.Translate(oo.getJSONObject(j).get("name").toString());
//                String ChineseName = oo.getJSONObject(j).get("name").toString();
                System.out.println( ChineseName  + ":" + oo.getJSONObject(j).get("score") +", calorie:"+calory );
//                data[j]=ChineseName  + ":" + oo.getJSONObject(j).get("score") +", calorie:"+calory;
                if(calory.length()>5) calory=calory.substring(0,4);
                data[j]=ChineseName  + "\t" + oo.getJSONObject(j).get("score") +"\t卡路里\t"+calory;
            }

        }
        return data;
    }
}
