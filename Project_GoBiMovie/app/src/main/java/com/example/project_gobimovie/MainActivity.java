package com.example.project_gobimovie;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bot_nav);

        // Đặt fragment mặc định khi ứng dụng khởi động (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.mnu_Home);
        }

        // Xử lý sự kiện khi nhấn vào các mục trong BottomNavigationView
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;

                // Sử dụng if-else thay vì switch
                if (item.getItemId() == R.id.mnu_Home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.mnu_Genre) {
                    selectedFragment = new GenreFragment();
                } else if (item.getItemId() == R.id.mnu_WatchList) {
                    selectedFragment = new WatchListFragment();
                } else if (item.getItemId() == R.id.mnu_MyProfile) {
                    selectedFragment = new ProfileFragment();
                } else {
                    return false;
                }

                loadFragment(selectedFragment);
                return true;
            }
        });

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm loadFragment để thay thế fragment trong FragmentContainerView
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}