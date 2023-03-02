package com.example.notepad.timer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notepad.bean.TimeSlot;
import com.example.notepad.bean.Todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimerDbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "timerSQLite.db";
    private static final String TABLE_NAME = "time_slot";

    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME + " (id integer primary key autoincrement, title text, date text, start_time text, time integer)";


    public TimerDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertData(TimeSlot ts){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", ts.getTitle());
        values.put("time", ts.getTime());
        values.put("start_time", ts.getStartTime());
        values.put("date", ts.getDate());

        return db.insert(TABLE_NAME, null, values);
    }

    public List<TimeSlot> queryAllFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        List<TimeSlot> list = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") int time = cursor.getInt(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));

                TimeSlot ts = new TimeSlot();
                ts.setId(id);
                ts.setTitle(title);
                ts.setStartTime(startTime);
                ts.setDate(date);
                ts.setTime(time);

                list.add(ts);
            }
            cursor.close();
        }
        return list;
    }

    public int[] queryTimesInAWeekFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        int[] numList = new int[7];

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String cur_date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") int time = cursor.getInt(cursor.getColumnIndex("time"));

                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                try {
                    //今日信息
                    Calendar calendar = Calendar.getInstance();
                    Date date = sdf.parse(cur_date);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int n = -1;
                    for (int i = 0; i <= 6; i++) {
                        if((c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
                                && (c.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))
                                && (c.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))){
                            n = i;
                            break;
                        }
                        calendar.add(Calendar.DAY_OF_MONTH,-1);
                    }
                    if(n != -1){
                        numList[6 - n] += time;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return numList;
    }

    public int queryTimesSumFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        int cnt = 0;

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int time = cursor.getInt(cursor.getColumnIndex("time"));
                cnt += time;
            }
            cursor.close();
        }
        return cnt;
    }
}
