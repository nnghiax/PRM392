package com.example.prm392app.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvUniversity, tvRole;
    private Button btnEdit, btnChangePassword, btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvUniversity = view.findViewById(R.id.tv_university);
        tvRole = view.findViewById(R.id.tv_role);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        loadProfile();

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(), SignInActivity.class));
            getActivity().finish();
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
                    tvName.setText("Tên: " + user.getName());
                    tvEmail.setText("Email: " + user.getEmail());
                    tvUniversity.setText("Trường: " + user.getUniversity());
                    tvRole.setText("Vai trò: " + user.getRole());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không tải được hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText edtName = dialogView.findViewById(R.id.edt_name);
        EditText edtUniversity = dialogView.findViewById(R.id.edt_university);

        new AlertDialog.Builder(getContext())
                .setTitle("Chỉnh sửa hồ sơ")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newName = edtName.getText().toString().trim();
                    String newUniversity = edtUniversity.getText().toString().trim();

                    userRef.child(uid).child("name").setValue(newName);
                    userRef.child(uid).child("university").setValue(newUniversity);

                    loadProfile();
                })
                .setNegativeButton("Hủy", null)
                .show();
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
