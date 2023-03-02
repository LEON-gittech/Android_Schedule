package com.example.notepad.todo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.MainActivity;
import com.example.notepad.R;
import com.example.notepad.bean.Todo;
import com.example.notepad.util.ToastUtil;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {

    private List<Todo> mBeanList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private TodoDbOpenHelper mTodoDbOpenHelper;

    public TodoAdapter(Context context, List<Todo> mBeanList){
        this.mBeanList = mBeanList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTodoDbOpenHelper = new TodoDbOpenHelper(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Todo todo = mBeanList.get(position);
        holder.mTvTitle.setText(todo.getTitle());
        if(todo.getContent() != null) {
            holder.mTvContent.setText(todo.getContent());
        }
        holder.mTvTime.setText(todo.getCreatedTime());
        if(todo.getStartTime() != null || todo.getEndTime() != null){
            holder.mTvTimeRange.setText(todo.getStartTime() + " - " + todo.getEndTime());
        }
        if(todo.getDone() == 0){
            holder.ivDone.setSelected(false);
            holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.black1));
            holder.mTvTitle.setPaintFlags(holder.mTvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.mTvContent.setPaintFlags(holder.mTvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        else {
            holder.ivDone.setSelected(true);
            holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.grey_900));
            holder.mTvTitle.setPaintFlags(holder.mTvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTvContent.setPaintFlags(holder.mTvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.ivDone.setSelected(todo.getDone() != 0);
        holder.ivStar.setSelected(todo.getStar() != 0);

        holder.ivDone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(todo.getDone() == 0){
                    todo.setDone(1);
                    holder.ivDone.setSelected(todo.getDone() != 0);
                    holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.grey_900));
                    holder.mTvTitle.setPaintFlags(holder.mTvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.mTvContent.setPaintFlags(holder.mTvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else if(todo.getDone() == 1){
                    todo.setDone(0);
                    holder.ivDone.setSelected(todo.getDone() != 0);
                    holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.black1));
                    holder.mTvTitle.setPaintFlags(holder.mTvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.mTvContent.setPaintFlags(holder.mTvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                mTodoDbOpenHelper.updateData(todo);
                mBeanList = mTodoDbOpenHelper.queryAllFromDb();
                notifyDataSetChanged();
            }
        });

        holder.ivStar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(todo.getStar() == 0){
                    todo.setStar(1);
                    holder.ivStar.setSelected(todo.getStar() != 0);
                }
                else if(todo.getStar() == 1){
                    todo.setStar(0);
                    holder.ivStar.setSelected(todo.getStar() != 0);
                }
                mTodoDbOpenHelper.updateData(todo);
                mBeanList = mTodoDbOpenHelper.queryAllFromDb();
                notifyDataSetChanged();
            }
        });

        //点击时，跳转到详情界面
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra("todo", todo);
                mContext.startActivity(intent);
            }
        });

        //长按
        holder.rlContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 弹出弹窗
                Dialog dialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                View dialogView = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);
                LinearLayout llDelete = dialogView.findViewById(R.id.ll_delete);
                LinearLayout llEdit = dialogView.findViewById(R.id.ll_edit);
                LinearLayout llDone = dialogView.findViewById(R.id.ll_done);
                LinearLayout llStar = dialogView.findViewById(R.id.ll_star);
                LinearLayout llDo = dialogView.findViewById(R.id.ll_do);

                llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int row = mTodoDbOpenHelper.deleteFromDbById(todo.getId());
                        if (row > 0) {
                            removeData(position);
                        }
                        // 清除弹窗
                        dialog.dismiss();
                        ToastUtil.toastShort(mContext,"删除完成");
                    }
                });

                llDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(todo.getDone() == 0){
                            todo.setDone(1);
                            holder.ivDone.setSelected(todo.getDone() != 0);
                            holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.grey_900));
                            holder.mTvTitle.setPaintFlags(holder.mTvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            holder.mTvContent.setPaintFlags(holder.mTvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        else if(todo.getDone() == 1){
                            todo.setDone(0);
                            holder.ivDone.setSelected(todo.getDone() != 0);
                            holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.black1));
                            holder.mTvTitle.setPaintFlags(holder.mTvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            holder.mTvContent.setPaintFlags(holder.mTvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                        mTodoDbOpenHelper.updateData(todo);
                        dialog.dismiss();
                        mBeanList = mTodoDbOpenHelper.queryAllFromDb();
                        notifyDataSetChanged();
                    }
                });

                llStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(todo.getStar() == 0){
                            todo.setStar(1);
                            holder.ivStar.setSelected(todo.getStar() != 0);
                        }
                        else if(todo.getStar() == 1){
                            todo.setStar(0);
                            holder.ivStar.setSelected(todo.getStar() != 0);
                        }
                        mTodoDbOpenHelper.updateData(todo);
                        dialog.dismiss();
                        mBeanList = mTodoDbOpenHelper.queryAllFromDb();
                        notifyDataSetChanged();
                    }
                });

                llEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EditActivity.class);
                        intent.putExtra("todo", todo);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });

                llDo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("todo", todo.getTitle());
                        intent.putExtras(bundle);
                        intent.putExtra("id",2);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                        getActivity(mContext).finish();
                    }
                });

                dialog.setContentView(dialogView);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                return true;
            }
        });
    }

    //获取item总数目
    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    //更新数据
    public void refreshData(List<Todo> todos) {
        this.mBeanList = todos;
        notifyDataSetChanged();//重新加载Adapter
    }

    public void removeData(int pos) {
        mBeanList.remove(pos);
        notifyItemRemoved(pos);
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

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTitle;
        TextView mTvContent;
        TextView mTvTime;
        TextView mTvTimeRange;
        ViewGroup rlContainer;
        ImageView ivDone;
        ImageView ivStar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvTitle = itemView.findViewById(R.id.tv_title);
            this.mTvContent = itemView.findViewById(R.id.tv_content);
            this.mTvTime = itemView.findViewById(R.id.tv_time);
            this.mTvTimeRange = itemView.findViewById(R.id.tv_time_range);
            this.rlContainer = itemView.findViewById(R.id.rl_item_container);
            this.ivDone = itemView.findViewById(R.id.iv_done);
            this.ivStar = itemView.findViewById(R.id.iv_star);
        }
    }
}
