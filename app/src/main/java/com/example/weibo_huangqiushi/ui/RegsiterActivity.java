package com.example.weibo_huangqiushi.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weibo_huangqiushi.R;
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

public class RegsiterActivity extends AppCompatActivity {
    private Button btn_exit;
    private Button btn_register;
    private EditText edt_phone;
    private EditText edt_password;
    private EditText edt_pwConfirm;
    private String phone = null;
    private String password = null;
    private String pwConfirm = null;
    private Context mContext;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter);

        mContext = this;
        initView();
        setListener();

        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if(message.what==0){
                    Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
                }
                if(message.what==100){
                    Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if(message.what==200){
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void initView() {
        edt_phone = findViewById(R.id.login_editText_phone2);
        edt_password = findViewById(R.id.login_editText_password2);
        edt_pwConfirm = findViewById(R.id.login_editText_passwordconfirm);

        btn_exit = findViewById(R.id.btn_exit2);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setEnabled(false);
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
                if (isOK()) {
                    btn_register.setEnabled(true);
                } else {
                    btn_register.setEnabled(false);
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
                if (isOK()) {
                    btn_register.setEnabled(true);
                } else {
                    btn_register.setEnabled(false);
                }
            }
        });
        edt_pwConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                pwConfirm = editable.toString();
                Log.d("isOK", "isOK: "+isOK());
                if (isOK()) {
                    btn_register.setEnabled(true);
                } else {
                    btn_register.setEnabled(false);
                }
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRegister();
            }
        });
    }
    private void postRegister(){
        String url = Localhost.url + "/login/register";
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
                message.what = 200;
                message.obj="注册失败";
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Message message = handler.obtainMessage();
                if(json.contains("success")){
                    message.what = 100;
                }
                if(json.contains("手机号已注册")){
                    message.what=200;
                    message.obj="手机号已注册";
                }
                handler.sendMessage(message);
            }
        });

    }

    private Boolean isOK() {
        return  phone != null
                && password != null
                && pwConfirm != null
                && phone.length() == 11
                && !password.isEmpty()
                && !pwConfirm.isEmpty()
                && password.length() <= 20
                && pwConfirm.length() <= 20
                && Objects.equals(password, pwConfirm);
    }
}