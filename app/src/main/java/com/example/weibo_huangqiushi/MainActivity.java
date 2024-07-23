package com.example.weibo_huangqiushi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.weibo_huangqiushi.ui.UploadActivity;
import com.example.weibo_huangqiushi.ui.dashboard.DashboardFragment;
import com.example.weibo_huangqiushi.ui.home.DetailActivity;
import com.example.weibo_huangqiushi.ui.home.HomeFragment;
import com.example.weibo_huangqiushi.until.RequestPermissionActivityBase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weibo_huangqiushi.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends RequestPermissionActivityBase {

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private Fragment homeFragment;
    private Fragment dashboardFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();

        // 使用 findFragmentByTag 查找现有的 Fragment 实例，避免重复创建
        homeFragment = fragmentManager.findFragmentByTag("1");
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.nav_host_fragment_activity_main, homeFragment, "1")
                    .commit();
        }
        dashboardFragment = fragmentManager.findFragmentByTag("2");
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.nav_host_fragment_activity_main, dashboardFragment, "2")
                    .hide(dashboardFragment)
                    .commit();
        }

        activeFragment = homeFragment;


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.navigation_home){
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                    activeFragment = homeFragment;
                    return true;
                } else if (item.getItemId()==R.id.navigation_dashboard) {
                    fragmentManager.beginTransaction().hide(activeFragment).show(dashboardFragment).commit();
                    activeFragment = dashboardFragment;
                    return true;
                }
                return false;
            }
        });

        Button button = findViewById(R.id.btn_main_weibo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

    }

}
