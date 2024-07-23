package com.example.weibo_huangqiushi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weibo_huangqiushi.MainActivity;
import com.example.weibo_huangqiushi.R;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private View view_custom;
    private TextView dialog_tx1;
    private TextView dialog_tx2;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mContext = SplashActivity.this;
        sp = getSharedPreferences("data", MODE_PRIVATE);

        //test();
        if (!isAgree()) {
            initDialog();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {//模拟加载时间
                        Log.d("TAG", "run: ");
                        Thread.sleep(1000);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

    }
    //测试dialog用
    private void test(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isAgree", false);
        editor.apply();
    }

    private Boolean isAgree() {
        return sp.getBoolean("isAgree", false);
    }

    private void initDialog() {
        builder = new AlertDialog.Builder(SplashActivity.this);
        final LayoutInflater inflater = SplashActivity.this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.layout_dialog, null, false);
        builder.setView(view_custom);
        alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_tx1 = view_custom.findViewById(R.id.dialog_title);
        dialog_tx2 = view_custom.findViewById(R.id.dialog_content);
        dialog_tx1.setText("声明与条款");
        //dialog_tx2.setText("内容说明");
        setClickText();
        alertDialog.setCancelable(false);
        alertDialog.show();
        window.setLayout((int) getResources().getDisplayMetrics().density * 320, (int) getResources().getDisplayMetrics().density * 280);

        view_custom.findViewById(R.id.dialog_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });
        view_custom.findViewById(R.id.dialog_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isAgree", true);
                editor.apply();
                alertDialog.dismiss();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
    private void setClickText(){
        SpannableString spannableString=new SpannableString(getString(R.string.ih_privacy));

        ClickableSpan clickableSpan1=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                alertDialog.dismiss();
                pivicyDialog(2);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF0D84FF);
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan clickableSpan2=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                alertDialog.dismiss();
                pivicyDialog(1);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF0D84FF);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan1,46,52, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2,53,59, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dialog_tx2.setMovementMethod(LinkMovementMethod.getInstance());
        dialog_tx2.setText(spannableString);
    }
    private void pivicyDialog(int type){
        AlertDialog.Builder builder1=new AlertDialog.Builder(mContext);
        if(type==1){
            builder1.setTitle("隐私政策");
            builder1.setMessage(getString(R.string.privacy_content));

        } else if(type==2){
            builder1.setTitle("用户协议");
            builder1.setMessage(getString(R.string.user_content));
        }
        builder1.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                alertDialog.show();
            }
        });
        builder1.create().show();
    }
}