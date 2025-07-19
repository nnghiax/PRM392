package com.example.prm392app.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import com.example.prm392app.ui.adapter.ApplicationAdapter;
import com.example.prm392app.ui.interview.ScheduleInterviewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApplicationHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;
    private List<Application> appList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerApplications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadApplications();
        return view;
    }

    private void loadApplications() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("applications").whereEqualTo("studentId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    appList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Application app = doc.toObject(Application.class);
                        if (app != null) {
                            app.setApplicationId(doc.getId()); // Đảm bảo ID được gán đúng
                            appList.add(app);
                        }
                    }

                    adapter = new ApplicationAdapter(appList, getContext(), new ApplicationAdapter.OnItemActionListener() {
                        @Override
                        public void onWithdrawClick(Application app) {
                            db.collection("applications").document(app.getApplicationId())
                                    .delete()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(getContext(), "Application withdrawn", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onScheduleClick(Application app) {
                            Intent i = new Intent(getContext(), ScheduleInterviewActivity.class);
                            i.putExtra("applicationId", app.getApplicationId());
                            startActivity(i);
                        }
                    });

                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load applications", Toast.LENGTH_SHORT).show()
                );
    }
}
