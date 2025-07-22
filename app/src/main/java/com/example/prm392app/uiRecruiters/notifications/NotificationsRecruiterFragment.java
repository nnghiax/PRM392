package com.example.prm392app.uiRecruiters.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.uiRecruiters.adapter.ApplicationRecruiterAdapter;
import com.example.prm392app.model.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsRecruiterFragment extends Fragment {

    private static final String TAG = "NotificationsRecruiter";
    private RecyclerView recyclerView;
    private List<Application> applicationList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ApplicationRecruiterAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notifications_recruiter_fragment, container, false);

        // Khởi tạo Firestore, Auth và Realtime Database
        initializeFirebase();

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_applications);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout! Check activity_notifications_recruiter_fragment.xml");
            Toast.makeText(getContext(), "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return view;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        applicationList = new ArrayList<>();
        adapter = new ApplicationRecruiterAdapter(applicationList, getContext(), db);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "RecyclerView initialized with adapter");

        // Lấy danh sách đơn ứng tuyển
        if (currentUserId != null) {
            loadApplicationsByRecruiter();
        } else {
            Log.e(TAG, "Current user ID is null, authentication failed");
            Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            Log.d(TAG, "Authenticated user ID: " + currentUserId);
        } else {
            Log.e(TAG, "No authenticated user found");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    private void loadApplicationsByRecruiter() {
        Log.d(TAG, "Loading applications for recruiter: " + currentUserId);

        db.collection("internships")
                .whereEqualTo("companyId", currentUserId)
                .get()
                .addOnSuccessListener(internshipSnapshots -> {
                    List<String> internshipIds = new ArrayList<>();
                    if (internshipSnapshots.isEmpty()) {
                        Log.d(TAG, "No internships found for recruiter: " + currentUserId);
                        Toast.makeText(getContext(), "No internships found", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (QueryDocumentSnapshot doc : internshipSnapshots) {
                        internshipIds.add(doc.getId());
                        Log.d(TAG, "Found internship ID: " + doc.getId() + " with companyId: " + doc.getString("companyId"));
                    }
                    Log.d(TAG, "Total internship IDs found: " + internshipIds.size());

                    if (!internshipIds.isEmpty()) {
                        db.collection("applications")
                                .whereIn("internshipId", internshipIds)
                                .get()
                                .addOnSuccessListener(applicationSnapshots -> {
                                    applicationList.clear();
                                    if (!applicationSnapshots.isEmpty()) {
                                        List<String> studentIds = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : applicationSnapshots) {
                                            Application application = doc.toObject(Application.class);
                                            application.setApplicationId(doc.getId());
                                            applicationList.add(application);
                                            if (application.getStudentId() != null) {
                                                studentIds.add(application.getStudentId());
                                            }
                                        }
                                        Log.d(TAG, "Total applications loaded: " + applicationList.size());

                                        if (!studentIds.isEmpty()) {
                                            fetchStudentDetailsBatch(studentIds);
                                        } else {
                                            Log.d(TAG, "No student IDs to fetch");
                                            Toast.makeText(getContext(), "No student data found", Toast.LENGTH_SHORT).show();
                                        }
                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "Updated UI with initial applications");
                                    } else {
                                        Log.d(TAG, "No applications found for given internship IDs");
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getContext(), "No applications found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error fetching applications: " + e.getMessage());
                                    Toast.makeText(getContext(), "Error loading applications", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.d(TAG, "No internship IDs to query applications");
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching internships: " + e.getMessage());
                    Toast.makeText(getContext(), "Error loading internships", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchStudentDetailsBatch(List<String> studentIds) {
        if (studentIds.isEmpty()) {
            Log.d(TAG, "No student IDs to fetch");
            Toast.makeText(getContext(), "No student IDs to fetch", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Map<String, Object>> studentDataMap = new HashMap<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "No student data found in Realtime Database");
                    Toast.makeText(getContext(), "No student data found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    if (studentIds.contains(userId)) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("fullName", userSnapshot.child("name").getValue(String.class));
                        data.put("dateOfBirth", userSnapshot.child("dateOfBirth").getValue(String.class));
                        data.put("phoneNumber", userSnapshot.child("phoneNumber").getValue(String.class));
                        data.put("email", userSnapshot.child("email").getValue(String.class));
                        data.put("address", userSnapshot.child("address").getValue(String.class));
                        studentDataMap.put(userId, data);
                        Log.d(TAG, "Fetched student data for ID: " + userId);
                    }
                }

                if (studentDataMap.isEmpty()) {
                    Log.d(TAG, "No matching student data found for IDs: " + studentIds);
                    Toast.makeText(getContext(), "No matching student data found", Toast.LENGTH_SHORT).show();
                } else {
                    for (Application app : applicationList) {
                        Map<String, Object> studentData = studentDataMap.get(app.getStudentId());
                        if (studentData != null) {
                            app.setFullName((String) studentData.get("fullName"));
                            app.setDateOfBirth((String) studentData.get("dateOfBirth"));
                            app.setPhoneNumber((String) studentData.get("phoneNumber"));
                            app.setEmail((String) studentData.get("email"));
                            app.setAddress((String) studentData.get("address"));
                        } else {
                            Log.w(TAG, "No data found for studentId: " + app.getStudentId());
                            app.setFullName("Unknown (" + app.getStudentId() + ")");
                        }
                    }

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Updated UI with " + applicationList.size() + " items");
                    Toast.makeText(getContext(), "Loaded " + applicationList.size() + " applications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching student data: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Error loading student data", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }
}