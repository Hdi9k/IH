package com.example.weibo_huangqiushi.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;

import android.Manifest;


import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.ui.data.Result;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;
import com.example.weibo_huangqiushi.ui.home.adapter.ImgAdapter;
import com.example.weibo_huangqiushi.until.Localhost;
import com.example.weibo_huangqiushi.until.RequestPermissionActivityBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {
    private final static String TAG = "UploadActivity";
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_READ_STORAGE = 3;

    private RecyclerView recyclerView;
    private ImgAdapter imgAdapter;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private ArrayList<String> imgUris = new ArrayList<>();
    private EditText edt_content;
    private String tx_content = null;
    private int type_weibo = 0;
    private Handler handler;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initView();
        checkAndRequestPermissions();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 100) {
                    sendWeibo2();
                }

                return false;
            }
        });
    }

    private void initView() {
        sp = this.getSharedPreferences("data", MODE_PRIVATE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView = findViewById(R.id.weibo_recyclerView);
        recyclerView.setLayoutManager(gridLayoutManager);
        imgAdapter = new ImgAdapter();
        recyclerView.setAdapter(imgAdapter);

        imgAdapter.addOnItemChildClickListener(R.id.btn_weiboItem_close, new BaseQuickAdapter.OnItemChildClickListener<Uri>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<Uri, ?> baseQuickAdapter, @NonNull View view, int i) {
                imgAdapter.removeAt(i);
                imageUris.remove(i);
            }
        });
        imgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<Uri>() {
            @Override
            public void onClick(@NonNull BaseQuickAdapter<Uri, ?> baseQuickAdapter, @NonNull View view, int i) {
                if (i == baseQuickAdapter.getItemCount() - 1) {
                    openGallery();
                }
            }
        });

        edt_content = findViewById(R.id.edt_content);
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 1000) {
                    Toast.makeText(UploadActivity.this, "内容字数不能多于1000字", Toast.LENGTH_SHORT).show();
                }
                tx_content = editable.toString();
            }
        });

        Button btn_return = findViewById(R.id.btn_weibo_return);
        Button btn_send = findViewById(R.id.btn_weibo_send);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tx_content != null && !tx_content.isEmpty()) {
                    sendWeibo1();
                } else {
                    Toast.makeText(UploadActivity.this, "标题或内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //0无 1新闻 2娱乐 3游戏 4财经 5数码 6生活 7广告
        String[] type = {"新闻", "娱乐", "游戏", "财经", "数码", "生活", "广告"};
        RadioGroup radioGroup = findViewById(R.id.weibo_radioGroup);
        for (String t : type) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(t);
            radioButton.setBackgroundResource(R.drawable.radio_button_selector);
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton.setTextColor(getResources().getColorStateList(R.color.radio_button_text_selector));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 10, 0); // 左、上、右、下间距
            radioButton.setLayoutParams(params);
            radioGroup.addView(radioButton);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                if (radioButton != null) {
                    // 在此处理选中状态变化
                    for (int j = 0; j < type.length; j++) {
                        if (radioButton.getText() == type[j]) {
                            type_weibo = j + 1;
                        }
                    }
                    Toast.makeText(UploadActivity.this, radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(UploadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_CODE_PICK_IMAGE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    imgAdapter.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                imgAdapter.add(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "访问存储权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendWeibo1() {
        if (!imageUris.isEmpty()) {
            for (Uri uri : imageUris) {
                uploadImg(uri);
            }
        } else {
            sendWeibo2();
        }
    }

    private void sendWeibo2() {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String url = Localhost.url + "/info/add";
        Log.d(TAG, "sendWeibo2: " + url);
        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        WeiboInfo weiboInfo;
        if(type_weibo==0){
            Toast.makeText(this, "请选择微博分类", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imgUris.isEmpty()) {
            weiboInfo = new WeiboInfo(tx_content, type_weibo);
        } else {
            weiboInfo = new WeiboInfo(tx_content, imgUris, type_weibo);
        }
        String json = gson.toJson(weiboInfo);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(UploadActivity.this, "发布微博失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG, "sendWeibo2: " + json);
                if (json.contains("The WeiboInfo contains forbidden keywords.")) {
                    runOnUiThread(() -> {
                        Toast.makeText(UploadActivity.this, "微博含有屏蔽词", Toast.LENGTH_SHORT).show();
                    });
                }
                if (json.contains("success")) {
                    runOnUiThread(() -> {
                        Toast.makeText(UploadActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });

    }

    private void uploadImg(Uri uri) {
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
                            Toast.makeText(UploadActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String json = response.body().string();
                    Log.d(TAG, "onResponse:Img " + json);
                    if (json.contains("服务端错误")) {
                        runOnUiThread(() ->
                                Toast.makeText(UploadActivity.this, "服务端出错，上传图片失败", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }
                    if (json.contains("success")) {
                        Type resultType = new TypeToken<Result<String>>() {
                        }.getType();
                        Result<String> result = gson.fromJson(json, resultType);
                        String uri1 = result.getData();
                        if (uri1 != null)
                            imgUris.add(uri1);
                        if (imgUris.size() == imageUris.size()) {
                            Message message = handler.obtainMessage();
                            message.what = 100;
                            message.obj = true;
                            handler.sendMessage(message);
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

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_STORAGE);
            }
        }
    }
}