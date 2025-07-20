package com.example.prm392app;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.prm392app.databinding.ActivityRecruiterBinding; // Sửa import

public class RecruiterActivity extends AppCompatActivity {

    private ActivityRecruiterBinding binding; // Sửa tên binding
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Kiểm tra trạng thái đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nếu chưa đăng nhập, chuyển đến SignInActivity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setupNavigation();

    }

    private void setupNavigation() {
        // Khởi tạo binding và set content view
        binding = ActivityRecruiterBinding.inflate(getLayoutInflater()); // Sửa tên binding
        setContentView(binding.getRoot());

        // Thiết lập BottomNavigationView
        BottomNavigationView navView = binding.navView1; // Sử dụng ID nav_view1
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home1, R.id.navigation_dashboard1, R.id.navigation_notifications1, R.id.navigation_chat1)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_recruiter);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    private void checkUserRole(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("recruiter".equals(role)) {
                            // Nếu là recruiter, khởi tạo navigation
                            setupNavigation();
                            // Đặt navigation_home1 làm destination mặc định
                            navController.navigate(R.id.navigation_home1);
                        } else {
                            // Nếu không phải recruiter, chuyển về MainActivity (dành cho student)
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Nếu không tìm thấy tài liệu, chuyển về MainActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi, chuyển về MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
}