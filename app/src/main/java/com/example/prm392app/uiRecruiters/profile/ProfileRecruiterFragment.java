package com.example.prm392app.uiRecruiters.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prm392app.R;
import com.example.prm392app.model.User;
import com.example.prm392app.SignInActivity;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class ProfileRecruiterFragment extends Fragment {

    private TextView tvCompany, tvEmail, tvUniversity, tvRole;
    private Button btnLogout, btnChangePassword;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_recruiter, container, false);

        // Ánh xạ các view
        tvCompany = view.findViewById(R.id.tv_company);
        tvEmail = view.findViewById(R.id.tv_email);
        tvRole = view.findViewById(R.id.tv_role);

        btnLogout = view.findViewById(R.id.btn_logout);
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return view;
        }

        uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        loadProfile();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(), SignInActivity.class));
            requireActivity().finish();
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        return view;
    }

    private void loadProfile() {
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    tvCompany.setText("Công ty: " + safe(user.getCompany()));
                    tvEmail.setText("Email: " + safe(user.getEmail()));
                    tvRole.setText("Vai trò: " + safe(user.getRole()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(String s) {
        return s != null ? s : "";
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        EditText edtOldPass = dialogView.findViewById(R.id.edt_old_password);
        EditText edtNewPass = dialogView.findViewById(R.id.edt_new_password);

        new AlertDialog.Builder(getContext())
                .setTitle("Đổi mật khẩu")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String oldPass = edtOldPass.getText().toString();
                    String newPass = edtNewPass.getText().toString();
                    FirebaseUser user = mAuth.getCurrentUser();

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                    user.reauthenticate(credential)
                            .addOnSuccessListener(unused -> user.updatePassword(newPass)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Sai mật khẩu cũ", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
