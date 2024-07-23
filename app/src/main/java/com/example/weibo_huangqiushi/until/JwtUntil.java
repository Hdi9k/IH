package com.example.weibo_huangqiushi.until;

import android.util.Log;

public class JwtUntil {
    public static boolean isTokenValid(String json){
        if(json.contains("令牌已过期")||json.contains("令牌为空")||json.contains("令牌解析失败")){
            Log.d("JWT", "令牌有误");
            return false;
        }
        return true;
    }
}
