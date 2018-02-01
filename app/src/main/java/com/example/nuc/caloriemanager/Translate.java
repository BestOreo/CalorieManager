package com.example.nuc.caloriemanager;

/**
 * Created by NUC on 2017/11/4.
 */

import com.baidu.translate.demo.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.translate.demo.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * 翻译模块，调用百度api
 */
public class Translate {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20171010000087351";
    private static final String SECURITY_KEY = "6vwiH8czLbmJu_49vkBN";

    public static void main(String[] args) throws JSONException {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String query = "Lincoln";
        String resultJson = api.getTransResult(query, "auto", "zh");
        JSONObject objJson = new JSONObject(resultJson);
        JSONArray arrayJson = objJson.getJSONArray("trans_result");;
        String translate_result = arrayJson.getJSONObject(0).get("dst").toString();
        System.out.println(unicode2String(translate_result));
    }

    /*
        翻译接口
        para: 英文原文
        return: 中文
      */
    public String Translate(String EnglistText) throws JSONException {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String resultJson = api.getTransResult(EnglistText, "auto", "zh");
        JSONObject objJson = new JSONObject(resultJson);
        JSONArray arrayJson = objJson.getJSONArray("trans_result");;
        String translate_result = arrayJson.getJSONObject(0).get("dst").toString();
        return translate_result;
    }

    /*
     *  这个函数主要解决中文编码问题
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 0; i < hex.length; i++) {

            try {
                // 汉字范围 \u4e00-\u9fa5 (中文)
                if(hex[i].length()>=4){//取前四个，判断是否是汉字
                    String chinese = hex[i].substring(0, 4);
                    try {
                        int chr = Integer.parseInt(chinese, 16);
                        boolean isChinese = true;
                        //转化成功，判断是否在  汉字范围内
                        if (isChinese){//在汉字范围内
                            // 追加成string
                            string.append((char) chr);
                            //并且追加  后面的字符
                            String behindString = hex[i].substring(4);
                            string.append(behindString);
                        }else {
                            string.append(hex[i]);
                        }
                    } catch (NumberFormatException e1) {
                        string.append(hex[i]);
                    }
                }else{
                    string.append(hex[i]);
                }
            } catch (NumberFormatException e) {
                string.append(hex[i]);
            }
        }
        return string.toString();
    }

}
