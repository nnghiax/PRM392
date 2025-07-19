
package com.example.prm392app.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.ui.adapter.InternshipAdapter;
import com.example.prm392app.model.Internship;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements InternshipAdapter.OnItemClickListener {
    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private InternshipAdapter adapter;
    private List<Internship> internships;
    private Spinner spinnerFilter;
    private Button buttonSort;
    private boolean isSortedAscending = true;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Khởi tạo views
        recyclerView = view.findViewById(R.id.recycler_view_internships);
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        buttonSort = view.findViewById(R.id.button_sort);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        internships = new ArrayList<>();
        adapter = new InternshipAdapter(getContext(), internships, this);
        recyclerView.setAdapter(adapter);

        // Thiết lập bộ lọc
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.field_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedField = parent.getItemAtPosition(position).toString();
                loadInternships(selectedField);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Thiết lập sắp xếp
        buttonSort.setOnClickListener(v -> {
            isSortedAscending = !isSortedAscending;
            buttonSort.setText(isSortedAscending ? "Sắp xếp theo ngày (Tăng)" : "Sắp xếp theo ngày (Giảm)");
            sortInternships();
        });

        // Tải danh sách thực tập mặc định
        loadInternships("Tất cả");

        return view;
    }

    private void loadInternships(String field) {
        Query query = field.equals("Tất cả") ?
                db.collection("internships") :
                db.collection("internships").whereEqualTo("field", field);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            internships.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Internship internship = document.toObject(Internship.class);
                if (internship != null) {
                    internship.setInternshipId(document.getId());
                    internships.add(internship);
                } else {
                    Log.w(TAG, "Internship is null for document: " + document.getId());
                }
            }
            sortInternships();
            adapter.updateData(internships);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading internships", e);
            Toast.makeText(getContext(), "Lỗi khi tải danh sách thực tập", Toast.LENGTH_SHORT).show();
        });
    }

    private void sortInternships() {
        Collections.sort(internships, (i1, i2) -> {
            Long postedAt1 = i1.getPostedAt() != null ? i1.getPostedAt() : 0L;
            Long postedAt2 = i2.getPostedAt() != null ? i2.getPostedAt() : 0L;
            if (isSortedAscending) {
                return Long.compare(postedAt1, postedAt2);
            } else {
                return Long.compare(postedAt2, postedAt1);
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void addNewInternship() {
        // Tạo một đối tượng Internship mẫu
        Internship newInternship = new Internship();
        newInternship.setJobTitle("Thực tập sinh mới");
        newInternship.setCompanyName("Công ty Mới");
        newInternship.setLocationAddress("789 Đường Mới");
        newInternship.setDuration("4 tháng");
        newInternship.setField("IT");
        newInternship.setDescription("Phát triển ứng dụng di động");
        newInternship.setRequirements("Kiến thức cơ bản về Kotlin");
        newInternship.setStipend(6000000.0); // Sửa từ 6000000L thành 6000000.0 để khớp với Double
        newInternship.setDeadline(1764181200000L); // Dùng Long
        newInternship.setPostedAt(System.currentTimeMillis()); // Dùng Long

        // Lưu vào Firestore
        db.collection("internships")
                .add(newInternship)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    newInternship.setInternshipId(id); // Cập nhật ID
                    internships.add(newInternship); // Thêm vào danh sách cục bộ
                    adapter.addInternship(newInternship); // Cập nhật UI
                    Toast.makeText(getContext(), "Thêm thực tập thành công, ID: " + id, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding internship", e);
                    Toast.makeText(getContext(), "Lỗi khi thêm thực tập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(Internship internship) {
        if (internship != null && internship.getInternshipId() != null) {
            Bundle bundle = new Bundle();
            Log.d(TAG, "Clicked internship with ID: " + internship.getInternshipId());
            bundle.putString("internship_id", internship.getInternshipId());
            NavController navController = Navigation.findNavController(requireView());
            try {
                navController.navigate(R.id.action_home_to_internship_details, bundle);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Navigation failed: " + e.getMessage());
                Toast.makeText(getContext(), "Lỗi khi chuyển trang chi tiết", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Internship or internshipId is null");
            Toast.makeText(getContext(), "Dữ liệu thực tập không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}
