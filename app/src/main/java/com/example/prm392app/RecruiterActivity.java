package com.example.prm392app;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.prm392app.databinding.ActivityRecruiterBinding;

public class RecruiterActivity extends AppCompatActivity {

    private ActivityRecruiterBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra trạng thái đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nếu chưa đăng nhập, chuyển đến SignInActivity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Khởi tạo navigation trực tiếp
        setupNavigation();
    }

    private void setupNavigation() {
        // Khởi tạo binding và set content view
        binding = ActivityRecruiterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập BottomNavigationView
        BottomNavigationView navView = binding.navView1;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home1, R.id.navigation_dashboard1, R.id.navigation_notifications1, R.id.navigation_chat1)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_recruiter);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        // Đặt navigation_home1 làm destination mặc định
        navController.navigate(R.id.navigation_home1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
