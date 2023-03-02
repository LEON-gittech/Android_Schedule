package com.example.notepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepad.bean.Todo;
import com.example.notepad.bean.TodoList;
import com.example.notepad.me.EditProfileActivity;
import com.example.notepad.me.LoginActivity;
import com.example.notepad.me.RegisterActivity;
import com.example.notepad.todo.TodoDbOpenHelper;
import com.example.notepad.util.ImageUtil;
import com.example.notepad.util.ToastUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeFragment extends Fragment {

    private TextView tvNickName,tvSign;
    private ImageView ivHead;
    private Button btnEdit, btnLogout, btnUpload, btnDownload, btnTheme;

    private String username;

    private TodoDbOpenHelper mTodoDbOpenHelper;

    public MeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromSpf();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNickName = view.findViewById(R.id.tv_nick_name);
        tvSign = view.findViewById(R.id.tv_sign_text);
        ivHead = view.findViewById(R.id.iv_head);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnUpload = view.findViewById(R.id.btn_upload);
        btnDownload = view.findViewById(R.id.btn_download);
        btnTheme = view.findViewById(R.id.btn_theme);

        if (isDarkTheme()) {
            btnTheme.setText("白天模式");
        }
        else {
            btnTheme.setText("黑夜模式");
        }

        mTodoDbOpenHelper = new TodoDbOpenHelper(getActivity());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences spf = getActivity().getSharedPreferences("spfRecord", 0);
                SharedPreferences.Editor edit = spf.edit();
                edit.putBoolean("isLogin", false);
                edit.putBoolean("Login", false);
                edit.apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("id",5);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username == null) return;
                String curTime = getCurrentTimeFormat();
                List<Todo> todos = mTodoDbOpenHelper.queryAllFromDb();
                int cnt = todos.size();
                StringBuilder sb = new StringBuilder("{\"username\": \"" + username +"\"," + "\"time\": \"" + curTime + "\"," + "\"todos\": [");
                for (Todo todo: todos) {
                    sb.append(todo.toJson());
                    if(cnt != 1){
                        sb.append(",");
                        cnt--;
                    }
                }
                sb.append("]}");
                uploadRequest(sb.toString());
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username == null) return;
                DownloadRequestThread drt = new DownloadRequestThread(username);
                drt.start();
                try {
                    drt.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TodoList todoList = new Gson().fromJson(drt.output, TodoList.class);
                mTodoDbOpenHelper.refreshAllData(todoList);
            }
        });

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDarkTheme()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });
    }

    private void getDataFromSpf() {
        SharedPreferences spfRecord = getActivity().getSharedPreferences("spfRecord", 0);
        String userName = spfRecord.getString("account", "");
        String sign = spfRecord.getString("sign", "");
        String image64 = spfRecord.getString("image_64", "");

        username = userName;
        tvNickName.setText(userName);
        tvSign.setText(sign);
        if(!image64.equals("")){
            ivHead.setImageBitmap(ImageUtil.base64ToImage(image64));
        }
    }

    //获取时间
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    //判断是否为深色主题
    private boolean isDarkTheme(){
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            default:
                return false;
        }
    }

    private void uploadRequest(String json){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // http client
                    OkHttpClient client = new OkHttpClient();
                    // 创建http请求
                    Request request = new Request.Builder()
                            .url("http://192.168.1.4:8080/todo/upload")
                            .post(RequestBody.create(MediaType.parse("application/json"),json))
                            .build();
                    //接收返回结果
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();

                    if(data.equals("true")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastLong(getActivity(), "上传成功");
                            }
                        });
                    }
                    else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastLong(getActivity(), "上传失败");
                            }
                        });
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLong(getActivity(), "上传失败");
                        }
                    });
                }
            }
        }).start();
    }

//    private void downloadRequest(String input){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // http client
//                    OkHttpClient client = new OkHttpClient();
//                    // 创建请求体
//                    RequestBody requestBody = new FormBody.Builder()
//                            .add("username",input)
//                            .build();
//                    // 创建http请求
//                    Request request = new Request.Builder()
//                            .url("http://192.168.1.5:8080/todo/download")
//                            .post(requestBody)
//                            .build();
//                    //接收返回结果
//                    Response response = client.newCall(request).execute();
//                    String data = response.body().string();
//
//                    if(!data.equals("false")) {
//
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.toastLong(getActivity(), "同步成功");
//                            }
//                        });
//                    }
//                    else {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.toastLong(getActivity(), "同步失败");
//                            }
//                        });
//                    }
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.toastLong(getActivity(), "同步失败");
//                        }
//                    });
//                }
//            }
//        }).start();
//    }

    class DownloadRequestThread extends Thread {
        private String input;
        public String output;

        public DownloadRequestThread(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            super.run();
            try {
                // http client
                OkHttpClient client = new OkHttpClient();
                // 创建请求体
                RequestBody requestBody = new FormBody.Builder()
                        .add("username",input)
                        .build();
                // 创建http请求
                Request request = new Request.Builder()
                        .url("http://192.168.1.4:8080/todo/download")
                        .post(requestBody)
                        .build();
                //接收返回结果
                Response response = client.newCall(request).execute();
                String data = response.body().string();

                if(!data.equals("false")) {
                    output = "{\"todos\" : " + data + "}";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLong(getActivity(), "同步成功");
                        }
                    });
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLong(getActivity(), "同步失败");
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toastLong(getActivity(), "同步失败");
                    }
                });
            }
        }

    }

}