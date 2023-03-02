package com.example.notepad.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.notepad.bean.Todo;
import com.example.notepad.bean.TodoList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodoDbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todoSQLite.db";
    private static final String TABLE_NAME_TODO = "todo";

    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME_TODO + " (id integer primary key autoincrement, title text, content text, create_time text, start_time text, end_time text, done integer, star integer)";


    public TodoDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertData(Todo todo){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", todo.getTitle());
        values.put("content", todo.getContent());
        values.put("create_time", todo.getCreatedTime());
        values.put("start_time", todo.getStartTime());
        values.put("end_time", todo.getEndTime());
        values.put("done",0);
        values.put("star",0);

        return db.insert(TABLE_NAME_TODO, null, values);
    }

    public void refreshAllData(TodoList todos){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME_TODO, null, null);

        for (Todo todo: todos.getTodos()) {
            ContentValues values = new ContentValues();
            values.put("title", todo.getTitle());
            values.put("content", todo.getContent());
            values.put("create_time", todo.getCreatedTime());
            values.put("start_time", todo.getStartTime());
            values.put("end_time", todo.getEndTime());
            values.put("done",todo.getDone());
            values.put("star",todo.getStar());
            db.insert(TABLE_NAME_TODO, null, values);
        }
    }

    public List<Todo> queryAllFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        List<Todo> todoList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, "done asc, star desc, id asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                @SuppressLint("Range") int star = cursor.getInt(cursor.getColumnIndex("star"));

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTitle(title);
                todo.setContent(content);
                todo.setCreatedTime(createTime);
                todo.setStartTime(startTime);
                todo.setEndTime(endTime);
                todo.setDone(done);
                todo.setStar(star);

                todoList.add(todo);
            }
            cursor.close();
        }

        return todoList;

    }

    public long updateData(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", todo.getTitle());
        values.put("content", todo.getContent());
        values.put("create_time", todo.getCreatedTime());
        values.put("start_time", todo.getStartTime());
        values.put("end_time", todo.getEndTime());
        values.put("done", todo.getDone());
        values.put("star", todo.getStar());

        return db.update(TABLE_NAME_TODO, values, "id like ?", new String[]{todo.getId()});
    }

    public int deleteFromDbById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME_TODO, "id like ?", new String[]{id});
    }

    public List<Todo> queryFromDbByTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return queryAllFromDb();
        }

        SQLiteDatabase db = getWritableDatabase();
        List<Todo> todoList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_TODO, null, "title like ?", new String[]{"%"+title+"%"}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title2 = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                @SuppressLint("Range") int star = cursor.getInt(cursor.getColumnIndex("star"));

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTitle(title2);
                todo.setContent(content);
                todo.setCreatedTime(createTime);
                todo.setStartTime(startTime);
                todo.setEndTime(endTime);
                todo.setDone(done);
                todo.setStar(star);

                todoList.add(todo);
            }
            cursor.close();
        }
        return todoList;
    }

    //查看给定结束时间的todo是否都完成
    public boolean queryIfDoneFromDbByEndTime(int year, int month, int day) {

        SQLiteDatabase db = getWritableDatabase();
        boolean flag = true;

        Cursor cursor = db.query(TABLE_NAME_TODO, null, "end_time like ?", new String[]{"%"+year+"/"+month+"/"+day+"%"}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                if(endTime.equals(year+"/"+month+"/"+day)) {
                    if (done == 0) flag = false;
                }
            }
            cursor.close();
        }
        return flag;
    }

    public List<Todo> queryTodayFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        List<Todo> todoList = new ArrayList<>();
        //今日信息
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, "done asc, star desc, id asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                @SuppressLint("Range") int star = cursor.getInt(cursor.getColumnIndex("star"));

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTitle(title);
                todo.setContent(content);
                todo.setCreatedTime(createTime);
                todo.setStartTime(startTime);
                todo.setEndTime(endTime);
                todo.setDone(done);
                todo.setStar(star);

                //判断是否为今日
                boolean flag = true;
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date date = sdf.parse(createTime);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    if(c.get(Calendar.YEAR) != y) flag = false;
                    if(c.get(Calendar.MONTH) != m) flag = false;
                    if(c.get(Calendar.DAY_OF_MONTH) != d) flag = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(flag) todoList.add(todo);
            }
            cursor.close();
        }

        return todoList;

    }

    public List<Todo> querySelectDayDDLFromDb(int y, int m, int d) {

        SQLiteDatabase db = getWritableDatabase();
        List<Todo> todoList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, "done asc, star desc, id asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                @SuppressLint("Range") int star = cursor.getInt(cursor.getColumnIndex("star"));

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTitle(title);
                todo.setContent(content);
                todo.setCreatedTime(createTime);
                todo.setStartTime(startTime);
                todo.setEndTime(endTime);
                todo.setDone(done);
                todo.setStar(star);

                //判断是否为今日
                boolean flag = true;
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date date = sdf.parse(endTime);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    if(c.get(Calendar.YEAR) != y) flag = false;
                    if(c.get(Calendar.MONTH) != (m-1)) flag = false;
                    if(c.get(Calendar.DAY_OF_MONTH) != d) flag = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(flag) todoList.add(todo);
            }
            cursor.close();
        }

        return todoList;

    }

    public List<Todo> queryMonthDataFromDb(int year, int month) {

        SQLiteDatabase db = getWritableDatabase();
        List<Todo> todoList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, "done asc, star desc, id asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                @SuppressLint("Range") int star = cursor.getInt(cursor.getColumnIndex("star"));

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTitle(title);
                todo.setContent(content);
                todo.setCreatedTime(createTime);
                todo.setStartTime(startTime);
                todo.setEndTime(endTime);
                todo.setDone(done);
                todo.setStar(star);

                //判断是否为这个月
                boolean flag = true;
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date date = sdf.parse(endTime);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    if(c.get(Calendar.YEAR) != year) flag = false;
                    if(c.get(Calendar.MONTH) != (month - 1)) flag = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(flag) todoList.add(todo);
            }
            cursor.close();
        }

        return todoList;

    }

    public List<Todo> queryStarFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        List<Todo> todoList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_TODO, null, "star = 1", null, null, null, "done asc, star desc, id asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));
                @SuppressLint("Range") int star = cursor.getInt(cursor.getColumnIndex("star"));

                Todo todo = new Todo();
                todo.setId(id);
                todo.setTitle(title);
                todo.setContent(content);
                todo.setCreatedTime(createTime);
                todo.setStartTime(startTime);
                todo.setEndTime(endTime);
                todo.setDone(done);
                todo.setStar(star);

                todoList.add(todo);
            }
            cursor.close();
        }
        return todoList;
    }

    public int[] queryDoneNumInAWeekFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        int[] numList = new int[7];

        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                @SuppressLint("Range") int done = cursor.getInt(cursor.getColumnIndex("done"));

                if(done == 0) continue;
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                try {
                    //今日信息
                    Calendar calendar = Calendar.getInstance();
                    Date date = sdf.parse(createTime);
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
                        numList[6 - n]++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            cursor.close();
        }
        return numList;
    }

    public int[] queryNumInAWeekFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        int[] numList = new int[7];

        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("create_time"));

                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                try {
                    //今日信息
                    Calendar calendar = Calendar.getInstance();
                    Date date = sdf.parse(createTime);
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
                        numList[6 - n]++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            cursor.close();
        }
        return numList;
    }

    public int queryAllDoneFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        int cnt = 0;

        Cursor cursor = db.query(TABLE_NAME_TODO, null, "done = 1", null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cnt++;
            }
            cursor.close();
        }
        return cnt;
    }
}
