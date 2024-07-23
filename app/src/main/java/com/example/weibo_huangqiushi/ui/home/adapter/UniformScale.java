package com.example.weibo_huangqiushi.ui.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

public class UniformScale extends ImageViewTarget<Bitmap> {
    private final static String TAG="UniformScale";
    private ImageView target;
    private int width;
    private int height;

    public UniformScale(ImageView view) {
        super(view);
        this.target=view;
    }

    @Override
    protected void setResource(@Nullable Bitmap resource) {
        if(resource==null){
            Log.w(TAG, "setResource: null");
            return;
        }
        view.setImageBitmap(resource);
        width=resource.getWidth();
        height=resource.getHeight();
        Log.d(TAG, width+"setResource: "+height);
        //ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(target.getLayoutParams());

    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        super.onResourceReady(resource, transition);
        ViewGroup.LayoutParams params=target.getLayoutParams();
        Log.d(TAG, params.width+"onResourceReady1: "+params.height);
        if(width>height){
            Log.d(TAG, "setResource: wider");
            params.height=180*4;
            params.width=320*4;
            target.setLayoutParams(params);
        }else {
            Log.d(TAG, "setResource: higher");
            params.height=320*4;
            params.width=180*4;
            target.setLayoutParams(params);
        }
        Log.d(TAG, params.width+"onResourceReady2: "+params.height);
    }
}
