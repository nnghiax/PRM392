package com.example.prm392app.ui.internships;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import com.example.prm392app.model.Internship;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InternshipDetailsFragment extends Fragment {
    private static final String TAG = "InternshipDetailsFragment";
    private TextView textJobTitle, textCompanyName, textDescription, textRequirements, textStipend, textDeadline, textApplicationStatus;
    private EditText editFullName, editPhoneNumber, editEmail, editAddress, editIntroduction, editPersonalSummary, editMotivation, editCommitment;
    private Button buttonSubmitApplication;
    private FirebaseFirestore db;
    private String internshipId;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internship_details, container, false);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo views
        textJobTitle = view.findViewById(R.id.text_job_title);
        textCompanyName = view.findViewById(R.id.text_company_name);
        textDescription = view.findViewById(R.id.text_description);
        textRequirements = view.findViewById(R.id.text_requirements);
        textStipend = view.findViewById(R.id.text_stipend);
        textDeadline = view.findViewById(R.id.text_deadline);
        textApplicationStatus = view.findViewById(R.id.text_application_status);
        editFullName = view.findViewById(R.id.edit_full_name);
        editPhoneNumber = view.findViewById(R.id.edit_phone_number);
        editEmail = view.findViewById(R.id.edit_email);
        editAddress = view.findViewById(R.id.edit_address);
        editIntroduction = view.findViewById(R.id.edit_introduction);
        editPersonalSummary = view.findViewById(R.id.edit_personal_summary);
        editMotivation = view.findViewById(R.id.edit_motivation);
        editCommitment = view.findViewById(R.id.edit_commitment);
        buttonSubmitApplication = view.findViewById(R.id.button_submit_application);

        // Lấy internship ID
        internshipId = getArguments() != null ? getArguments().getString("internship_id") : null;
        if (internshipId == null) {
            Log.e(TAG, "internshipId is null");
            Toast.makeText(getContext(), "Không tìm thấy ID thực tập", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Tải chi tiết thực tập
        loadInternshipDetails();

        // Xử lý nút nộp đơn
        buttonSubmitApplication.setOnClickListener(v -> submitApplication());

        return view;
    }

    private void loadInternshipDetails() {
        db.collection("internships").document(internshipId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Internship internship = documentSnapshot.toObject(Internship.class);
                    if (internship != null) {
                        textJobTitle.setText(internship.getJobTitle() != null ? internship.getJobTitle() : "Không có tiêu đề");
                        textCompanyName.setText("Công ty: " + (internship.getCompanyName() != null ? internship.getCompanyName() : "Không rõ"));
                        textDescription.setText("Mô tả: " + (internship.getDescription() != null ? internship.getDescription() : "Không có mô tả"));
                        textRequirements.setText("Yêu cầu: " + (internship.getRequirements() != null ? internship.getRequirements() : "Không có yêu cầu"));
                        textStipend.setText("Trợ cấp: " + (internship.getStipend() != null ?
                                String.format("%,.0f VNĐ", internship.getStipend()) : "Không rõ"));
                        textDeadline.setText("Hạn nộp: " + (internship.getDeadline() != null && internship.getDeadline() != 0 ?
                                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(internship.getDeadline())) : "Không có hạn"));
                        checkApplicationStatus();
                    } else {
                        Log.w(TAG, "Internship data is null for ID: " + internshipId);
                        Toast.makeText(getContext(), "Dữ liệu thực tập không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading internship details", e);
                    Toast.makeText(getContext(), "Lỗi khi tải chi tiết thực tập", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkApplicationStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để kiểm tra trạng thái", Toast.LENGTH_SHORT).show();
            return;
        }
        String studentId = currentUser.getUid();

        db.collection("applications")
                .whereEqualTo("internshipId", internshipId)
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Application application = queryDocumentSnapshots.getDocuments().get(0).toObject(Application.class);
                        textApplicationStatus.setVisibility(View.VISIBLE);
                        textApplicationStatus.setText("Trạng thái nộp hồ sơ: " + (application != null && application.getStatus() != null ? application.getStatus() : "Không rõ"));
                        buttonSubmitApplication.setEnabled(false);
                    } else {
                        textApplicationStatus.setVisibility(View.GONE);
                        buttonSubmitApplication.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking application status", e);
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái nộp hồ sơ", Toast.LENGTH_SHORT).show();
                });
    }

    private void submitApplication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để nộp đơn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (internshipId == null) {
            Log.e(TAG, "Cannot submit: internshipId is null");
            Toast.makeText(getContext(), "Lỗi khi nộp đơn", Toast.LENGTH_SHORT).show();
            return;
        }

        String studentId = currentUser.getUid();
        String fullName = editFullName.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String introduction = editIntroduction.getText().toString().trim();
        String personalSummary = editPersonalSummary.getText().toString().trim();
        String motivation = editMotivation.getText().toString().trim();
        String commitment = editCommitment.getText().toString().trim();
        String companyName = textCompanyName.getText().toString().replace("Công ty: ", "").trim();
        String applicationDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || introduction.isEmpty() ||
                personalSummary.isEmpty() || motivation.isEmpty() || commitment.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        String applicationId = db.collection("applications").document().getId();
        Application application = new Application(
                applicationId,
                studentId,
                internshipId,
                fullName,
                null, // dateOfBirth (tùy chọn, có thể thêm EditText nếu cần)
                phoneNumber,
                email,
                address,
                companyName,
                "ĐƠN XIN VIỆC", // applicationTitle (cố định, có thể cho nhập nếu cần)
                introduction,
                personalSummary,
                motivation,
                commitment,
                applicationDate,
                "Pending",
                System.currentTimeMillis()
        );

        db.collection("applications").document(applicationId).set(application)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Nộp đơn thành công", Toast.LENGTH_SHORT).show();
                    checkApplicationStatus();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving application", e);
                    Toast.makeText(getContext(), "Lỗi khi nộp đơn", Toast.LENGTH_SHORT).show();
                });
    }
}
