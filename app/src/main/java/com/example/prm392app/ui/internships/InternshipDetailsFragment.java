package com.example.prm392app.ui.internships;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import com.example.prm392app.model.Internship;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InternshipDetailsFragment extends Fragment {
    private TextView textJobTitle, textCompanyName, textDescription, textRequirements, textStipend, textDeadline, textApplicationStatus;
    private Button buttonApply;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String internshipId;
    private ActivityResultLauncher<Intent> resumeLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internship_details, container, false);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("resumes");

        // Khởi tạo views
        textJobTitle = view.findViewById(R.id.text_job_title);
        textCompanyName = view.findViewById(R.id.text_company_name);
        textDescription = view.findViewById(R.id.text_description);
        textRequirements = view.findViewById(R.id.text_requirements);
        textStipend = view.findViewById(R.id.text_stipend);
        textDeadline = view.findViewById(R.id.text_deadline);
        textApplicationStatus = view.findViewById(R.id.text_application_status);
        buttonApply = view.findViewById(R.id.button_apply);

        // Lấy internship ID
        internshipId = getArguments().getString("internship_id");

        // Thiết lập resume picker
        resumeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri resumeUri = result.getData().getData();
                uploadResumeAndApply(resumeUri);
            }
        });

        // Thiết lập yêu cầu quyền
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                pickResume();
            } else {
                Toast.makeText(getContext(), "Cần quyền truy cập bộ nhớ để chọn hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });

        // Tải chi tiết thực tập
        loadInternshipDetails();

        // Xử lý nút nộp hồ sơ
        buttonApply.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                pickResume();
            }
        });

        return view;
    }

    private void loadInternshipDetails() {
        db.collection("internships").document(internshipId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Internship internship = documentSnapshot.toObject(Internship.class);
                    if (internship != null) {
                        textJobTitle.setText(internship.getJobTitle());
                        textCompanyName.setText("Công ty: " + internship.getCompanyName());
                        textDescription.setText("Mô tả: " + internship.getDescription());
                        textRequirements.setText("Yêu cầu: " + internship.getRequirements());
                        textStipend.setText("Trợ cấp: " + internship.getStipend() + " VNĐ");
                        textDeadline.setText("Hạn nộp: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(new Date(internship.getDeadline())));
                        checkApplicationStatus();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải chi tiết thực tập", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkApplicationStatus() {
        db.collection("applications")
                .whereEqualTo("internshipId", internshipId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Application application = queryDocumentSnapshots.getDocuments().get(0).toObject(Application.class);
                        textApplicationStatus.setVisibility(View.VISIBLE);
                        textApplicationStatus.setText("Trạng thái nộp hồ sơ: " + application.getStatus());
                        buttonApply.setEnabled(false);
                    } else {
                        textApplicationStatus.setVisibility(View.GONE);
                        buttonApply.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái nộp hồ sơ", Toast.LENGTH_SHORT).show();
                });
    }

    private void pickResume() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        resumeLauncher.launch(intent);
    }

    private void uploadResumeAndApply(Uri resumeUri) {
        String applicationId = db.collection("applications").document().getId();
        StorageReference resumeRef = storageRef.child(applicationId + ".pdf");

        resumeRef.putFile(resumeUri)
                .addOnSuccessListener(taskSnapshot -> resumeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Application application = new Application(
                            applicationId,
                            "anonymous_user", // Không yêu cầu đăng nhập
                            internshipId,
                            uri.toString(),
                            "Pending",
                            System.currentTimeMillis()
                    );
                    db.collection("applications").document(applicationId).set(application)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Nộp hồ sơ thành công", Toast.LENGTH_SHORT).show();
                                checkApplicationStatus();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi khi nộp hồ sơ", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải lên hồ sơ", Toast.LENGTH_SHORT).show();
                });
    }
}
