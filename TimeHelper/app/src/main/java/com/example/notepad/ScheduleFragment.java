package com.example.notepad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepad.bean.Course;
import com.example.notepad.schedule.AddCourseActivity;
import com.example.notepad.schedule.DatabaseHelper;
import com.example.notepad.schedule.SeeCourseActivity;

import java.util.ArrayList;
import java.util.Random;

public class ScheduleFragment extends Fragment {

    private View v;
    private Toolbar mToolbar;

    //星期几
    private RelativeLayout day;

    //SQLite Helper类
    private DatabaseHelper databaseHelper;

    //被点击的View
    View ClickedView;
    int currentCoursesNumber = 0;
    int maxCoursesNumber = 0;

    public ScheduleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;

        //工具条
        mToolbar = view.findViewById(R.id.schedule_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        //从数据库读取数据
        databaseHelper = new DatabaseHelper(getActivity(), "database.db", null, 1);
        loadData();

        //动态添加月份
        Calendar c = Calendar.getInstance();
        TextView text = view.findViewById(R.id.month);
        text.setText(String.valueOf( c.get(Calendar.MONTH) + 1)+"月");

        for(int i = 0; i < 14;i ++){
            createLeftView(i);
        }
    }

    //从数据库加载数据
    @SuppressLint("Range")
    private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end"))));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //使用从数据库读取出来的课程信息来加载课程表视图
        for (Course course : coursesList) {
            createItemCourseView(course);
        }
    }

    //保存数据到数据库
    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher, class_room, day, class_start, class_end) " + "values(?, ?, ?, ?, ?, ?)",
                        new String[] {course.getCourseName(),
                                course.getTeacher(),
                                course.getClassRoom(),
                                course.getDay()+"",
                                course.getStart()+"",
                                course.getEnd()+""}
                );
    }

    //创建"第几节数"视图
    private void createLeftView(int i) {
        String []start_hour=new String[]{"08","08","09","10","11","13","13","14","15","16","18","18","19","20"};
        String []start_minute=new String[]{"00","50","50","40","30","00","50","50","40","30","00","50","40","30"};
        String []end_minute=new String[]{"45","35","35","25","15","45","35","35","25","15","45","35","25","15"};
        String []end_hour=new String[]{"08","09","10","11","12","13","14","15","16","17","18","19","20","21"};
        Random r = new Random();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.classes, null);
        TextView text1 = view.findViewById(R.id.class_num);
        text1.setText(String.valueOf(++currentCoursesNumber));

        TextView text2 = view.findViewById(R.id.start_time);
        text2.setText(start_hour[i]+":"+start_minute[i]);

        TextView text3 = view.findViewById(R.id.end_time);
        text3.setText(end_hour[i]+":"+end_minute[i]);

        LinearLayout leftViewLayout = v.findViewById(R.id.left_view_layout);
        leftViewLayout.addView(view);//在一个layout里面加了很多个view，所以才能连在一块
    }

    //获得控件里面的星期几控件
    private RelativeLayout getViewDay(int day){
        int dayId = 0;
        switch (day) {
            case 1: dayId = R.id.monday; break;
            case 2: dayId = R.id.tuesday; break;
            case 3: dayId = R.id.wednesday; break;
            case 4: dayId = R.id.thursday; break;
            case 5: dayId = R.id.friday; break;
            case 6: dayId = R.id.saturday; break;
            case 7: dayId = R.id.weekday; break;
        }
        return v.findViewById(dayId);
    }

    //创建单个课程视图
    private void createItemCourseView(final Course course) {
        String []color = new String[]{"#e7f2fd","#fae4e3","#eae4fc","#fdf9de","#fbeff0","#e4faf7","#f0fefe","#e5e6fa"};
        int getDay = course.getDay();
        if ((getDay < 1 || getDay > 7) || course.getStart() > course.getEnd())
            Toast.makeText(getActivity(), "设置信息有误", Toast.LENGTH_LONG).show();
        else {
            day = getViewDay(getDay);
            int height = 260;
            final View v = LayoutInflater.from(getActivity()).inflate(R.layout.course_card, null); //加载单个课程布局
            Random r = new Random();
            v.setBackgroundColor(Color.parseColor(color[r.nextInt(8)]));
            v.setY(height * (course.getStart()-1)); //设置开始高度,即第几节课开始
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd()-course.getStart()+1)*height - 8); //设置布局高度,即跨多少节课
            v.setLayoutParams(params);
            TextView text = v.findViewById(R.id.text_view);
            text.setText(course.getCourseName() + "\n@" + course.getClassRoom() + "\n" + course.getTeacher()); //显示课程名
            day.addView(v);
            //查看课程
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClickedView = view;
                    Intent intent = new Intent(getActivity(), SeeCourseActivity.class);
                    intent.putExtra("seeCourse", course);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_courses:
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//用来添加课程

        //如果你想在Activity中得到新打开Activity关闭后返回的数据，你需要使用系统提供的
        // startActivityForResult(Intent intent,int requestCode)方法打开新的Activity，
        // 新的Activity关闭后会向前面的Activity传回数据，为了得到传回的数据，
        // 你必须在前面的Activity中重写onActivityResult(int requestCode, int resultCode,Intent data)方法：
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Course course = (Course) data.getSerializableExtra("course");
            //创建课程表视图
            createItemCourseView(course);
            //存储数据到数据库
            saveData(course);
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Course PreCourse = (Course) data.getSerializableExtra("PreCourse");
            boolean isDelete = data.getBooleanExtra("isDelete", true);

            if (isDelete) {
                ClickedView.setVisibility(View.GONE);//先隐藏
                day = getViewDay(PreCourse.getDay());
                day.removeView(ClickedView);//再移除课程视图
                SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                sqLiteDatabase.execSQL("delete from courses where course_name = ? and day =? and class_start=? and class_end=?",
                        new String[]{PreCourse.getCourseName(),
                                String.valueOf(PreCourse.getDay()),
                                String.valueOf(PreCourse.getStart()),
                                String.valueOf(PreCourse.getEnd())});
                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                intent.putExtra("ReviseCourse", PreCourse);
                intent.putExtra("isRevise", true);
                startActivityForResult(intent, 2);
            }

        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            Course PreCourse = (Course) data.getSerializableExtra("PreCourse");
            Course newCourse = (Course) data.getSerializableExtra("newCourse");

            ClickedView.setVisibility(View.GONE);//先隐藏
            day = getViewDay(PreCourse.getDay());
            day.removeView(ClickedView);//再移除课程视图

            //创建课程表视图
            createItemCourseView(newCourse);

            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL("update courses set " +
                            "course_name = ?,teacher = ?,class_room=? ,day=? ,class_start=? ,class_end =?" +
                            "where course_name = ? and day =? and class_start=? and class_end=?",
                    new String[]{newCourse.getCourseName(),
                            newCourse.getTeacher(),
                            newCourse.getClassRoom(),
                            String.valueOf(newCourse.getDay()),
                            String.valueOf(newCourse.getStart()),
                            String.valueOf(newCourse.getEnd()),
                            PreCourse.getCourseName(),
                            String.valueOf(PreCourse.getDay()),
                            String.valueOf(PreCourse.getStart()),
                            String.valueOf(PreCourse.getEnd())});

        }
    }
}