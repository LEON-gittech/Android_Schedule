package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepad.bean.TimeSlot;
import com.example.notepad.bean.Todo;
import com.example.notepad.timer.TimerDbOpenHelper;
import com.example.notepad.timer.TsAdapter;
import com.example.notepad.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimerFragment extends Fragment {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private TsAdapter tsAdapter;

    private Chronometer ch;//计时器
    private TextView tvTodoTitle;
    private Button begin;//开始计时按钮
    private Button end;//结束计时按钮

    private List<TimeSlot> tss;

    private String cur_date;
    private String start_time;
    private long recordingTime;//记录总时间
    private String todo_title = "";

    TimerDbOpenHelper timerDbOpenHelper;

    public TimerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDataFromDb();
    }

    private void refreshDataFromDb() {
        tss = timerDbOpenHelper.queryAllFromDb();
        tsAdapter.refreshData(tss);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = view.findViewById(R.id.timer_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        tss = new ArrayList<>();

        timerDbOpenHelper = new TimerDbOpenHelper(getActivity());
        mRecyclerView = view.findViewById(R.id.rlv_timer);
        tvTodoTitle = view.findViewById(R.id.tv_todo_title);
        ch = (Chronometer)view.findViewById(R.id.timer);
        begin = (Button)view.findViewById(R.id.begin);
        end = (Button)view.findViewById(R.id.end);

        tsAdapter = new TsAdapter(getActivity(),tss);
        mRecyclerView.setAdapter(tsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if(getActivity().getIntent().getIntExtra("id", 0) == 2) {
            todo_title = getActivity().getIntent().getStringExtra("todo");
            if (!todo_title.equals(""))
                tvTodoTitle.setText("正在专注\n" + todo_title);
            getActivity().getIntent().removeExtra("id");
        }

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_time = getCurrentTimeFormat();
                cur_date = getCurrentTimeFormat2();
                ch.setBase(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime()获取的是系统开机到现在的时间，不能被修改
                ch.start();//开始计时
                begin.setEnabled(false);
                end.setEnabled(true);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.stop();//停止计时
                recordingTime = SystemClock.elapsedRealtime()-ch.getBase();//保存当前停止的时间
                begin.setEnabled(true);
                end.setEnabled(false);

                if(recordingTime > 1000) {
                    ToastUtil.toastShort(getActivity(),"已记录");
                    TimeSlot ts = new TimeSlot();
                    ts.setTime((int) (recordingTime / 1000));
                    ts.setStartTime(start_time);
                    ts.setDate(cur_date);
                    if (todo_title != null) {
                        ts.setTitle(todo_title);
                    }
                    else {
                        ts.setTitle("无名任务");
                    }
                    timerDbOpenHelper.insertData(ts);
                }
                else
                    ToastUtil.toastLong(getActivity(),"请至少坚持十分钟");
                tvTodoTitle.setText("选择一个任务开始专注");
                refreshDataFromDb();
            }
        });
        ch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {//为Chronomter绑定事件监听器
            @Override
            public void onChronometerTick(Chronometer chronometer) {
            }
        });
    }

    //获取时间
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    //获取时间
    private String getCurrentTimeFormat2() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}