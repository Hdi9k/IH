package com.example.weibo_huangqiushi.ui.home.adapter;

import android.content.Context;
import android.util.Log;
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

import java.util.List;

public class BQAdapterImgs extends BaseQuickAdapter<WeiboInfo, QuickViewHolder> {
    private final static  int[] img_id= new int[]{R.id.card_img1,R.id.card_img2,R.id.card_img3,
                                                R.id.card_img4,R.id.card_img5,R.id.card_img6,
                                                R.id.card_img7,R.id.card_img8,R.id.card_img9};
    private RequestOptions requestOptions = new RequestOptions().transform(new CircleCrop());


    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int i, @Nullable WeiboInfo weiboInfo) {
        if(weiboInfo != null){
            holder.setText(R.id.card_tx_username1,weiboInfo.getUsername());
            holder.setText(R.id.card_tx_content1,weiboInfo.getTitle());
            Glide.with(holder.itemView)
                    .load(weiboInfo.getAvatar())
                    .placeholder(R.drawable.ellipse_1)
                    .apply(requestOptions)
                    .into((ImageView) holder.getView(R.id.card_img_user1));

            List<String> urlList=weiboInfo.getImages();
            for(int t=0;t<urlList.size();t++){
                if(urlList.get(t)!=null){
                    Glide.with(holder.itemView)
                            .load(urlList.get(t))
                            .into((ImageView) holder.getView(img_id[t]));
                }
            }
            if(urlList.size()<=9){
                for (int t=urlList.size();t<9;t++){
                    Log.d("TAG", "onBindViewHolder: null!!");
                    holder.setGone(img_id[t],true);
                }
            }
        }
    }

    @Override
    protected int getItemViewType(int position, @NonNull List<? extends WeiboInfo> list) {
        return super.getItemViewType(position, list);
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.layout_card_item_imgs,viewGroup);
    }
}
