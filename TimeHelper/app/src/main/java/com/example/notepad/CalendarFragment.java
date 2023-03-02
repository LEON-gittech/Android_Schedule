package com.example.notepad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.notepad.bean.Todo;
import com.example.notepad.calendar.CalendarAdapter;
import com.example.notepad.todo.TodoDbOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    private ViewPager2 viewPager2;
    private CalendarAdapter calendarAdapter;
    private TextView dateTv, msgTv, dataTv;
    private ImageView arrowLeftImg, arrowRightImg;
    private TodoDbOpenHelper mTodoDbOpenHelper;
    View myView;

    public CalendarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myView = view;
        init();
        initData();
    }

    private void init() {
        viewPager2 = myView.findViewById(R.id.vpContainer);
        //viewPager2的适配器
        calendarAdapter = new CalendarAdapter();
        viewPager2.setAdapter(calendarAdapter);
        dateTv = myView.findViewById(R.id.date_tv);
        msgTv = myView.findViewById(R.id.tv_calendar_msg);
        dataTv = myView.findViewById(R.id.tv_calendar_msg_data);
        arrowLeftImg = myView.findViewById(R.id.arrow_left);
        arrowRightImg = myView.findViewById(R.id.arrow_right);
        mTodoDbOpenHelper = new TodoDbOpenHelper(getActivity());
    }

    private void initData() {
        List<Calendar> data = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -i);
            data.add(calendar);
        }

        calendarAdapter.refreshData(data);
        viewPager2.setCurrentItem(11, false);
        int year = data.get(data.size() - 1).get(Calendar.YEAR);
        int month = data.get(data.size() - 1).get(Calendar.MONTH) + 1;
        dateTv.setText(year + "-" + month);
        msgTv.setText(year + "年" + month + "月");
        showMsg(year, month);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int year = data.get(position).get(Calendar.YEAR);
                int month = data.get(position).get(Calendar.MONTH) + 1;
                dateTv.setText(year + "-" + month);
                msgTv.setText(year + "年" + month + "月");
                showMsg(year, month);
                RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
                View view = recyclerView.getLayoutManager().findViewByPosition(position);
                if (view != null)
                    updatePagerHeightForChild(view, viewPager2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            public void updatePagerHeightForChild(View view, ViewPager2 pager) {
                view.post(() -> {
                    int weightMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
                    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    view.measure(weightMeasureSpec, heightMeasureSpec);
                    if (pager.getLayoutParams().height != view.getMeasuredHeight()) {
                        ViewGroup.LayoutParams layoutParams = pager.getLayoutParams();
                        layoutParams.height = view.getMeasuredHeight();
                        pager.setLayoutParams(layoutParams);
                    }
                });

            }
        });

        arrowRightImg.setOnClickListener(V -> {
            arrowRightImg.post(() -> {
                if (viewPager2.getCurrentItem() != 11)
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1, false);
            });
        });
        arrowLeftImg.setOnClickListener(V -> {
            arrowLeftImg.post(() -> {
                if (viewPager2.getCurrentItem() != 0)
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1, false);
            });
        });

    }

    //展示这个月的数据
    private void showMsg(int year, int month){
        List<Todo> todos = mTodoDbOpenHelper.queryMonthDataFromDb(year, month);
        if(todos.size() == 0){
            dataTv.setText("这个月还没有需要完成的DDL");
        }
        else{
            StringBuilder dataMsg = new StringBuilder();
            int sum = todos.size();
            int cnt = 0;
            for (Todo todo : todos) {
                if(todo.getDone() == 0){
                    cnt++;
                    int res = 0;//离ddl的天数
                    SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");
                    try {
                        Date date = sdf.parse(todo.getEndTime());
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        int ddl = c.get(Calendar.DAY_OF_MONTH);
                        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        res = ddl - today;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(todo.getStar() == 1)
                        dataMsg.append("★");
                    if(res >= 0)
                        dataMsg.append(cnt + ". " + todo.getTitle() + "（还剩" + res +"天）\n");
                    else
                        dataMsg.append(cnt + ". " + todo.getTitle() + "（已错过）" + "\n");
                }
            }
            if(cnt == 0){
                dataTv.setText("这个月的DDL都已经完成");
            }
            else{
                dataTv.setText("这个月共" + sum + "个DDL，还有" + cnt + "个未完成\n" + dataMsg.toString() + "记得及时完成");
            }
        }
    }
}