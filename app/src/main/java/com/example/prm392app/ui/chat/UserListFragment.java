package com.example.prm392app.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.User;
import com.example.prm392app.ui.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;

    private DatabaseReference dbRef;
    private String currentUserId;
    private String currentRole;

    private static final String TAG = "UserListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        recyclerView = view.findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, this::onUserClick);
        recyclerView.setAdapter(userAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        loadCurrentUserRole();

        return view;
    }

    private void loadCurrentUserRole() {
        dbRef.child(currentUserId).child("role").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentRole = snapshot.getValue(String.class);
                        if (currentRole != null) {
                            loadOtherUsers(currentRole.equals("student") ? "recruiter" : "student");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Lỗi lấy vai trò: " + error.getMessage());
                    }
                });
    }

    private void loadOtherUsers(String targetRole) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null
                            && !userSnap.getKey().equals(currentUserId)
                            && targetRole.equals(user.getRole())) {

                        user.setUid(userSnap.getKey());
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi tải danh sách người dùng: " + error.getMessage());
            }
        });
    }

    private void onUserClick(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("otherUserId", user.getUid());
        bundle.putString("otherUserName", user.getDisplayName());

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userList_to_chat, bundle);
    }
}
