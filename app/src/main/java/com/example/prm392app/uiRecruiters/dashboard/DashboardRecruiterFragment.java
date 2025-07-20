package com.example.prm392app.uiRecruiters.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_dashboard_recruiter_fragment, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view_applications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        applicationList = new ArrayList<>();
        InterviewApplicationAdapter adapter = new InterviewApplicationAdapter(applicationList, getContext(),
                (application) -> showDateTimePicker(application));
        recyclerView.setAdapter(adapter);

        // Load applications with Under Review status
        loadUnderReviewApplications();

        return root;
    }

    private void loadUnderReviewApplications() {
        db.collection("applications")
                .whereEqualTo("status", "Under Review")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Application application = document.toObject(Application.class);
                        application.setApplicationId(document.getId());
                        applicationList.add(application);
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading applications", e));
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
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error scheduling interview", e));
    }

    private void updateApplicationStatus(String applicationId, String newStatus) {
        db.collection("applications")
                .document(applicationId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> loadUnderReviewApplications())
                .addOnFailureListener(e -> Log.e(TAG, "Error updating application status", e));
    }
}
