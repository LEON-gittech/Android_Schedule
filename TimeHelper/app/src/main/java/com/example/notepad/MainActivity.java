package com.example.notepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.notepad.data.TimeDataFragment;
import com.example.notepad.me.MeNoLoggedFragment;
import com.example.notepad.util.ImageUtil;
import com.example.notepad.util.ToastUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //是否登录
    private boolean isLogin;

    //drawer
    DrawerLayout drawerLayout;
    private TextView tvNickName,tvSign;
    private ImageView ivHead;

    //nav
    private LinearLayout llSchedule, llTodo, llCalendar, llData, llTimer, llMe;
    private ImageView ivSchedule, ivTodo, ivCalendar, ivData, ivTimer, ivMe;
    private TextView tvSchedule, tvTodo, tvCalendar, tvData, tvTimer, tvMe;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkLoginStatus();
        if(isLogin){
            getDataFromSpf();
        }

        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            //从添加课程activity 回到 ScheduleFragment
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            fragmentTransaction.replace(R.id.fcv,scheduleFragment).commit();
            ivSchedule.setSelected(true);
            getIntent().removeExtra("id");
        }
        else if (id == 2) {
            //从TodoFragment 到 TimerFragment
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            TimerFragment timerFragment = new TimerFragment();
            fragmentTransaction.replace(R.id.fcv,timerFragment).commit();
            ivTodo.setSelected(false);
            ivTimer.setSelected(true);
        }
        else if(id == 3){
            //TimeDataFragment 与 DataFragment 的转换
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            TimeDataFragment timeDataFragment = new TimeDataFragment();
            fragmentTransaction.replace(R.id.fcv,timeDataFragment).commit();
            ivData.setSelected(true);
            ivTodo.setSelected(false);
            getIntent().removeExtra("id");
        }
        else if(id == 4){
            //TimeDataFragment 与 DataFragment 的转换
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            DataFragment dataFragment = new DataFragment();
            fragmentTransaction.replace(R.id.fcv,dataFragment).commit();
            ivData.setSelected(true);
            ivTodo.setSelected(false);
            getIntent().removeExtra("id");
        }
        else if(id == 5){
            //回到me
            if(isLogin){
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                MeFragment meFragment = new MeFragment();
                fragmentTransaction.replace(R.id.fcv,meFragment).commit();
                ivMe.setSelected(true);
            }
            else {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                MeNoLoggedFragment meNoLoggedFragment = new MeNoLoggedFragment();
                fragmentTransaction.replace(R.id.fcv,meNoLoggedFragment).commit();
                ivMe.setSelected(true);
            }
            getIntent().removeExtra("id");
        }
        else if(id == 6){
            //CalendarFragment 到 TodoFragment
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            TodoFragment todoFragment = new TodoFragment();
            fragmentTransaction.replace(R.id.fcv,todoFragment).commit();
            ivTodo.setSelected(true);
            ivCalendar.setSelected(false);
        }
    }

    private void initEvent() {
        //主页
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        HelloFragment helloFragment = new HelloFragment();
        fragmentTransaction.replace(R.id.fcv,helloFragment).commit();

        llSchedule.setOnClickListener(this);
        llTodo.setOnClickListener(this);
        llCalendar.setOnClickListener(this);
        llData.setOnClickListener(this);
        llTimer.setOnClickListener(this);
        llMe.setOnClickListener(this);
    }

    private void initView() {

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        tvNickName = findViewById(R.id.tv_drawer_nick_name);
        tvSign = findViewById(R.id.tv_drawer_sign_text);
        ivHead = findViewById(R.id.iv_drawer_head);

        llSchedule = findViewById(R.id.ll_schedule);
        llTodo = findViewById(R.id.ll_todo);
        llCalendar = findViewById(R.id.ll_calendar);
        llData = findViewById(R.id.ll_data);
        llTimer = findViewById(R.id.ll_timer);
        llMe = findViewById(R.id.ll_me);

        ivSchedule = findViewById(R.id.iv_schedule);
        ivTodo = findViewById(R.id.iv_todo);
        ivCalendar = findViewById(R.id.iv_calendar);
        ivData = findViewById(R.id.iv_data);
        ivTimer = findViewById(R.id.iv_timer);
        ivMe = findViewById(R.id.iv_me);

        tvSchedule = findViewById(R.id.tv_schedule);
        tvTodo = findViewById(R.id.tv_todo);
        tvCalendar = findViewById(R.id.tv_calendar);
        tvData = findViewById(R.id.tv_data);
        tvTimer = findViewById(R.id.tv_timer);
        tvMe = findViewById(R.id.tv_me);
    }

    //导航点击事件
    @Override
    public void onClick(View view) {
        ivSchedule.setSelected(false);
        ivTodo.setSelected(false);
        ivCalendar.setSelected(false);
        ivData.setSelected(false);
        ivTimer.setSelected(false);
        ivMe.setSelected(false);
        switch (view.getId()){
            case R.id.ll_schedule:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                fragmentTransaction.replace(R.id.fcv,scheduleFragment).commit();
                ivSchedule.setSelected(true);
                break;
            case R.id.ll_todo:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                TodoFragment todoFragment = new TodoFragment();
                fragmentTransaction.replace(R.id.fcv,todoFragment).commit();
                ivTodo.setSelected(true);
                break;
            case R.id.ll_calendar:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                CalendarFragment calendarFragment = new CalendarFragment();
                fragmentTransaction.replace(R.id.fcv,calendarFragment).commit();
                ivCalendar.setSelected(true);
                break;
            case R.id.ll_data:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                DataFragment dataFragment = new DataFragment();
                fragmentTransaction.replace(R.id.fcv,dataFragment).commit();
                ivData.setSelected(true);
                break;
            case R.id.ll_timer:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                TimerFragment timerFragment = new TimerFragment();
                fragmentTransaction.replace(R.id.fcv,timerFragment).commit();
                ivTimer.setSelected(true);
                break;
            case R.id.ll_me:
                if(isLogin){
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    MeFragment meFragment = new MeFragment();
                    fragmentTransaction.replace(R.id.fcv,meFragment).commit();
                    ivMe.setSelected(true);
                }
                else {
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    MeNoLoggedFragment meNoLoggedFragment = new MeNoLoggedFragment();
                    fragmentTransaction.replace(R.id.fcv,meNoLoggedFragment).commit();
                    ivMe.setSelected(true);
                }
            default:
                break;
        }
    }

    //检查登录状态 getDataFromSpf
    private void checkLoginStatus() {
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        isLogin = spf.getBoolean("Login", false);
        if(!isLogin) {
            isLogin = spf.getBoolean("isLogin", false);
            if(isLogin){
                SharedPreferences.Editor edit = spf.edit();
                edit.putBoolean("Login", true);
            }
        }
    }

    private void getDataFromSpf() {
        SharedPreferences spfRecord = getSharedPreferences("spfRecord", MODE_PRIVATE);
        String nickName = spfRecord.getString("account", "");
        String sign = spfRecord.getString("sign", "");
        String image64 = spfRecord.getString("image_64", "");

        tvNickName.setText(nickName);
        tvSign.setText(sign);
        if(!image64.equals("")){
            ivHead.setImageBitmap(ImageUtil.base64ToImage(image64));
        }
    }

    //接收返回信息
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //从login返回
//        if (requestCode == 1 && resultCode == 1 && data != null) {
//            ToastUtil.toastLong(MainActivity.this, "sss");
//            if(isLogin){
//                fragmentManager = getSupportFragmentManager();
//                fragmentTransaction = fragmentManager.beginTransaction();
//                MeFragment meFragment = new MeFragment();
//                fragmentTransaction.replace(R.id.fcv,meFragment).commit();
//                ivMe.setSelected(true);
//            }
//            else {
//                fragmentManager = getSupportFragmentManager();
//                fragmentTransaction = fragmentManager.beginTransaction();
//                MeNoLoggedFragment meNoLoggedFragment = new MeNoLoggedFragment();
//                fragmentTransaction.replace(R.id.fcv,meNoLoggedFragment).commit();
//                ivMe.setSelected(true);
//            }
//        }
//    }

    //main结束时把Login置为false
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        SharedPreferences.Editor edit = spf.edit();
        edit.putBoolean("Login", false);
    }
}