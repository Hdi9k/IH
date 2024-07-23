package com.example.weibo_huangqiushi.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.example.weibo_huangqiushi.R;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;

public class BQAdapterBigImg extends BaseQuickAdapter<WeiboInfo, QuickViewHolder> {
    private RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());
    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int i, @Nullable WeiboInfo weiboInfo) {

        holder.setText(R.id.card_tx_username,weiboInfo.getUsername());
        holder.setText(R.id.card_tx_content,weiboInfo.getTitle());
        Glide.with(holder.itemView)
                .load(weiboInfo.getAvatar())
                .placeholder(R.drawable.ellipse_1)
                .apply(requestOptions)
                .into((ImageView) holder.getView(R.id.card_img_user));
        Glide.with(holder.itemView)
                .asBitmap()
                .load(weiboInfo.getImages().get(0))
                .into(new UniformScale(holder.getView(R.id.card_img_bigmode)));
    }


    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.layout_card_item_bigimg,viewGroup);
    }
}
