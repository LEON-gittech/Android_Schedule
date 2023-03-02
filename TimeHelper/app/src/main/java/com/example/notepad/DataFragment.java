package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.notepad.todo.TodoDbOpenHelper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

public class DataFragment extends Fragment {

    private Toolbar mToolbar;

    private LinearLayout llTodos, llTimes;
    private ImageView ivTodos, ivTimes;

    private TextView tvNum, tvRate, tvNumAll;
    private TodoDbOpenHelper mTodoDbOpenHelper;
    private LineChartView chart;
    private LineChartView chart2;
    private final int numberOfPoints = 7;
    private final int number = 10;
    private String[] days = new String[numberOfPoints];
    private float[] nums = new float[numberOfPoints];
    private float[] rates = new float[numberOfPoints];
    private ValueShape shape = ValueShape.CIRCLE;

    public DataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = view.findViewById(R.id.data_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mTodoDbOpenHelper = new TodoDbOpenHelper(getActivity());
        tvNum = view.findViewById(R.id.tv_num);
        tvRate = view.findViewById(R.id.tv_rate);
        tvNumAll = view.findViewById(R.id.tv_num_all);
        chart = view.findViewById(R.id.chart1);
        chart2 = view.findViewById(R.id.chart2);

        llTodos = view.findViewById(R.id.ll_todos);
        llTimes = view.findViewById(R.id.ll_times);
        ivTodos = view.findViewById(R.id.iv_todos);
        ivTimes = view.findViewById(R.id.iv_times);
        ivTodos.setSelected(true);
        ivTimes.setSelected(false);

        setDay();
        getData();

        tvNumAll.setText(String.valueOf(mTodoDbOpenHelper.queryAllDoneFromDb()));
        tvNum.setText(String.valueOf((int) nums[numberOfPoints - 1]));
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(0);//不要百分小数
        tvRate.setText(fmt.format(rates[numberOfPoints - 1]));

        generateData();
        generateData2();
        resetViewport();

        llTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("id",3);
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
        int[] doneNumList = mTodoDbOpenHelper.queryDoneNumInAWeekFromDb();
        int[] numList = mTodoDbOpenHelper.queryNumInAWeekFromDb();
        for (int i = 0; i < numberOfPoints; i++) {
            nums[i] = (float) doneNumList[i];
            if(numList[i] == 0) rates[i] = 0;
            else rates[i] = nums[i] / numList[i];
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

        final Viewport v1 = new Viewport(chart2.getMaximumViewport());
        v1.bottom = 0;
        v1.top = 1;
        v1.left = 0;
        v1.right = numberOfPoints - 1;
        chart2.setMaximumViewport(v1);
        chart2.setCurrentViewport(v1);
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
            values.add(new PointValue(i, nums[i]));
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

    //设置图标2
    private void generateData2() {
        List<Line> lines = new ArrayList<Line>();
        List<AxisValue> axisXValues = new ArrayList<AxisValue>();

        for (int i = 0; i < numberOfPoints; i++) {
            axisXValues.add(i, new AxisValue(i).setLabel(days[i]));
        }

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numberOfPoints; i++) {
            values.add(new PointValue(i, rates[i]));
        }

        Line line = new Line(values);
        line.setColor(Color.parseColor("#d896f6"));
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
        chart2.setLineChartData(data);
    }
}