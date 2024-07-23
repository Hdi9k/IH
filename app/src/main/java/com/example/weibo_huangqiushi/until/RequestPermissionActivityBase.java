package com.example.weibo_huangqiushi.until;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class RequestPermissionActivityBase extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    //所需权限
    public String[] getNeedPermissions(){
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
        };
    }
    public static final int PERMISSION_REQUEST_CODE=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> lst= new ArrayList<String>();
        String[] permissions=getNeedPermissions();
        for(String p:permissions)
            if(ContextCompat.checkSelfPermission(this,p)!= PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,p)){
                lst.add(p);
            }
        int size=lst.size();
        if(size>0){
            ActivityCompat.requestPermissions(this,lst.toArray(new String[size]),PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.e("权限error","APP申请权限不全");
                    return;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

