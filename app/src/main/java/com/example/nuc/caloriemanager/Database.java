package com.example.nuc.caloriemanager;

/**
 * Created by zf on 2017/11/20.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zf on 2017/11/20.
 */

public class Database extends SQLiteOpenHelper {
    public static Database db;

    public static String Calorie = "create table Calorie(" + "id integer primary key autoincrement," +
            "foodname text," +
            "Calorie real," +
            "time text)";

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Calorie);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String foodname, String calorie) {
        SQLiteDatabase sqLiteDatabase = Database.db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("foodname", foodname);
        values.put("Calorie", calorie);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);
        values.put("time", time);
        sqLiteDatabase.insert("Calorie", null, values);
    }

    public void insert_demo() {
        SQLiteDatabase sqLiteDatabase = Database.db.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateBefore = new Date();
        for (int i = 0; i < 100; i++) {
            Calendar c = Calendar.getInstance();
            String foodname[] = {"苹果", "香蕉", "鸡腿", "米饭", "玉米", "白菜", "鸭肉"};
            values.put("foodname", foodname[i % 7]);
            values.put("Calorie", Math.random() * 100);
            c.add(Calendar.DAY_OF_MONTH, (int) (((100 - i) / 14) * -1) - 1);
            dateBefore = c.getTime();
            values.put("time", formatter.format(dateBefore));
            sqLiteDatabase.insert("Calorie", null, values);
            values.clear();
        }
        values.put("foodname", "香蕉");
        values.put("Calorie", 1000);
        values.put("time", "2017/11/01");
        sqLiteDatabase.insert("Calorie", null, values);
        values.clear();
        values.put("foodname", "香蕉");
        values.put("Calorie", 1000);
        values.put("time", "2017/09/25");
        sqLiteDatabase.insert("Calorie", null, values);
        values.clear();
        values.put("foodname", "香蕉");
        values.put("Calorie", 1000);
        values.put("time", "2017/10/25");
        sqLiteDatabase.insert("Calorie", null, values);
        values.clear();
    }

    public ArrayList<String> LastWeek() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        ArrayList<String> week = new ArrayList<>();
        Date dateBefore = new Date();
        for (int i = 0; i < 7; i++) {
            c.add(Calendar.DAY_OF_MONTH, -1);
            dateBefore = c.getTime();
            week.add(sdf.format(dateBefore));
        }
        return week;
    }

    public ArrayList<String> Select_Week() {
        SQLiteDatabase c = Database.db.getWritableDatabase();
        Cursor cursor = c.query("Calorie", null, null, null, null, null, null);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        String ctime = formatter.format(date);
        ArrayList<String> a = new ArrayList();
        ArrayList<String> week = LastWeek();
        double num = 0;
        int weekday = 6;
        if (cursor.moveToFirst()) {
            do {
                String Calorie = cursor.getString(cursor.getColumnIndex("Calorie"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                if (time.equals(week.get(weekday))) {
                    num += Float.parseFloat(Calorie);
                }
                if (weekday > 0 && week.get(weekday - 1).equals(time)) {
                    a.add(num + "");
                    num = Float.parseFloat(Calorie);
                    weekday -= 1;
                }
                if (weekday == 0 && !time.equals(week.get(0))) {
                    break;
                }
                if (weekday < 0)
                    break;

            } while (cursor.moveToNext());
        }
        a.add(num + "");
        return a;
    }

    public ArrayList<String> Select_Month4() {
        SQLiteDatabase c = Database.db.getWritableDatabase();
        Cursor cursor = c.query("Calorie", null, null, null, null, null, null);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        String ctime = formatter.format(date);
        ArrayList<String> a = new ArrayList();
        ArrayList<String> week = LastWeek();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        double[] num = {0, 0, 0, 0};
        int weekday = 6;
        if (cursor.moveToFirst()) {
            do {
                String Calorie = cursor.getString(cursor.getColumnIndex("Calorie"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String[] s = time.split("/");
                if (Integer.parseInt(s[1]) == month)
                    num[3] += Float.parseFloat(Calorie);
                if (Integer.parseInt(s[1]) == month - 1)
                    num[2] += Float.parseFloat(Calorie);
                if (Integer.parseInt(s[1]) == month - 2)
                    num[1] += Float.parseFloat(Calorie);
                if (Integer.parseInt(s[1]) == month - 3)
                    num[0] += Float.parseFloat(Calorie);
            } while (cursor.moveToNext());
        }
        for (int i = 0; i < 4; i++)
            a.add(num[i] + "");
        return a;
    }

    public ArrayList<String> show() {
        SQLiteDatabase c = Database.db.getWritableDatabase();
        Cursor cursor = c.query("Calorie", null, null, null, null, null, null);
        ArrayList<String> a = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String foodname = cursor.getString(cursor.getColumnIndex("foodname"));
                String Calorie = cursor.getString(cursor.getColumnIndex("Calorie"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                a.add(id + foodname + Calorie + time + "\n");
            } while (cursor.moveToNext());
        }
        return a;
    }

    public void delete() {
        SQLiteDatabase c = Database.db.getWritableDatabase();
        c.execSQL("delete from Calorie");
    }

    public double Select_today()
    {
        SQLiteDatabase c = Database.db.getWritableDatabase();
        Cursor cursor = c.query("Calorie", null, null, null, null, null, null);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        String ctime = formatter.format(date);
        double sum=0;
        if (cursor.moveToFirst()) {
            do {
                String Calorie = cursor.getString(cursor.getColumnIndex("Calorie"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                if(time.equals(ctime))
                    sum+=Float.parseFloat(Calorie);
            } while (cursor.moveToNext());
        }
        return sum;
    }
}