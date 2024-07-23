package com.example.weibo_huangqiushi.ui.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.ui.data.Comment;
import com.example.weibo_huangqiushi.ui.data.PageBean;
import com.example.weibo_huangqiushi.ui.data.Result;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;
import com.example.weibo_huangqiushi.ui.home.adapter.BQMultipyAdapter;
import com.example.weibo_huangqiushi.ui.home.adapter.CmtAdapter;
import com.example.weibo_huangqiushi.ui.home.adapter.UniformScale;
import com.example.weibo_huangqiushi.until.Localhost;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    private final static String TAG = "DetailActivity";
    private Context mContext;
    private RecyclerView detailRecycler;
    private RecyclerView commentRecycler;
    private BQMultipyAdapter bqMultipyAdapter;
    private CmtAdapter cmtAdapter;
    private WeiboInfo weiboInfo;
    private boolean turnToComment;
    private List<Comment> comments;
    private Comment comment;
    private EditText edtComment;
    private Button btnSend;
    private String textComment = null;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContext = this;

        this.weiboInfo = getIntent().getParcelableExtra("weiboInfo");
        this.turnToComment=getIntent().getBooleanExtra("clickComment",false);

        initView();
        getComment();
    }

    private void initView() {
        detailRecycler = findViewById(R.id.detail_recyclerView);
        commentRecycler = findViewById(R.id.comment_recyclerView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext);
        detailRecycler.setLayoutManager(linearLayoutManager1);
        detailRecycler.setNestedScrollingEnabled(false);
        commentRecycler.setLayoutManager(linearLayoutManager2);
        commentRecycler.setNestedScrollingEnabled(false);
        //refresh
        initAdapter();
        bqMultipyAdapter.add(weiboInfo);
        detailRecycler.setAdapter(bqMultipyAdapter);

        sp = getSharedPreferences("data", MODE_PRIVATE);
        edtComment = findViewById(R.id.edt_comment);
        edtComment.setMaxLines(10);
        edtComment.setHorizontallyScrolling(false);

        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textComment = editable.toString();
            }
        });
        if(turnToComment){
            edtComment.requestFocus();
        }

        btnSend = findViewById(R.id.btn_sendComment);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtComment.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                sendComment();
            }
        });
    }

    private void initAdapter() {
        List<WeiboInfo> list = new ArrayList<>();
        list.add(weiboInfo);
        bqMultipyAdapter = new BQMultipyAdapter(list, detailRecycler);

        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                finish();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                edtComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtComment,0);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close1, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                finish();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment1, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                edtComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtComment,0);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close2, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                finish();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment2, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                edtComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtComment,0);
            }
        });

        int[] img_id = new int[]{R.id.card_img1, R.id.card_img2, R.id.card_img3, R.id.card_img4, R.id.card_img5, R.id.card_img6, R.id.card_img7, R.id.card_img8, R.id.card_img9};
        bqMultipyAdapter.addOnItemChildClickListener(img_id[0], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第1张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(0)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[1], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第2张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(1, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[2], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第3张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(2)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[3], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第4张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(3, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[4], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第5张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(4, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[5], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第6张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(5, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[6], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第7张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(6, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[7], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第8张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(7, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(img_id[8], new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "当前第9张，总共" + list.get(i).getImages().size() + "张", Toast.LENGTH_SHORT).show();
                OpenImage.with(mContext)
                        .setNoneClickView()
                        .setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                        .setImageUrlList(list.get(i).getImages(), MediaType.IMAGE)
                        .setClickPosition(8, i)//前者是点击所在数据位置，后者是点击的 RecyclerView 中位置
                        .setWechatExitFillInEffect(true)
                        .setShowDownload()
                        .show();
            }
        });

    }

    private void initCmtAdapter() {
        cmtAdapter = new CmtAdapter();
        cmtAdapter.addAll(comments);
        cmtAdapter.setAnimationEnable(true);
        cmtAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInLeft);
        commentRecycler.setAdapter(cmtAdapter);
    }

    private void getComment() {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String url = Localhost.url + "/comment/all?weiboId=" + weiboInfo.getWeiboId();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching comments", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    Type resultType = new TypeToken<Result<List<Comment>>>() {
                    }.getType();
                    Result<List<Comment>> result = gson.fromJson(json, resultType);

                    if (result.getCode() == 200) {
                        comments = result.getData();
                        // 处理评论数据，更新UI
                        runOnUiThread(() -> {
                            // 更新UI代码
                            initCmtAdapter();
                        });
                    } else {
                        Log.e(TAG, "Error: " + result.getMsg());
                    }
                } else {
                    Log.e(TAG, "Unexpected response: " + response);
                }
            }
        });
    }

    private void sendComment() {
        if (textComment == null) {
            Toast.makeText(mContext, "评论不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Comment comment=new Comment();
        comment.setUsername(sp.getString("username",null));
        comment.setAvatar(sp.getString("avatar",null));
        comment.setText(textComment);

        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        String url = Localhost.url + "/comment/add";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("weiboId", String.valueOf(weiboInfo.getWeiboId()));
        hashMap.put("text", textComment);
        String json = gson.toJson(hashMap);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(mContext, "发布评论失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG, "onResponse:comment "+json);
                if(json.contains("success")){
                    runOnUiThread(() -> {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        edtComment.setText(null);
                        comments.add(0,comment);
                        cmtAdapter.add(0,comment);
                        weiboInfo.setCommentCount(weiboInfo.getCommentCount()+1);
                        bqMultipyAdapter.set(0,weiboInfo);
                    });
                    EventBus.getDefault().post(new EventMessage("comment", Math.toIntExact(weiboInfo.getWeiboId())));
                }
                if(json.contains("评论含有屏蔽词")){
                    runOnUiThread(() -> {
                        Toast.makeText(mContext, "评论含有屏蔽词", Toast.LENGTH_SHORT).show();
                    });
                }


            }
        });
    }
}