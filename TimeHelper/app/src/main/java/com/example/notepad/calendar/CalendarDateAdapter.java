package com.example.notepad.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.notepad.MainActivity;
import com.example.notepad.R;
import com.example.notepad.bean.DateBean;
import com.example.notepad.todo.TodoDbOpenHelper;

import java.util.Calendar;
import java.util.List;

public class CalendarDateAdapter extends BaseAdapter {
    private Context context;
    private List<DateBean> mData;
    private TodoDbOpenHelper todoDbOpenHelper;

    public CalendarDateAdapter(Context context, List<DateBean> mData) {
        this.context = context;
        this.mData = mData;
        this.todoDbOpenHelper = new TodoDbOpenHelper(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.calendar_data_item, null);
            viewHolder = new ViewHolder();
            viewHolder.llDate = view.findViewById(R.id.ll_calendar_date);
            viewHolder.dateTv = view.findViewById(R.id.date_tv);
            viewHolder.ddlState = view.findViewById(R.id.ddl_state);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DateBean data = mData.get(i);
        if (data.getDay() != 0) {
            viewHolder.dateTv.setText("" + data.getDay());
            if(!todoDbOpenHelper.queryIfDoneFromDbByEndTime(data.getYear(),data.getMonth(), data.getDay()))
                viewHolder.ddlState.setSelected(true);
        } else {
            viewHolder.dateTv.setText("");
            viewHolder.ddlState.setVisibility(View.GONE);
        }

        //选中日期 表示为今天
        Calendar calendar = Calendar.getInstance();
        if (data.getYear() == calendar.get(Calendar.YEAR) && data.getMonth() == (calendar.get(Calendar.MONTH) + 1) && data.getDay() == calendar.get(Calendar.DAY_OF_MONTH)) {
            viewHolder.dateTv.setText("今天");
        }

        viewHolder.llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToastUtil.toastLong(context, "Now: " + data.getYear() + data.getMonth() + data.getDay());
                //跳转到TodoFragment
                Intent intent = new Intent(context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("year", data.getYear());
                bundle.putInt("month", data.getMonth());
                bundle.putInt("day", data.getDay());
                intent.putExtras(bundle);
                intent.putExtra("id",6);
                context.startActivity(intent);
                getActivity(context).finish();
            }
        });

        return view;
    }

    //根据context 获取 其Activity
    private Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    static class ViewHolder {
        public LinearLayout llDate;
        public TextView dateTv;
        public ImageView ddlState;
    }
}