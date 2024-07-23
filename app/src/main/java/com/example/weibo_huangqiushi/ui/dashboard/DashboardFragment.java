package com.example.weibo_huangqiushi.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.databinding.FragmentMineBinding;
import com.example.weibo_huangqiushi.ui.LoginActivity;
import com.example.weibo_huangqiushi.ui.ModifyActivity;
import com.example.weibo_huangqiushi.ui.data.Result;
import com.example.weibo_huangqiushi.ui.data.UserInfo;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;
import com.example.weibo_huangqiushi.ui.home.DetailActivity;
import com.example.weibo_huangqiushi.ui.home.adapter.BQMultipyAdapter;
import com.example.weibo_huangqiushi.until.Localhost;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardFragment extends Fragment {
    private RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
    private RecyclerView recyclerView;
    private List<WeiboInfo> list;
    private BQMultipyAdapter bqMultipyAdapter;
    private ImageView img_user;
    private TextView tx_user;
    private TextView tx_fans;
    private TextView tx_unlogin;
    private Button btn_exit;
    private Button btn_modify;
    private UserInfo userInfo;
    private SharedPreferences sp;
    private Handler handler;
    private Context mContext;
    private FragmentMineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentMineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mContext = getContext();

        initView(root);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 100) {
                    refresh();
                    savaInfo(userInfo);
                }
                if (message.what == 200) {
                    userInfo = null;
                    refresh();
                }
                if (message.what == 300) {
                    if (list != null) {
                        initAdapter();
                        bqMultipyAdapter.addAll(list);
                        recyclerView.setAdapter(bqMultipyAdapter);
                        //Toast.makeText(mContext, "已加载用户微博", Toast.LENGTH_SHORT).show();
                    }
                }
                if (message.what == 400) {
                    int i = (int) message.obj;
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    bqMultipyAdapter.removeAt(i);
                    list.remove(i);
                }
                if (message.what == 401) {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return root;
    }

    private void initView(View root) {
        sp = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        img_user = root.findViewById(R.id.login_img);
        tx_user = root.findViewById(R.id.mine_user_name);
        tx_fans = root.findViewById(R.id.mine_tx_islogin);
        tx_unlogin = root.findViewById(R.id.mine_tx_unlogin);
        btn_exit = root.findViewById(R.id.mine_btn);
        btn_modify=root.findViewById(R.id.mine_btn_modify);
        recyclerView = root.findViewById(R.id.mine_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        if (isLogin()) {
            if (mContext != null)
                Glide.with(mContext).load(userInfo.getAvatar()).into(img_user);
            getUserWeibo();
            tx_user.setText(userInfo.getUsername());
            tx_fans.setText("");
            tx_unlogin.setVisibility(View.GONE);
            btn_exit.setVisibility(View.VISIBLE);
            btn_modify.setVisibility(View.VISIBLE);
            img_user.setEnabled(false);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            img_user.setBackgroundResource(R.drawable.ellipse_1);
            tx_user.setText("请先登录");
            tx_fans.setText("点击头像去登录");
            tx_unlogin.setVisibility(View.VISIBLE);
            btn_exit.setVisibility(View.GONE);
            btn_modify.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo == null || !userInfo.getLoginStatus()) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getContext()).load(R.drawable.ellipse_1).into(img_user);
                img_user.setEnabled(true);
                userInfo = null;
                clearInfo();
                refresh();
                logout();
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ModifyActivity.class);
                intent.putExtra("avatar",userInfo.getAvatar());
                intent.putExtra("username",userInfo.getUsername());
                intent.putExtra("phone",userInfo.getPhone());
                startActivity(intent);
            }
        });
    }

    private void initAdapter() {
        bqMultipyAdapter = new BQMultipyAdapter(list, recyclerView);
        bqMultipyAdapter.setAnimationEnable(true);
        bqMultipyAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn);
        bqMultipyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<WeiboInfo>() {
            @Override
            public void onClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("weiboInfo", list.get(i));
                startActivity(intent);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                showDeleteConfirmationDialog(baseQuickAdapter.getItem(i).getWeiboId(),i);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "第" + (i + 1) + "条微博被点击", Toast.LENGTH_SHORT).show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close1, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {

                showDeleteConfirmationDialog(baseQuickAdapter.getItem(i).getWeiboId(),i);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment1, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "第" + (i + 1) + "条微博被点击", Toast.LENGTH_SHORT).show();
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_close2, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                showDeleteConfirmationDialog(baseQuickAdapter.getItem(i).getWeiboId(),i);
            }
        });
        bqMultipyAdapter.addOnItemChildClickListener(R.id.card_btn_comment2, new BaseQuickAdapter.OnItemChildClickListener<WeiboInfo>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<WeiboInfo, ?> baseQuickAdapter, @NonNull View view, int i) {
                Toast.makeText(mContext, "第" + (i + 1) + "条微博被点击", Toast.LENGTH_SHORT).show();
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

    private void refresh() {
        if (userInfo != null && userInfo.getLoginStatus() == true) {
            getUserWeibo();
            btn_exit.setVisibility(View.VISIBLE);
            btn_modify.setVisibility(View.VISIBLE);
            if (mContext != null)
                Glide.with(mContext).load(userInfo.getAvatar()).apply(requestOptions).into(img_user);
            tx_user.setText(userInfo.getUsername());
            tx_fans.setText("");
            tx_unlogin.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            img_user.setBackgroundResource(R.drawable.ellipse_1);
            tx_user.setText("请先登录");
            tx_fans.setText("点击头像去登录");
            btn_exit.setVisibility(View.GONE);
            btn_modify.setVisibility(View.GONE);
            img_user.setEnabled(true);
            tx_unlogin.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private Boolean isLogin() {
        getUserInfo();
        if (userInfo == null) return false;
        if (!userInfo.getLoginStatus()) {
            clearInfo();
            refresh();
        }
        return userInfo.getLoginStatus();
    }

    private void getUserInfo() {
        String token = sp.getString("token", null);
        if (token == null) return;
        String url = Localhost.url + "/user/id";

        Gson gson = new Gson();
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("okHttp in mine", "onFailure: ");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Message message = handler.obtainMessage();
                Log.d("okHttp", "onResponse: " + json);
                Type resultType = new TypeToken<Result<UserInfo>>() {
                }.getType();
                Result<UserInfo> result = gson.fromJson(json, resultType);
                if (json.contains("无效的token")) {
                    message.what = 200;
                    handler.sendMessage(message);
                    return;
                }
                if (result.getCode() == 200) {
                    userInfo = result.getData();
                    Log.d("TAG", "onResponseUser: " + userInfo.getId());
                    savaInfo(userInfo);
                    message.what = 100;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void getUserWeibo() {
        Gson gson = new Gson();
        Request request;
        String url = Localhost.url + "/user/weibo/id";
        String token = sp.getString("token", null);
        if (token == null) {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        } else {
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
                Log.d("okHttp in mine", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Type resultType = new TypeToken<Result<List<WeiboInfo>>>() {
                }.getType();
                Result<List<WeiboInfo>> result = gson.fromJson(json, resultType);

                if (result.getCode() == 200) {
                    List<WeiboInfo> weiboInfoList = result.getData();
                    list = weiboInfoList;
                    Message message = handler.obtainMessage();
                    message.what = 300;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void savaInfo(UserInfo userInfo) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("id", userInfo.getId());
        editor.putString("username", userInfo.getUsername());
        editor.putString("phone", userInfo.getPhone());
        editor.putString("avatar", userInfo.getAvatar());
        editor.putBoolean("loginStatus", userInfo.getLoginStatus());
        editor.apply();
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

    private void logout() {
        String token = sp.getString("token", null);
        if (token == null) return;
        String url = Localhost.url + "/login/logout";
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    private void showDeleteConfirmationDialog(Long id, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("再次确认");
        builder.setMessage("您确认要删除当前帖子吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the delete action
                deletePost(id,position);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deletePost(Long id,int position) {
        String token = sp.getString("token", null);
        if (token == null) {
            Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Localhost.url + "/user/weibo/id?weiboId=" + id;
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .delete()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Message message = handler.obtainMessage();
                message.what = 401;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d("TAG", "onResponseDel: +" + json);
                if (json.contains("success")) {
                    Message message = handler.obtainMessage();
                    message.what = 400;
                    message.obj = position;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume:123 ");
        isLogin();
        refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}