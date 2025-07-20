package com.example.prm392app.uiRecruiters.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
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

public class UserListRecruiterFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;
    private String currentUserId;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list_recruiter, container, false);

        recyclerView = view.findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(userList, this::onUserClick);
        recyclerView.setAdapter(userAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        loadStudentUsers();

        return view;
    }

    private void loadStudentUsers() {
        dbRef.orderByChild("role").equalTo("student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            User user = userSnap.getValue(User.class);
                            if (user != null && !userSnap.getKey().equals(currentUserId)) {
                                user.setUid(userSnap.getKey());
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserListRecruiter", "Lỗi tải student: " + error.getMessage());
                    }
                });
    }

    private void onUserClick(User user) {
        Bundle args = new Bundle();
        args.putString("otherUserId", user.getUid());
        args.putString("otherUserName", user.getName() != null ? user.getName() : user.getEmail());

        // ✅ Dùng Navigation Component
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userListRecruiter_to_chatRecruiter, args);
    }
}
