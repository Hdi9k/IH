package com.example.weibo_huangqiushi.ui.home.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;
import com.example.weibo_huangqiushi.ui.home.EventMessage;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JzvdStd;

public class BQMultipyAdapter extends BaseQuickAdapter<WeiboInfo, QuickViewHolder> {
    public static final String TAG = "BQMultipyAdapter";
    private int TYPE = 0;
    private RecyclerView recyclerView;
    private List<WeiboInfo> mlist = new ArrayList<>();
    private List<String> big_img = new ArrayList<>();
    private List<List<String>> imgs = new ArrayList<>();
    List<String> playerList = new ArrayList<>();
    private RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());

    private final static int[] img_id = new int[]{R.id.card_img1, R.id.card_img2, R.id.card_img3,
            R.id.card_img4, R.id.card_img5, R.id.card_img6,
            R.id.card_img7, R.id.card_img8, R.id.card_img9};

    public BQMultipyAdapter(List<WeiboInfo> mlist, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.mlist = mlist;
        for (WeiboInfo info : mlist) {
            if (info.getVideoUrl() != null && info.getPoster() != null) {
                playerList.add(info.getVideoUrl());
            }
            if (info.getImages() != null && info.getImageCount() > 1) {
                imgs.add(info.getImages());
            }
            if (info.getImages() != null && info.getImageCount() == 1) {
                big_img.add(info.getImages().get(0));
            }
        }
    }

    @Override
    protected int getItemViewType(int position, @NonNull List<? extends WeiboInfo> list) {
        WeiboInfo info = list.get(position);
        if (info.getVideoUrl() != null && info.getPoster() != null)
            return 4;
        else if (info.getImages() != null && info.getImageCount() > 1)
            return 3;
        else if (info.getImages() != null && info.getImageCount() == 1)
            return 2;
        else if (info.getImageCount() == 0)
            return 1;
        else
            return 0;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, @SuppressLint("RecyclerView") int i, @Nullable WeiboInfo weiboInfo) {
        TYPE = holder.getItemViewType();
        if (weiboInfo != null) {

            if (TYPE == 2) {
                holder.setText(R.id.card_tx_username, weiboInfo.getUsername());
                holder.setText(R.id.card_tx_content, weiboInfo.getTitle());
                if(weiboInfo.getCommentCount()!=0){
                    holder.setText(R.id.card_btn_comment,weiboInfo.getCommentCount().toString());
                }
                Glide.with(holder.itemView)
                        .load(weiboInfo.getAvatar())
                        .placeholder(R.drawable.ellipse_1)
                        .apply(requestOptions)
                        .into((ImageView) holder.getView(R.id.card_img_user));
                Glide.with(holder.itemView)
                        .asBitmap()
                        .load(weiboInfo.getImages().get(0))
                        .into(new UniformScale(holder.getView(R.id.card_img_bigmode)));

                ImageView img_like = holder.getView(R.id.card_heart1);
                Button button = holder.getView(R.id.card_btn_like);
                setLike(button, img_like, weiboInfo);

                ImageView image_big = holder.getView(R.id.card_img_bigmode);
                image_big.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "当前第1张，总共1张", Toast.LENGTH_SHORT).show();
                        OpenImage.with(holder.itemView.getContext())
                                .setClickRecyclerView((RecyclerView) holder.itemView.getParent(), new SourceImageViewIdGet<OpenImageUrl>() {
                                    @Override
                                    public int getImageViewId(OpenImageUrl openImageUrl, int i) {
                                        return R.id.card_img_bigmode;
                                    }
                                }).setSrcImageViewScaleType(ImageView.ScaleType.FIT_CENTER, true)
                                .setImageUrl(weiboInfo.getImages().get(0), MediaType.IMAGE)
                                .setClickPosition(0, i)//前者是点击所在 allShowData 数据位置，后者是点击的 RecyclerView 中位置
                                .setWechatExitFillInEffect(true)
                                .setShowDownload()
                                .show();
                        //openImage = setImgClick(holder, holder.getAdapterPosition());
                    }
                });

                if(weiboInfo.getCategory()==7){
                    holder.setVisible(R.id.card_tx_ad1,true);
                }

            }
            if (TYPE == 3 || TYPE == 1) {
                holder.setText(R.id.card_tx_username1, weiboInfo.getUsername());
                holder.setText(R.id.card_tx_content1, weiboInfo.getTitle());
                if(weiboInfo.getCommentCount()!=0){
                    holder.setText(R.id.card_btn_comment1,weiboInfo.getCommentCount().toString());
                }
                Glide.with(holder.itemView)
                        .load(weiboInfo.getAvatar())
                        .placeholder(R.drawable.ellipse_1)
                        .apply(requestOptions)
                        .into((ImageView) holder.getView(R.id.card_img_user1));

                ImageView img_like = holder.getView(R.id.card_heart2);
                Button button = holder.getView(R.id.card_btn_like1);
                setLike(button, img_like, weiboInfo);

                List<String> urlList = weiboInfo.getImages();
                if (urlList != null) {
                    for (int t = 0; t < urlList.size(); t++) {
                        if (urlList.get(t) != null) {
                            Glide.with(holder.itemView)
                                    .load(urlList.get(t))
                                    .into((ImageView) holder.getView(img_id[t]));
                        }
                    }
                    if (urlList.size() <= 9) {
                        for (int t = urlList.size(); t < 9; t++) {
                            holder.setGone(img_id[t], true);
                        }
                    }
                } else {
                    for (int t = 0; t < 9; t++) {
                        holder.setGone(img_id[t], true);
                    }
                }
                if(weiboInfo.getCategory()==7){
                    holder.setVisible(R.id.card_tx_ad2,true);
                }
            }
            if (TYPE == 4) {
                Log.d("TAG", "checkId: " + R.id.card_tx_username2 + weiboInfo.getUsername());
                holder.setText(R.id.card_tx_username2, weiboInfo.getUsername());
                holder.setText(R.id.card_tx_content2, weiboInfo.getTitle());
                if(weiboInfo.getCommentCount()!=0){
                    holder.setText(R.id.card_btn_comment2,weiboInfo.getCommentCount().toString());
                }
                Glide.with(holder.itemView)
                        .load(weiboInfo.getAvatar())
                        .placeholder(R.drawable.ellipse_1)
                        .apply(requestOptions)
                        .into((ImageView) holder.getView(R.id.card_img_user2));

                ImageView img_like = holder.getView(R.id.card_heart3);
                Button button = holder.getView(R.id.card_btn_like2);
                setLike(button, img_like, weiboInfo);

                Glide.with(holder.itemView)
                        .load(weiboInfo.getPoster())
                        .placeholder(R.drawable.ellipse_1)
                        .into((ImageView) holder.getView(R.id.card_img_cover));

                JzvdStd jzvdStd = holder.getView(R.id.card_video);
                jzvdStd.setUp(weiboInfo.getVideoUrl(), weiboInfo.getTitle());
                jzvdStd.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        Log.d(TAG, "onScrollChange: changin");
                        Rect rect = new Rect();
                        jzvdStd.getLocalVisibleRect(rect);
                        if (rect.top >= recyclerView.getHeight() || rect.bottom <= 0) {
                            // 暂停视频播放
                            Log.d(TAG, "onScrollChange: out");
                        }
                    }
                });

                ImageView play = holder.getView(R.id.card_btn_play);
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jzvdStd.startVideo();
                        Glide.with(holder.itemView).clear((ImageView) holder.getView(R.id.card_img_cover));
                        holder.setGone(R.id.card_btn_play, true);
                    }
                });
                if(weiboInfo.getCategory()==7){
                    holder.setVisible(R.id.card_tx_ad1,true);
                }
            } else {
                Log.d("TAG", "onBindViewHolder: error");
            }
        }
    }

    private void postLike(Boolean type, int id) {
        EventBus.getDefault().post(new EventMessage(type, id));
    }

    private void setLike(Button button, ImageView img_like, WeiboInfo weiboInfo) {
        if (weiboInfo.getLikeCount() != 0) {
            button.setText("" + weiboInfo.getLikeCount());
        }
        if (weiboInfo.getLikeFlag()) {
            button.setTextColor(0xFFEA512F);
            img_like.setImageResource(R.drawable.card_red_heart);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!weiboInfo.getLikeFlag()) {
                    postLike(true, Integer.parseInt(weiboInfo.getWeiboId().toString()));
                    startAnimation(img_like, button);
                    weiboInfo.setLikeFlag(true);
                    button.setText("" + (weiboInfo.getLikeCount() + 1));
                } else {
                    postLike(false, Integer.parseInt(weiboInfo.getWeiboId().toString()));
                    cancelAnimation(img_like, button);
                    weiboInfo.setLikeFlag(false);
                    button.setText("" + (weiboInfo.getLikeCount() - 1));
                }
            }
        });
    }

    private void startAnimation(ImageView imageView, Button button) {
        imageView.setImageResource(R.drawable.card_red_heart);
        button.setTextColor(0xFFEA512F);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1.0f, 1.2f, 1.0f);
        // 旋转动画，沿Y轴旋转360度
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION_Y, 0f, 360f);
        // 设置动画持续时间为1000ms
        scaleXAnimator.setDuration(1000);
        scaleYAnimator.setDuration(1000);
        rotationAnimator.setDuration(1000);

        // 创建动画集合，并添加缩放和旋转动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, rotationAnimator);

        // 启动动画
        animatorSet.start();
    }

    private void cancelAnimation(ImageView imageView, Button button) {
        button.setTextColor(0xFF000000);
        imageView.setImageResource(R.drawable.card_heart);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1.0f, 0.8f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1.0f, 0.8f, 1.0f);

        // 设置动画持续时间为1000ms
        scaleXAnimator.setDuration(1000);
        scaleYAnimator.setDuration(1000);

        // 创建动画集合，并添加缩放动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);

        // 启动动画
        animatorSet.start();
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        Log.d("TAG", "onCreateViewHolder1: " + TYPE);
        if (i == 1) return new QuickViewHolder(R.layout.layout_card_item_imgs, viewGroup);

        if (i == 2) return new QuickViewHolder(R.layout.layout_card_item_bigimg, viewGroup);

        if (i == 3) return new QuickViewHolder(R.layout.layout_card_item_imgs, viewGroup);

        if (i == 4) return new QuickViewHolder(R.layout.layout_card_item_video, viewGroup);
        //else return new QuickViewHolder(R.layout.layout_card_item_imgs,viewGroup);
        Log.d("TAG", "onCreateViewHolder2: " + i);
        return null;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);


    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}
