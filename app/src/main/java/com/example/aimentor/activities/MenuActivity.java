package com.example.aimentor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aimentor.R;
import com.example.aimentor.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ViewPager2 viewPager;
    NavigationView navigationView;
    Menu menu;
    MenuItem menuItemLogout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.example.aimentor.utils.ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.drawerNavigation);
        menu = navigationView.getMenu();
        menuItemLogout = menu.findItem(R.id.logout_menu);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // click Item drawer menu
        navigationView.setNavigationItemSelectedListener(this);
        // call view pager
        setupViewPager();
        // click tab
        clickTabNavigation();
        // logout
        Logout();
    }
    private void Logout(){
        menuItemLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(GravityCompat.START); // close
                Intent login = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(login);
                finish();// khong cho back lai
                return false;
            }
        });
    }
    private void clickTabNavigation(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home_menu) {
                    viewPager.setCurrentItem(0); // Home - position : 0
                } else if (menuItem.getItemId() == R.id.Category_menu) {
                    viewPager.setCurrentItem(1); // Category
                } else if (menuItem.getItemId() == R.id.Quiz_menu) {
                    viewPager.setCurrentItem(2); // Quiz
                } else if (menuItem.getItemId() == R.id.Settings_menu) {
                    viewPager.setCurrentItem(3); // Settings
                }
                return true;
            }
        });
    }
    private void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0){
                    bottomNavigationView.getMenu().findItem(R.id.home_menu).setChecked(true);
                } else if (position == 1) {
                    bottomNavigationView.getMenu().findItem(R.id.Category_menu).setChecked(true);
                } else if (position == 2) {
                    bottomNavigationView.getMenu().findItem(R.id.Quiz_menu).setChecked(true);
                } else if (position == 3) {
                    bottomNavigationView.getMenu().findItem(R.id.Settings_menu).setChecked(true);
                } else {
                    bottomNavigationView.getMenu().findItem(R.id.home_menu).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.home_menu) {
            viewPager.setCurrentItem(0); // Home - position : 0
        } else if (menuItem.getItemId() == R.id.Category_menu) {
            viewPager.setCurrentItem(1); // Category
        } else if (menuItem.getItemId() == R.id.Quiz_menu) {
            viewPager.setCurrentItem(2); // Quiz
        } else if (menuItem.getItemId() == R.id.Settings_menu) {
            viewPager.setCurrentItem(3); // Settings
        } else {
            viewPager.setCurrentItem(0); // Home - position : 0
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectTab(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
}
