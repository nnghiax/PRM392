 package com.example.prm392app.uiRecruiters.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prm392app.R;
import com.example.prm392app.model.Internship;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeRecruiterFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText editTextJobTitle, editTextCompanyName, editTextLocationAddress;
    private EditText editTextDuration, editTextDescription, editTextRequirements, editTextStipend;
    private Spinner spinnerField;
    private Button buttonPickDeadline, buttonSaveInterview;
    private TextView textViewDeadline;
    private FirebaseFirestore db;
    private Double selectedLatitude, selectedLongitude;
    private Long selectedDeadline;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_recruiter_fragment, container, false);

        // Khởi tạo Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các thành phần giao diện
        editTextJobTitle = view.findViewById(R.id.editTextJobTitle);
        editTextCompanyName = view.findViewById(R.id.editTextCompanyName);
        editTextLocationAddress = view.findViewById(R.id.editTextLocationAddress);
        editTextDuration = view.findViewById(R.id.editTextDuration);
        spinnerField = view.findViewById(R.id.spinnerField);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextRequirements = view.findViewById(R.id.editTextRequirements);
        editTextStipend = view.findViewById(R.id.editTextStipend);
        buttonPickDeadline = view.findViewById(R.id.buttonPickDeadline);
        textViewDeadline = view.findViewById(R.id.textViewDeadline);
        buttonSaveInterview = view.findViewById(R.id.buttonSaveInterview);

        // Cấu hình Spinner cho field
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.field_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerField.setAdapter(adapter);

        // Khởi tạo Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Xử lý nút chọn ngày
        buttonPickDeadline.setOnClickListener(v -> showDatePickerDialog());

        // Xử lý nút lưu phỏng vấn
        buttonSaveInterview.setOnClickListener(v -> saveInterview());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Đặt vị trí mặc định (ví dụ: Hà Nội, Việt Nam)
        LatLng defaultLocation = new LatLng(21.0278, 105.8342);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Cho phép chọn vị trí trên bản đồ
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLatitude = latLng.latitude;
            selectedLongitude = latLng.longitude;
            editTextLocationAddress.setText("Selected: " + latLng.latitude + ", " + latLng.longitude);
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(year1, month1, dayOfMonth);
                    selectedDeadline = calendar.getTimeInMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    textViewDeadline.setText(sdf.format(calendar.getTime()));
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveInterview() {
        String jobTitle = editTextJobTitle.getText().toString().trim();
        String companyName = editTextCompanyName.getText().toString().trim();
        String locationAddress = editTextLocationAddress.getText().toString().trim();
        String duration = editTextDuration.getText().toString().trim();
        String field = spinnerField.getSelectedItem().toString();
        String description = editTextDescription.getText().toString().trim();
        String requirements = editTextRequirements.getText().toString().trim();
        String stipendStr = editTextStipend.getText().toString().trim();

        if (jobTitle.isEmpty() || companyName.isEmpty() || locationAddress.isEmpty() || duration.isEmpty() ||
                description.isEmpty() || requirements.isEmpty() || stipendStr.isEmpty() ||
                selectedLatitude == null || selectedLongitude == null || selectedDeadline == null) {
            Toast.makeText(getContext(), "Please fill all fields and select a location and deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi stipend sang Double
        Double stipend;
        try {
            stipend = Double.parseDouble(stipendStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid stipend value", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Internship
        String internshipId = db.collection("internships").document().getId();
        Internship internship = new Internship(
                internshipId,
                jobTitle,
                "companyId", // Thay bằng logic lấy companyId nếu cần
                companyName,
                locationAddress,
                selectedLatitude,
                selectedLongitude,
                duration,
                field,
                description,
                requirements,
                stipend,
                selectedDeadline,
                System.currentTimeMillis()
        );

        // Lưu vào Firestore collection internships
        db.collection("internships")
                .document(internshipId)
                .set(internship)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Interview created successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create interview: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
