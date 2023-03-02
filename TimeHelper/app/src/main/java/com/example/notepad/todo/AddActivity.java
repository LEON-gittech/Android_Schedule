package com.example.notepad.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.notepad.R;
import com.example.notepad.bean.Todo;
import com.example.notepad.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener {

    private EditText etTitle,etContent;
    private TextView tvStime, tvEtime;
    LinearLayout llStime, llEtime;
    private int year, month, day;
    private StringBuffer date, date2;
    private TodoDbOpenHelper mTodoDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        date = new StringBuffer();
        date2 = new StringBuffer();

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        tvStime = findViewById(R.id.tv_stime);
        tvEtime = findViewById(R.id.tv_etime);
        llStime = findViewById(R.id.ll_stime);
        llEtime = findViewById(R.id.ll_etime);

        llStime.setOnClickListener(this);
        llEtime.setOnClickListener(this);

        mTodoDbOpenHelper = new TodoDbOpenHelper(this);

        initDateTime();
    }

    //新建一条TODO
    public void add(View view) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        String stime = tvStime.getText().toString();
        String etime = tvEtime.getText().toString();

        if (TextUtils.isEmpty(title)) {
            ToastUtil.toastShort(this, "Title is empty!");
            return;
        }

        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setContent(content);
        todo.setCreatedTime(getCurrentTimeFormat());
        todo.setStartTime(stime);
        todo.setEndTime(etime);

        long row = mTodoDbOpenHelper.insertData(todo);
        if(row != -1){
            ToastUtil.toastShort(this, "Adding done!");
            this.finish();
        }
        else{
            ToastUtil.toastShort(this, "Adding failed!");
        }

    }
    //获取时间
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ll_stime){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (date.length() > 0) { //清除上次记录的日期
                        date.delete(0, date.length());
                    }
                    tvStime.setText(date.append(String.valueOf(year)).append("/").append(String.valueOf(month)).append("/").append(day));
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();
            View dialogView = View.inflate(this, R.layout.dialog_date, null);
            final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

            dialog.setTitle("Set Date");
            dialog.setView(dialogView);
            dialog.show();
            //初始化日期监听事件
            datePicker.init(year, month - 1, day, this);
        }

        else if(v.getId() == R.id.ll_etime) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (date2.length() > 0) { //清除上次记录的日期
                        date2.delete(0, date2.length());
                    }
                    tvEtime.setText(date2.append(String.valueOf(year)).append("/").append(String.valueOf(month)).append("/").append(day));
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();
            View dialogView = View.inflate(this, R.layout.dialog_date, null);
            final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

            dialog.setTitle("Set Date");
            dialog.setView(dialogView);
            dialog.show();
            //初始化日期监听事件
            datePicker.init(year, month - 1, day, this);
        }
    }

    //日期改变的监听事件
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1;
        this.day = dayOfMonth;
    }
}