package com.example.notepad.me;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notepad.R;
import com.example.notepad.util.ToastUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText etAccount,etPass,etPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        etAccount = findViewById(R.id.et_account);
        etPass = findViewById(R.id.et_password);
        etPassConfirm = findViewById(R.id.et_password_confirm);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = etAccount.getText().toString();
        String pass = etPass.getText().toString();
        String passConfirm = etPassConfirm.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.equals(pass,passConfirm)) {
            Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_LONG).show();
            return;
        }

        //查看是否授权
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            //如果授权了，就发送注册请求
            registerRequest(name, pass);
        }
        else{
            ToastUtil.toastLong(RegisterActivity.this, "没有获得网络权限");
        }

//        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
//        SharedPreferences.Editor edit = spf.edit();
//        edit.putString("account", name);
//        edit.putString("password", pass);
//        edit.apply();
        this.finish();

    }
    private void registerRequest(String name, String pass){
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                            .url("http://192.168.1.4:8080/user/register")
                            .post(RequestBody.create(MediaType.parse("application/json"),json))
                            .build();
                    //接收返回结果
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();

                    if(data.equals("true")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastLong(RegisterActivity.this, "注册成功");
                            }
                        });
                    }
                    else if(data.equals("false")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastLong(RegisterActivity.this, "用户名已存在，注册失败");
                            }
                        });
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLong(RegisterActivity.this, "发送请求失败");
                        }
                    });
                }
            }
        }).start();
    }

}