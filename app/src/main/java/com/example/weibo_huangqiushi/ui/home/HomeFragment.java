package com.example.weibo_huangqiushi.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.databinding.FragmentHomeBinding;
import com.example.weibo_huangqiushi.ui.data.PageBean;
import com.example.weibo_huangqiushi.ui.data.Result;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;
import com.example.weibo_huangqiushi.ui.home.adapter.BQMultipyAdapter;
import com.example.weibo_huangqiushi.until.JwtUntil;
import com.example.weibo_huangqiushi.until.Localhost;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import kotlin.ranges.IntRange;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private final static String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private BQMultipyAdapter bqMultipyAdapter;
    private final static int pageSize = 10;
    private Long currentPage = 1L;
    private Long totalPage = 2L;
    private SharedPreferences sp;
    private RefreshLayout refreshLayout;
    private Context mContext;
    private Handler handler;
    private List<WeiboInfo> list = new ArrayList<>();
    private View rootView;//缓存Fragment view
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if ( rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_home, container,false);
        } else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mContext = getContext();


        initView(root);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 100) {//初始化
                    if (list != null) {
                        initAdapter();
                        bqMultipyAdapter.addAll(list);
                        recyclerView.setAdapter(bqMultipyAdapter);
                        //Toast.makeText(mContext, "数据已加载", Toast.LENGTH_SHORT).show();
                    }
                }
                if (message.what == 200) {//刷新
                    //Collections.shuffle(list);
                    initAdapter();
                    bqMultipyAdapter.addAll(list);
                    recyclerView.setAdapter(bqMultipyAdapter);
                    Toast.makeText(mContext, "数据已刷新", Toast.LENGTH_SHORT).show();
                    refreshLayout.setEnableLoadMore(true);
                    currentPage = 1L;
                }
                if (message.what == 300) {//加载更多
                    if (list != null) {
                        for (int i = (int) ((currentPage - 1) * pageSize); i < list.size(); i++) {
                            bqMultipyAdapter.add(list.get(i));
                        }
                        //recyclerView.setAdapter(bqMultipyAdapter);
                        refreshLayout.finishLoadMore();
                        Toast.makeText(mContext, "已下拉加载", Toast.LENGTH_SHORT).show();
                    }
                }
                if (message.what == 400) {//上拉时页数增加
                    if (currentPage < totalPage) {
                        currentPage++;
                    }
                }
                if (message.what == 500) {//点赞
                    //Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 501) {
                    //Toast.makeText(mContext, "已经点过赞了", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 502) {
                    //Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 600) {//取消点赞
                    //Toast.makeText(mContext, "取消点赞成功", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 602) {
                    //Toast.makeText(mContext, "取消点赞失败", Toast.LENGTH_SHORT).show();
                }
                if (message.what == 700) {
                    clearInfo();
                    getWeiboInfo(1);
                }

                return false;
            }
        });
        return root;
    }

    private void initView(View root) {
        sp = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        recyclerView = root.findViewById(R.id.home_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout = root.findViewById(R.id.smart_refresh);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list = new ArrayList<>();
                getWeiboInfo(2);
                refreshLayout.finishRefresh(500);
                send(200);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (currentPage < totalPage) {
                    currentPage++;
                    getWeiboInfo(3);
                } else {
                    Toast.makeText(mContext, "已经到底啦", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishLoadMore(100);
                    refreshLayout.setEnableLoadMore(false);
                }
            }
        });

        getWeiboInfo(1);

    }

    private void initAdapter() {
        bqMultipyAdapter = new BQMultipyAdapter(list, recyclerView);
        bqMultipyAdapter.setAnimationEnable(true);
        bqMultipyAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn);
        bqMultipyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<WeiboInfo>() {
            @Override
            public void onClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                postUserLike(baseQuickAdapter.getItem(i).getCategory());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("weiboInfo", list.get(i));
                startActivity(intent);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "关闭" + (i + 1), Toast.LENGTH_SHORT).show();
                bqMultipyAdapter.removeAt(i);
                list.remove(i);
                recyclerView.setAdapter(bqMultipyAdapter);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                postUserLike(baseQuickAdapter.getItem(i).getCategory());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("weiboInfo", list.get(i));
                intent.putExtra("clickComment",true);
                startActivity(intent);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close1, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "关闭" + (i + 1), Toast.LENGTH_SHORT).show();
                bqMultipyAdapter.removeAt(i);
                list.remove(i);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment1, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                postUserLike(baseQuickAdapter.getItem(i).getCategory());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("weiboInfo", list.get(i));
                intent.putExtra("clickComment",true);
                startActivity(intent);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close2, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "关闭" + (i + 1), Toast.LENGTH_SHORT).show();
                bqMultipyAdapter.removeAt(i);
                list.remove(i);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment2, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                postUserLike(baseQuickAdapter.getItem(i).getCategory());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("weiboInfo", list.get(i));
                intent.putExtra("clickComment",true);
                startActivity(intent);
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

    //type=1 初始化;type=2 刷新;type=3 添加
    private void getWeiboInfo(int type) {
        Gson gson = new Gson();
        Request request;
        String url = Localhost.url + "/info/homePage?page=" + currentPage + "&pageSize=" + pageSize;

        String token = sp.getString("token", null);
        if (token == null) {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        } else {
            //token = "Bearer " + token;
            request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .get()
                    .build();
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("okHttp in home", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Type resultType = new TypeToken<Result<PageBean<WeiboInfo>>>() {
                }.getType();
                Result<PageBean<WeiboInfo>> result = gson.fromJson(json, resultType);
                Log.d(TAG, "onResponse: json" + json);
                if (!JwtUntil.isTokenValid(json)) {
                    send(700);
                    return;
                }
                if (result.getCode() == 200) {
                    PageBean<WeiboInfo> pageBean = result.getData();
                    List<WeiboInfo> weiboInfoList = pageBean.getRows();
                    //list.addAll(weiboInfoList);
                    list = weiboInfoList;
                    totalPage = pageBean.getTotal() / pageSize + 1;
                    Log.d(TAG, "onResponselogin: totalPage" + totalPage);
                    Log.d(TAG, "onResponselogin: " + weiboInfoList);
                }
                if (type == 1) send(100);
                if (type == 2) send(200);
                if (type == 3) send(300);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage msg) {
        Log.d(TAG, "onMessageEvent: ");
        if(msg.flag!=null&& msg.message == null){
            if (msg.flag) {
                postlike(msg.id);
            } else  {
                postcancel(msg.id);
            }
        }

        if (Objects.equals(msg.message, "comment")) {
            List<WeiboInfo> infos = bqMultipyAdapter.getItems();
            for (int i = 0; i < infos.size(); i++) {
                WeiboInfo info = infos.get(i);
                Log.d(TAG, "onMessageEvent: start change"+info.getWeiboId());
                if (info.getWeiboId() == msg.id) {
                    Log.d(TAG, "onMessageEvent: changed"+info.getWeiboId());
                    info.setCommentCount(info.getCommentCount() + 1);
                    bqMultipyAdapter.set(i, info);
                    break;
                }
            }
        }
    }

    private void postlike(int id) {
        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Localhost.url + "/like/up?weiboId=" + id;
        //token = "Bearer " + token;
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                send(502);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG, "onResponselike: +" + json);
                if (json.contains("true")) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getWeiboId() == id) {
                            list.get(i).setLikeFlag(true);
                            list.get(i).addLikeCount();
                            break;
                        }
                    }
                    send(500);
                } else if (json.contains("不能重复点赞")) {
                    send(501);
                } else {
                    send(502);
                }

            }
        });
    }

    private void postcancel(int id) {
        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Localhost.url + "/like/cancel?weiboId=" + id;
        //token = "Bearer " + token;

        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                send(602);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG, "onResponselike: +" + json);
                if (json.contains("true")) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getWeiboId() == id) {
                            list.get(i).setLikeFlag(false);
                            list.get(i).decLikeCount();
                            break;
                        }
                    }
                    send(600);
                } else {
                    send(602);
                }

            }
        });
    }

    private void postUserLike(int category){
        String token = sp.getString("token", null);
        if (token == null) {
            return;
        }
        String url = Localhost.url + "/info/interest?category=" + category;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: 兴趣点击");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json=response.body().string();
                if(json.contains("success")){
                    Log.d(TAG, "success to post interest of category"+category);
                }
            }
        });
    }

    private void send(int what) {
        Message message = handler.obtainMessage();
        message.what = what;
        handler.sendMessage(message);
    }

    private void clearInfo() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("id");
        editor.remove("username");
        editor.remove("phone");
        editor.remove("avatar");
        editor.remove("loginStatus");
        editor.remove("token");
        editor.putBoolean("isAgree", false);
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "EventBus111 registered");
        if (!EventBus.getDefault().isRegistered(HomeFragment.this)) {
            EventBus.getDefault().register(HomeFragment.this);
            Log.d(TAG, "EventBus registered");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(HomeFragment.this)) {
            EventBus.getDefault().unregister(HomeFragment.this);
            Log.d(TAG, "EventBus unregistered");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}