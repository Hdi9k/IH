package com.example.weibo_huangqiushi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.ui.data.Json_Data;
import com.example.weibo_huangqiushi.until.Localhost;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_phone;
    private EditText edt_password;
    private Button btn_login;
    private Button btn_exit;
    private Button btn_goRegister;
    private String phone;
    private String password;
    private SharedPreferences sp;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private TextView dialog_tx1;
    private TextView dialog_tx2;
    private Handler handler;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        initView();
        setListener();


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 100) {
                    Toast.makeText(LoginActivity.this, "已成功发送验证码", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 200) {
                    Toast.makeText(LoginActivity.this, "发送验证码失败", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 300) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (message.what == 400) {
                    Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 500) {
                    Toast.makeText(LoginActivity.this, "短信验证码错误", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 600) {
                    Toast.makeText(LoginActivity.this, "登录错误", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void initView() {
        sp = getSharedPreferences("data", MODE_PRIVATE);
        edt_phone = findViewById(R.id.login_editText_phone);
        edt_password = findViewById(R.id.login_editText_password);
        btn_login = findViewById(R.id.btn_login);
        btn_exit = findViewById(R.id.btn_exit);
        btn_goRegister = findViewById(R.id.btn_goRegister);
        btn_login.setEnabled(false);
    }

    private void setListener() {
        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phone = editable.toString();
                if (password != null && phone.length() == 11 && !password.isEmpty()) {
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
                }
            }
        });
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = editable.toString();
                if (phone != null && phone.length() == 11 && !password.isEmpty()) {
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
                }
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone != null && password != null) {
                    if (phone.length() != 11 || password.length() >= 20) {
                        Toast.makeText(LoginActivity.this, "未输入正确的手机号或密码", Toast.LENGTH_SHORT).show();
                    } else if (!sp.getBoolean("isAgree", false)) {
                        Toast.makeText(LoginActivity.this, "您还未同意《用户协议》与《隐私政策》", Toast.LENGTH_SHORT).show();
                        initDialog();
                    } else {
                        login();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "手机号或密码为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegsiterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String url = Localhost.url + "/login/login";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("phone", phone);
        hashMap.put("password", password);
        Gson gson = new Gson();
        String json = gson.toJson(hashMap);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = handler.obtainMessage();
                message.what = 600;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //解析json
                Gson gson = new Gson();
                String json = response.body().string();
                Json_Data jsonData = gson.fromJson(json, Json_Data.class);
                Log.d("OkHttp", "onResponse:data " + jsonData.toString());
                if (Objects.equals(jsonData.getMsg(), "手机号或密码错误")) {
                    Message message = handler.obtainMessage();
                    message.what = 400;
                    handler.sendMessage(message);
                }
                if (Objects.equals(jsonData.getMsg(), "success")) {
                    Log.d("OkHttp", "onResponse: " + jsonData.getData());
                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", jsonData.getData().toString());
                    editor.apply();
                    Message message = handler.obtainMessage();
                    message.what = 300;
                    handler.sendMessage(message);
                }

            }
        });
    }

    private void initDialog() {

        builder = new AlertDialog.Builder(LoginActivity.this);
        final LayoutInflater inflater = this.getLayoutInflater();
        View view_custom;
        view_custom = inflater.inflate(R.layout.layout_dialog, null, false);
        builder.setView(view_custom);
        alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_tx1 = view_custom.findViewById(R.id.dialog_title);
        dialog_tx2 = view_custom.findViewById(R.id.dialog_content);
        dialog_tx1.setText("声明与条款");
        //dialog_tx2.setText("内容说明");
        setClickText();
        alertDialog.setCancelable(false);
        alertDialog.show();
        window.setLayout((int) getResources().getDisplayMetrics().density * 320, (int) getResources().getDisplayMetrics().density * 280);

        view_custom.findViewById(R.id.dialog_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        view_custom.findViewById(R.id.dialog_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isAgree", true);
                editor.apply();
                alertDialog.dismiss();

            }
        });
    }

    private void setClickText() {
        SpannableString spannableString = new SpannableString(getString(R.string.ih_privacy));

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                alertDialog.dismiss();
                pivicyDialog(2);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF0D84FF);
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                alertDialog.dismiss();
                pivicyDialog(1);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF0D84FF);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan1, 46, 52, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 53, 59, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dialog_tx2.setMovementMethod(LinkMovementMethod.getInstance());
        dialog_tx2.setText(spannableString);
    }

    private void pivicyDialog(int type) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        if (type == 1) {
            builder1.setTitle("隐私政策");
            builder1.setMessage(getString(R.string.privacy_content));

        } else if (type == 2) {
            builder1.setTitle("用户协议");
            builder1.setMessage(getString(R.string.user_content));
        }
        builder1.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                alertDialog.show();
            }
        });
        builder1.create().show();
    }
}