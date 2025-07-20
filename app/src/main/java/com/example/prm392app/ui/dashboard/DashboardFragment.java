
        package com.example.prm392app.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import com.example.prm392app.ui.adapter.ApplicationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private RecyclerView recyclerView;
    private ApplicationAdapter applicationAdapter;
    private List<Application> applicationList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view_applications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        applicationList = new ArrayList<>();
        applicationAdapter = new ApplicationAdapter(applicationList, getContext(), db);
        recyclerView.setAdapter(applicationAdapter);

        // Tải danh sách ứng dụng từ Firestore
        loadApplications();

        return root;
    }

    private void loadApplications() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No authenticated user");
            return;
        }
        String studentId = currentUser.getUid();

        db.collection("applications")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Application application = document.toObject(Application.class);
                        applicationList.add(application);
                    }
                    applicationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading applications", e);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
