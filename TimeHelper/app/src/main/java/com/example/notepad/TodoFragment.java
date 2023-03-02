package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.notepad.todo.TodoAdapter;
import com.example.notepad.bean.Todo;
import com.example.notepad.todo.AddActivity;
import com.example.notepad.todo.TodoDbOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mBtnAdd;
    private List<Todo> mTodos;
    private TodoAdapter mTodoAdapter;
    private TodoDbOpenHelper mTodoDbOpenHelper;

    int year = -1;
    int month = -1;
    int day = -1;

    public TodoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity().getIntent().getIntExtra("id", 0) == 6) {
            year = getActivity().getIntent().getIntExtra("year", -1);
            month = getActivity().getIntent().getIntExtra("month", -1);
            day = getActivity().getIntent().getIntExtra("day", -1);
            getActivity().getIntent().removeExtra("id");
        }
        refreshDataFromDb();
        initAnim();
    }

    // 创建时绑定布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    // fragment已创建时
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = view.findViewById(R.id.todo_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mRecyclerView = view.findViewById(R.id.rlv);
        mTodos = new ArrayList<>();
        mTodoDbOpenHelper = new TodoDbOpenHelper(getActivity());
        mTodoAdapter = new TodoAdapter(getActivity(), mTodos);
        mRecyclerView.setAdapter(mTodoAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mBtnAdd = view.findViewById(R.id.fcb);

        //点击+按钮，进入新建界面AddActivity
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        year = -1;
        month = -1;
        day = -1;
    }

    private void initAnim() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.todo_anim);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        layoutAnimationController.setDelay(0.2f);
        mRecyclerView.setLayoutAnimation(layoutAnimationController);
    }

    //重新加载数据
    private void refreshDataFromDb() {
        if(year == -1)
            mTodos = mTodoDbOpenHelper.queryAllFromDb();
        else{
            mTodos = mTodoDbOpenHelper.querySelectDayDDLFromDb(year, month, day);
            mToolbar.setTitle(year + "/" + month + "/" + day);
            year = -1;
            month = -1;
            day = -1;
        }
        mTodoAdapter.refreshData(mTodos);
    }

    //重新加载数据
    private void getTodayDataFromDb() {
        mTodos = mTodoDbOpenHelper.queryTodayFromDb();
        mTodoAdapter.refreshData(mTodos);
    }

    //重新加载数据
    private void getStarDataFromDb() {
        mTodos = mTodoDbOpenHelper.queryStarFromDb();
        mTodoAdapter.refreshData(mTodos);
    }

    // 创建搜索菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        //搜索框
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        //查询监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //查询提交时执行
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            //查询输入时执行
            @Override
            public boolean onQueryTextChange(String newText) {
                mTodos = mTodoDbOpenHelper.queryFromDbByTitle(newText);
                mTodoAdapter.refreshData(mTodos);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    //菜单点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_all:
                refreshDataFromDb();
                initAnim();
                break;
            case R.id.menu_today:
                getTodayDataFromDb();
                initAnim();
                break;
            case R.id.menu_star:
                getStarDataFromDb();
                initAnim();
                break;
            default:
        }
        return true;
    }

}