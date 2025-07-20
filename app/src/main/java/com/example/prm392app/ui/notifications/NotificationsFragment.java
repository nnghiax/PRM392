package com.example.prm392app.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.databinding.FragmentNotificationsBinding;
import com.example.prm392app.ui.adapter.ApplicationAdapter;
import com.example.prm392app.model.Application;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Thiết lập RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view_applications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationAdapter();
        recyclerView.setAdapter(adapter);

        // Quan sát danh sách ứng dụng và cập nhật adapter
        notificationsViewModel.getApplications().observe(getViewLifecycleOwner(), applications -> {
            if (applications != null) {
                adapter.setApplications(applications);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}