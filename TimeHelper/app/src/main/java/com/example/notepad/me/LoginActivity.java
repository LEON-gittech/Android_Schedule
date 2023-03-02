package com.example.notepad.me;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.MainActivity;
import com.example.notepad.R;
import com.example.notepad.util.ToastUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etAccount,etPassword;
    private CheckBox cbRemember,cbAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();

                if(account.equals("") || password.equals("")){
                    ToastUtil.toastLong(LoginActivity.this, "请填写完整");
                    return;
                }

                //发送请求
                LoginRequestThread t = new LoginRequestThread(account, password);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (t.flag) {
                    if (cbRemember.isChecked()) {
                        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                        SharedPreferences.Editor edit = spf.edit();
                        edit.putString("account", account);
                        edit.putString("password", password);
                        edit.putBoolean("isRemember", true);
                        edit.putBoolean("Login", true);
                        edit.putBoolean("isLogin", cbAutoLogin.isChecked());
                        edit.apply();
                    }else {
                        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                        SharedPreferences.Editor edit = spf.edit();
                        edit.putString("account", account);
                        edit.putBoolean("Login", true);
                        edit.putBoolean("isRemember", false);
                        edit.putBoolean("isLogin", false);
                        edit.apply();
                    }
                    //回到main activity
                    Intent intent = new Intent();
                    intent.putExtra("id",5);
                    setResult(1,intent);
                    finish();
                }
            }
        });

        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbRemember.setChecked(true);
                }
            }
        });

        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    cbAutoLogin.setChecked(false);
                }
            }
        });
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        etAccount = findViewById(R.id.et_login_account);
        etPassword = findViewById(R.id.et_login_password);
        cbRemember = findViewById(R.id.cb_remember);
        cbAutoLogin = findViewById(R.id.cb_auto_login);
    }

    private void initData() {
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        boolean isRemember = spf.getBoolean("isRemember", false);
        boolean isLogin = spf.getBoolean("isLogin", false);
        String account = spf.getString("account", "");
        String password = spf.getString("password", "");

        if (isLogin) {
            Intent intent = new Intent();
            intent.putExtra("id",5);
            setResult(1,intent);
            finish();
        }

        if (isRemember) {
            etAccount.setText(account);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }
    }

    //去注册
    public void toRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    class LoginRequestThread extends Thread{
        public String name;
        public String pass;
        public boolean flag = false;

        public LoginRequestThread(String name, String pass){
            this.name = name;
            this.pass = pass;
        }

        @Override
        public void run() {
            super.run();
            try {
                //传json
                String json = "{\n" +
                        "    \"username\": \""+ name + "\",\n" +
                        "    \"password\": \"" + pass +"\"\n" +
                        "}";
                // http client
                OkHttpClient client = new OkHttpClient();
                // 创建http请求
                Request request = new Request.Builder()
                        .url("http://192.168.1.4:8080/user/login")
                        .post(RequestBody.create(MediaType.parse("application/json"),json))
                        .build();
                //接收返回结果
                Response response = client.newCall(request).execute();
                String data = response.body().string();

                if(data.equals("true")) {
                    flag = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLong(LoginActivity.this, "登录成功");
                        }
                    });
                }
                else if(data.equals("false")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLong(LoginActivity.this, "用户名或密码错误，登录失败");
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toastLong(LoginActivity.this, "发送请求失败");
                    }
                });
            }
        }

    }

}