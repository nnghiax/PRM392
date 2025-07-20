package com.example.prm392app.uiRecruiters.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.InterviewSlot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InterviewScheduleFragment extends Fragment {
    private RecyclerView rvInterviewSlots;
    private LinearLayout recruiterPanel;
    private EditText etDateTime;
    private Button btnProposeSlot;
    private InterviewSlotAdapter adapter;
    private List<InterviewSlot> slotList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private boolean isRecruiter;
    private Calendar selectedDateTime;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_interview_schedule, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        rvInterviewSlots = root.findViewById(R.id.rvInterviewSlots);
        recruiterPanel = root.findViewById(R.id.recruiterPanel);
        etDateTime = root.findViewById(R.id.etDateTime);
        btnProposeSlot = root.findViewById(R.id.btnProposeSlot);

        // Check if user is recruiter (You need to implement this based on your user role system)
        isRecruiter = checkIfUserIsRecruiter();

        // Setup RecyclerView
        slotList = new ArrayList<>();
        adapter = new InterviewSlotAdapter(slotList, getContext(), !isRecruiter);
        rvInterviewSlots.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInterviewSlots.setAdapter(adapter);

        // Show/hide recruiter panel
        recruiterPanel.setVisibility(isRecruiter ? View.VISIBLE : View.GONE);

        if (isRecruiter) {
            setupDateTimePicker();
            setupProposeButton();
        }

        // Load interview slots
        loadInterviewSlots();

        return root;
    }

    private boolean checkIfUserIsRecruiter() {
        // Implement your logic to check if current user is a recruiter
        // This should match your existing role management system
        return true; // Temporary return value
    }

    private void setupDateTimePicker() {
        selectedDateTime = Calendar.getInstance();

        etDateTime.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // After date is selected, show time picker
                    new TimePickerDialog(
                        requireContext(),
                        (timeView, hourOfDay, minute) -> {
                            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            selectedDateTime.set(Calendar.MINUTE, minute);

                            // Update EditText with selected date and time
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            etDateTime.setText(sdf.format(selectedDateTime.getTime()));
                        },
                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                        selectedDateTime.get(Calendar.MINUTE),
                        true
                    ).show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupProposeButton() {
        btnProposeSlot.setOnClickListener(v -> {
            if (etDateTime.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new interview slot
            InterviewSlot newSlot = new InterviewSlot(
                "applicationId", // You need to pass this from the application
                mAuth.getCurrentUser().getUid(),
                "studentId", // You need to pass this from the application
                etDateTime.getText().toString(),
                "Company Name", // You need to pass this from the application
                "Job Title" // You need to pass this from the application
            );

            // Save to Firebase
            db.collection("interview_slots")
                .add(newSlot)
                .addOnSuccessListener(documentReference -> {
                    newSlot.setId(documentReference.getId());
                    slotList.add(newSlot);
                    adapter.notifyItemInserted(slotList.size() - 1);
                    etDateTime.setText("");
                    Toast.makeText(getContext(), "Interview slot proposed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to propose interview slot", Toast.LENGTH_SHORT).show();
                });
        });
    }

    private void loadInterviewSlots() {
        String userId = mAuth.getCurrentUser().getUid();
        String field = isRecruiter ? "recruiterId" : "studentId";

        db.collection("interview_slots")
            .whereEqualTo(field, userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                slotList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    InterviewSlot slot = document.toObject(InterviewSlot.class);
                    slot.setId(document.getId());
                    slotList.add(slot);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load interview slots", Toast.LENGTH_SHORT).show();
            });
    }
}
