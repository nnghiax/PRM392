package com.example.prm392app.uiRecruiters.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.example.prm392app.model.Application;
import com.example.prm392app.model.Internship;
import com.example.prm392app.model.InterviewSchedule;
import com.example.prm392app.uiRecruiters.adapter.InterviewApplicationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class DashboardRecruiterFragment extends Fragment {
    private static final String TAG = "DashboardRecruiterFrag";
    private RecyclerView recyclerView;
    private List<Application> applicationList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private InterviewApplicationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_dashboard_recruiter_fragment, container, false);

        initializeFirebase();
        setupRecyclerView(root);
        if (currentUserId != null) {
            loadApplicationsByRecruiter();
        }

        return root;
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            Log.d(TAG, "Current user ID (Recruiter): " + currentUserId);
        } else {
            Log.e(TAG, "No user logged in");
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView(View root) {
        recyclerView = root.findViewById(R.id.recycler_view_applications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        applicationList = new ArrayList<>();
        adapter = new InterviewApplicationAdapter(applicationList, getContext(), this::showDateTimePicker);
        recyclerView.setAdapter(adapter);
    }

    private void loadApplicationsByRecruiter() {
        Log.d(TAG, "Loading applications for recruiter: " + currentUserId);

        // First, get all internships posted by this recruiter
        db.collection("internships")
                .whereEqualTo("companyId", currentUserId)
                .get()
                .addOnSuccessListener(internshipSnapshots -> {
                    List<String> internshipIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : internshipSnapshots) {
                        Internship internship = doc.toObject(Internship.class);
                        internshipIds.add(doc.getId());
                        Log.d(TAG, "Found internship: " + doc.getId() + " with company: " + internship.getCompanyName());
                    }

                    if (internshipIds.isEmpty()) {
                        Log.d(TAG, "No internships found for this recruiter");
                        return;
                    }

                    // Then, get all applications for these internships with "Under Review" status
                    db.collection("applications")
                            .whereIn("internshipId", internshipIds)
                            .whereEqualTo("status", "Under Review")
                            .addSnapshotListener((value, error) -> {
                                if (error != null) {
                                    Log.e(TAG, "Listen failed for applications", error);
                                    return;
                                }

                                applicationList.clear();
                                if (value != null && !value.isEmpty()) {
                                    for (QueryDocumentSnapshot document : value) {
                                        try {
                                            Application application = document.toObject(Application.class);
                                            application.setApplicationId(document.getId());
                                            applicationList.add(application);
                                            Log.d(TAG, "Added application: " + document.getId() +
                                                    " for internship: " + application.getInternshipId() +
                                                    " from student: " + application.getFullName());
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error converting document", e);
                                        }
                                    }
                                    Log.d(TAG, "Total applications loaded: " + applicationList.size());
                                } else {
                                    Log.d(TAG, "No applications found with Under Review status");
                                }

                                adapter.notifyDataSetChanged();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting internships", e);
                    Toast.makeText(getContext(), "Error loading internships", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDateTimePicker(Application application) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timeDialog = new TimePickerDialog(getContext(),
                            (view1, hourOfDay, minute) -> {
                                String dateTime = String.format("%04d-%02d-%02d %02d:%02d",
                                        year, month + 1, dayOfMonth, hourOfDay, minute);
                                scheduleInterview(application, dateTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timeDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dateDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dateDialog.show();
    }

    private void scheduleInterview(Application application, String dateTime) {
        String scheduleId = UUID.randomUUID().toString();
        InterviewSchedule schedule = new InterviewSchedule(
                scheduleId,
                application.getApplicationId(),
                dateTime,
                "PENDING",
                ""
        );

        db.collection("interview_schedules")
                .document(scheduleId)
                .set(schedule)
                .addOnSuccessListener(aVoid -> {
                    updateApplicationStatus(application.getApplicationId(), "Scheduled");
                    Toast.makeText(getContext(), "Interview scheduled successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error scheduling interview", e);
                    Toast.makeText(getContext(), "Failed to schedule interview", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateApplicationStatus(String applicationId, String newStatus) {
        db.collection("applications")
                .document(applicationId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Application status updated to: " + newStatus);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating application status", e);
                });
    }
}
