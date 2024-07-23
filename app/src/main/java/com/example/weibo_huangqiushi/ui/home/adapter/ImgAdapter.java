package com.example.weibo_huangqiushi.ui.home.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.example.weibo_huangqiushi.R;

import java.util.List;

public class ImgAdapter extends BaseQuickAdapter<Uri, QuickViewHolder> {
    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int i, @Nullable Uri uri) {
        Button button=holder.getView(R.id.btn_weiboItem_close);
        if(i==getItemCount()-1){
            button.setVisibility(View.GONE);
            Glide.with(holder.itemView)
                    .load(R.drawable.baseline_add_photo_alternate_24)
                    .into((ImageView) holder.getView(R.id.img_weiboItem_img));
        }else{
            button.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView)
                    .load(uri)
                    .into((ImageView) holder.getView(R.id.img_weiboItem_img));
        }
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.layout_weibo_item_img,viewGroup);
    }

    @Override
    protected int getItemCount(@NonNull List<? extends Uri> items) {
        return super.getItemCount(items)+1;
    }
}
