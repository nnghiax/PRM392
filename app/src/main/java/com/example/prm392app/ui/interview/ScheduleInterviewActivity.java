package com.example.prm392app.ui.interview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392app.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ScheduleInterviewActivity extends AppCompatActivity {
    private EditText edtDateTime;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private String applicationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_interview);

        edtDateTime = findViewById(R.id.edtDateTimeSchedule);
        btnConfirm = findViewById(R.id.btnConfirmSchedule);
        db = FirebaseFirestore.getInstance();

        applicationId = getIntent().getStringExtra("applicationId");

        btnConfirm.setOnClickListener(v -> {
            String interviewTime = edtDateTime.getText().toString().trim();
            if (interviewTime.isEmpty()) {
                Toast.makeText(this, "Please enter a time", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> schedule = new HashMap<>();
            schedule.put("applicationId", applicationId);
            schedule.put("interviewTime", interviewTime);
            schedule.put("status", "Scheduled");

            db.collection("interviews").document(applicationId).set(schedule)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Interview scheduled", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to schedule interview", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
