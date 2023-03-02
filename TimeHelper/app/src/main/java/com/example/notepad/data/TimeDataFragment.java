package com.example.notepad.data;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.notepad.MainActivity;
import com.example.notepad.R;
import com.example.notepad.timer.TimerDbOpenHelper;
import com.example.notepad.todo.TodoDbOpenHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class TimeDataFragment extends Fragment {

    private Toolbar mToolbar;

    private LinearLayout llTodos, llTimes;
    private ImageView ivTodos, ivTimes;

    private TextView tvTimes, tvTimesAll;
    private TimerDbOpenHelper timerDbOpenHelper;
    private LineChartView chart;

    private final int numberOfPoints = 7;
    private final int number = 720;
    private String[] days = new String[numberOfPoints];
    private float[] times = new float[numberOfPoints];
    private ValueShape shape = ValueShape.CIRCLE;

    public TimeDataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = view.findViewById(R.id.data_time_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        timerDbOpenHelper = new TimerDbOpenHelper(getActivity());
        tvTimes = view.findViewById(R.id.tv_today_times);
        tvTimesAll = view.findViewById(R.id.tv_times_all);
        chart = view.findViewById(R.id.chart3);

        llTodos = view.findViewById(R.id.ll_todos);
        llTimes = view.findViewById(R.id.ll_times);
        ivTodos = view.findViewById(R.id.iv_todos);
        ivTimes = view.findViewById(R.id.iv_times);
        ivTimes.setSelected(true);
        ivTodos.setSelected(false);

        setDay();
        getData();

        tvTimesAll.setText(String.valueOf(timerDbOpenHelper.queryTimesSumFromDb()/60) + "分钟");
        tvTimes.setText(String.valueOf(((int) times[numberOfPoints - 1])/60) + "分钟");

        generateData();
        resetViewport();

        llTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("id",4);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private void setDay(){
        Calendar calendar = Calendar.getInstance();
        for (int i = numberOfPoints - 1; i >= 0; i--) {
            String day = String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            days[i] = day;
            calendar.add(Calendar.DAY_OF_MONTH,-1);
        }
    }

    private void getData() {
        int[] timesList = timerDbOpenHelper.queryTimesInAWeekFromDb();
        for (int i = 0; i < numberOfPoints; i++) {
            times[i] = (float) (timesList[i] / 60);
        }
    }

    //设置数轴范围
    private void resetViewport() {
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = number;
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    //设置图标
    private void generateData() {
        List<Line> lines = new ArrayList<Line>();
        List<AxisValue> axisXValues = new ArrayList<AxisValue>();

        for (int i = 0; i < numberOfPoints; i++) {
            axisXValues.add(i, new AxisValue(i).setLabel(days[i]));
        }

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numberOfPoints; i++) {
            values.add(new PointValue(i, times[i]));
        }

        Line line = new Line(values);
        line.setColor(Color.parseColor("#a75cfb"));
        line.setShape(shape);//设置形状
        line.setCubic(true);//设置线为曲线
        line.setFilled(true);//设置填满
        line.setHasLabels(true);//显示便签
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);


        LineChartData data = new LineChartData(lines);

        //传入底部list数据
        Axis axisX = new Axis();
        //底部标注是否斜着显示
        axisX.setHasTiltedLabels(false);
        //字体大小
        axisX.setTextSize(12);
        //字体颜色
        axisX.setTextColor(Color.parseColor("#666666"));
        //各标签之间的距离 (0-32之间)
        axisX.setMaxLabelChars(0);
        //是否显示坐标线
        axisX.setHasLines(true);
        axisX.setValues(axisXValues);
        data.setAxisXBottom(axisX);

        //左边参数设置
        Axis axisY = new Axis();
        axisY.setTextSize(12);
        axisY.setTextColor(Color.parseColor("#666666"));
        axisY.setHasLines(true);
        data.setAxisYLeft(axisY);

        ////设置数据的初始值，即所有的数据从baseValue开始计算，默认值为0
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);
    }
}