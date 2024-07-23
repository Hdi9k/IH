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
import com.example.weibo_huangqiushi.ui.data.Comment;
import com.example.weibo_huangqiushi.ui.data.WeiboInfo;

public class CmtAdapter extends BaseQuickAdapter<Comment, QuickViewHolder> {
    private RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());


    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int i, @Nullable Comment comment) {
        if(comment!=null){
            holder.setText(R.id.cmt_tx_username,comment.getUsername());
            holder.setText(R.id.cmt_tx_comment,comment.getText());
            Glide.with(holder.itemView)
                    .load(comment.getAvatar())
                    .placeholder(R.drawable.ellipse_1)
                    .apply(requestOptions)
                    .into((ImageView) holder.getView(R.id.cmt_img_user));
        }
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.layout_item_comment,viewGroup);
    }
}
