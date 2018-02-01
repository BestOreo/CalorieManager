package com.example.nuc.caloriemanager;

/**
 * Created by NUC on 2017/11/4.
 */
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.graphics.Bitmap;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class CalorieCount {


    private static final String ENDPOINT = "https://api-2445582032290.production.gw.apicast.io/v1/foodrecognition";

    public String infor;
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * Food Recognition for Calorie Mama API Main method
     *
     */
    public  CalorieCount(FoodTask foodTask) throws IOException {
        // creates a unique boundary based on time stamp
        if (foodTask == null) {
            throw new IllegalArgumentException("Food task can't be null");
        }

        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(ENDPOINT + "?user_key=" + foodTask.getToken());
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                true);

        addImage("file", foodTask.getImage());
    }

    private void addImage(String fieldName, Bitmap image) throws IOException {
        String FileName = "temp.jpeg";
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + FileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(FileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.writeTo(outputStream);
        writer.append(LINE_FEED);
        writer.flush();
        outputStream.flush();
        out.flush();
        out.close();
    }

    public String send() {
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        StringBuilder output = new StringBuilder();

        try {

            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                reader.close();
                httpConn.disconnect();
            } else if (status == HttpURLConnection.HTTP_FORBIDDEN) {
                System.out.print("Error code: " + status + ", Description: Access denied. Check your Token please");
            } else {
                System.out.print("Error code: " + status + ", Description: " + output.toString());
            }
        } catch (IOException e) {
            System.out.print(e.toString());
        }
        return output.toString();
    }

}


/*
 *  卡路里计算模块
 *  这个模块暂时未写函数，直接main调用。如有需要自行改写
 */
//public class CalorieCount {
//    public static String infor;
//    public String a="";
//
////    public static void main(String[] args) throws JSONException {
////        // 调用命令行，不确定windows可不可以
////        String []cmds = {"curl", "-H", "-i", "-F", "media=@./pic/4.jpeg","https://api-2445582032290.production.gw.apicast.io/v1/foodrecognition?"
////                + "user_key=7601dc4ce3192d433ba40e7492069677"};
////        ProcessBuilder pb=new ProcessBuilder(cmds);
////        pb.redirectErrorStream(true);
////        Process p;
////        try {
////            p = pb.start();
////            BufferedReader br=null;
////            String line=null;
////
////            br=new BufferedReader(new InputStreamReader(p.getInputStream()));
////            while((line=br.readLine())!=null){
////                infor = line; // 最后一行信息是json对象
////            }
////            br.close();
////        } catch (IOException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
////        //System.out.println(infor);
////        MyObject v = new MyObject();
////        v.calorieCoubt(infor);// 计算卡路里
////    }

