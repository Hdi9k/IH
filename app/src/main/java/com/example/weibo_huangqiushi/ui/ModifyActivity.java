package com.example.weibo_huangqiushi.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.ui.data.Result;
import com.example.weibo_huangqiushi.until.Localhost;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyActivity extends AppCompatActivity {
    private RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
    private Button btn_modify;
    private String avatar=null;
    private String username=null;
    private String phone=null;
    private String password=null;
    private String imgUri=null;
    private ImageView img_avatar;
    private ObjectAnimator objectAnimator;
    private Handler handler;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        Intent intent=getIntent();
        avatar=intent.getStringExtra("avatar");
        username=intent.getStringExtra("username");
        phone=intent.getStringExtra("phone");

        initView();

        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                return false;
            }
        });
    }

    private void initView(){
        sp = this.getSharedPreferences("data", MODE_PRIVATE);
        Button btn_return=findViewById(R.id.btn_exit2);
        btn_modify=findViewById(R.id.btn_modify);
        img_avatar=findViewById(R.id.modify_img);
        EditText edt_username=findViewById(R.id.modify_edt_username);
        EditText edt_phone=findViewById(R.id.modify_edt_phone);
        EditText edt_password=findViewById(R.id.modify_edt_password);

        if(avatar!=null){
            Glide.with(this).load(avatar).apply(requestOptions).into(img_avatar);
        }
        edt_username.setText(username);
        edt_phone.setText(phone);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.isEmpty()||phone.isEmpty()){
                    Toast.makeText(ModifyActivity.this, "修改内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phone.length()!=11){
                    Toast.makeText(ModifyActivity.this, "手机号需要为11位", Toast.LENGTH_SHORT).show();
                    return;
                }
                postModify();
            }
        });
        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                username=editable.toString();
            }
        });
        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phone=editable.toString();
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
                password=editable.toString();
            }
        });
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }
    private void postModify(){
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String url = Localhost.url + "/user/update";
        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        if(avatar!=null)hashMap.put("avatar",imgUri);
        if(username!=null)hashMap.put("username",username);
        if(phone!=null)hashMap.put("phone",phone);
        if(password!=null)hashMap.put("password",password);
        String json=gson.toJson(hashMap);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .put(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ModifyActivity.this, "修改信息失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                if(json.contains("success")){
                    runOnUiThread(() -> {
                        Toast.makeText(ModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this).load(R.drawable.baseline_change_circle_24).apply(requestOptions).into(img_avatar);
            btn_modify.setEnabled(false);
            btn_modify.setText("等待头像上传");
            btn_modify.setTextColor(0x88000000);
            uploadImg(imageUri);
        }
    }
    private void uploadImg(Uri uri){
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String url = Localhost.url + "/info/upload";
        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uri != null) {
            String filePath = getRealPathFromUri(uri);
            File file = new File(filePath);
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(ModifyActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String json = response.body().string();
                    if (json.contains("服务端错误")) {
                        runOnUiThread(() -> {
                            Toast.makeText(ModifyActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    if (json.contains("success")) {
                        Type resultType = new TypeToken<Result<String>>() {
                        }.getType();
                        Result<String> result = gson.fromJson(json, resultType);
                        String uri1 = result.getData();
                        if (uri1 != null){
                            imgUri=uri1;
                            Log.d("TAG", "onResponseModifyImg: "+imgUri);
                            runOnUiThread(() -> {
                                Toast.makeText(ModifyActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                                Glide.with(ModifyActivity.this).load(imgUri).apply(requestOptions).into(img_avatar);
                                btn_modify.setEnabled(true);
                                btn_modify.setText("确认");
                                btn_modify.setTextColor(0xff0D84FF);
                            });
                        }

                    }
                }
            });
        }
    }
    private String getRealPathFromUri(Uri uri) {
        String[] filePaths = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, filePaths, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(filePaths[0]);
        String filePath = cursor.getString(index);
        cursor.close();
        return filePath;
    }

}