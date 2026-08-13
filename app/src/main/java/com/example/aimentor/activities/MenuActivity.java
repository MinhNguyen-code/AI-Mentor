package com.example.aimentor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
    // Variable to hold database connection for Database Inspector
    private android.database.sqlite.SQLiteDatabase inspectorDbConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.example.aimentor.utils.ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        // Keep DB connection open for Android Studio Database Inspector
        com.example.aimentor.databases.SqliteDbHelper dbHelper = new com.example.aimentor.databases.SqliteDbHelper(this);
        inspectorDbConnection = dbHelper.getReadableDatabase();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.drawerNavigation);
        menu = navigationView.getMenu();
        menuItemLogout = menu.findItem(R.id.logout_menu);

        // Display current username in the Drawer Menu
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String currentUsername = sharedPreferences.getString("USERNAME_USER", "User Profile");
        MenuItem accountMenuItem = menu.findItem(R.id.account_menu);
        if (accountMenuItem != null) {
            accountMenuItem.setTitle(currentUsername);
        }

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
                } else if (menuItem.getItemId() == R.id.Leaderboard_menu) {
                    viewPager.setCurrentItem(4); // Leaderboard
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
                } else if (position == 4) {
                    bottomNavigationView.getMenu().findItem(R.id.Leaderboard_menu).setChecked(true);
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
        } else if (menuItem.getItemId() == R.id.Leaderboard_menu) {
            viewPager.setCurrentItem(4); // Leaderboard
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

    // ===== NOTIFICATION SYSTEM =====

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            showNotificationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNotificationDialog() {
        if (isFinishing()) return;

        // Get current user id
        android.content.SharedPreferences sp = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int userId = sp.getInt("ID_USER", -1);
        if (userId == -1) {
            android.widget.Toast.makeText(this, "Please log in to view notifications.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Get stats for dynamic notifications
        com.example.aimentor.repository.ChatRepository chatRepo = new com.example.aimentor.repository.ChatRepository(this);
        com.example.aimentor.repository.StatsRepository statsRepo = new com.example.aimentor.repository.StatsRepository(this);

        int bookmarkCount = chatRepo.getBookmarkCount(userId);
        int totalQuizzes = statsRepo.getTotalQuizCount(userId);
        int questionCount = chatRepo.getQuestionCount(userId);
        int streakDays = 1 + (questionCount > 0 ? Math.min(6, questionCount / 2) : 0);

        // Inflate dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notifications, null);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
                
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Bind dialog views
        TextView tvNotifyBookmarks = dialogView.findViewById(R.id.tvNotifyBookmarks);
        TextView tvNotifyProgress = dialogView.findViewById(R.id.tvNotifyProgress);
        TextView tvNotifyStreak = dialogView.findViewById(R.id.tvNotifyStreak);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseNotifications);

        // Set dynamic texts
        if (bookmarkCount > 0) {
            tvNotifyBookmarks.setText("Review your bookmarked lessons: you have " + bookmarkCount + " saved insights waiting for you!");
        } else {
            tvNotifyBookmarks.setText("You haven't bookmarked any answers yet. Highlight helpful AI answers using the ⭐ icon!");
        }

        if (totalQuizzes > 0) {
            int accuracy = statsRepo.getOverallQuizAccuracy(userId);
            tvNotifyProgress.setText("Weekly Progress: You've completed " + totalQuizzes + " quizzes with an average accuracy of " + accuracy + "%.");
        } else {
            tvNotifyProgress.setText("Weekly Progress: Start taking AI-generated Kahoot quizzes from the Courses tab to track your progress!");
        }

        tvNotifyStreak.setText("Learning Streak: You are currently on a " + streakDays + "-day streak! Ask AI questions daily to maintain your momentum.");

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}

