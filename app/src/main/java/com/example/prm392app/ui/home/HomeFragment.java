package com.example.prm392app.ui.home;

import android.os.Bundle;
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
                internship.setInternshipId(document.getId());
                internships.add(internship);
            }
            sortInternships();
            adapter.updateData(internships);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi tải danh sách thực tập", Toast.LENGTH_SHORT).show();
        });
    }

    private void sortInternships() {
        Collections.sort(internships, (i1, i2) -> {
            if (isSortedAscending) {
                return Long.compare(i1.getPostedAt(), i2.getPostedAt());
            } else {
                return Long.compare(i2.getPostedAt(), i1.getPostedAt());
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
        newInternship.setStipend(6000000L);
        newInternship.setDeadline(1764181200000L); // 24/11/2025 00:00:00 GMT+7
        newInternship.setPostedAt(System.currentTimeMillis()); // Thời gian hiện tại

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
                    Toast.makeText(getContext(), "Lỗi khi thêm thực tập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(Internship internship) {
        Bundle bundle = new Bundle();
        bundle.putString("internship_id", internship.getInternshipId());
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_home_to_internship_details, bundle);
    }
}
