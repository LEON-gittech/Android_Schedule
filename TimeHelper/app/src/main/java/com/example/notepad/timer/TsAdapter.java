package com.example.notepad.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.R;
import com.example.notepad.bean.TimeSlot;

import java.util.List;

public class TsAdapter extends RecyclerView.Adapter<TsAdapter.MyViewHolder2> {

    private List<TimeSlot> mBeanList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public TsAdapter(Context context,List<TimeSlot> mBeanList){
        this.mBeanList = mBeanList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.ts_list_item_layout, parent, false);
        MyViewHolder2 myViewHolder = new MyViewHolder2(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        TimeSlot ts = mBeanList.get(position);
        holder.tvTitle.setText(" " + ts.getTitle());
        holder.tvTime.setText("开始于 " + ts.getStartTime() + "  持续 " + ts.getTime()/60 + " 分钟");
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public void refreshData(List<TimeSlot> tss){
        this.mBeanList = tss;
        notifyDataSetChanged();//重新加载Adapter
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder{

        ImageView ivImage;
        TextView tvTitle, tvTime;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tv_timer_title);
            this.tvTime = itemView.findViewById(R.id.tv_timer_time);
            this.ivImage = itemView.findViewById(R.id.iv_timer_do);
        }
    }
}
