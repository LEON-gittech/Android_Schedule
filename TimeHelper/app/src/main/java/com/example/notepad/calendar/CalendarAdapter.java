package com.example.notepad.calendar;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<com.example.notepad.calendar.CalendarView> {
    private List<Calendar> calendar = new ArrayList<>();

    public CalendarAdapter() {
    }

    @NonNull
    @Override
    public com.example.notepad.calendar.CalendarView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new com.example.notepad.calendar.CalendarView(LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.notepad.calendar.CalendarView holder, int position) {
        if (calendar.size() != 0)
            holder.initData(calendar.get(position));
    }

    @Override
    public int getItemCount() {
        return calendar.size();
    }

    public void refreshData(List<Calendar> data) {
        for (int i = 0; i < data.size(); i++) {
            calendar.add(data.get(i));
        }
        notifyDataSetChanged();
    }

}
