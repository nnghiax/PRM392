package com.example.prm392app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Internship;

import java.util.ArrayList;
import java.util.List;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.InternshipViewHolder> {
    private List<Internship> internships;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Internship internship);
    }

    public InternshipAdapter(Context context, List<Internship> internships, OnItemClickListener listener) {
        this.context = context;
        this.internships = internships != null ? internships : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public InternshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_internship, parent, false);
        return new InternshipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InternshipViewHolder holder, int position) {
        Internship internship = internships.get(position);
        if (internship != null) {
            holder.textJobTitle.setText(internship.getJobTitle() != null ? internship.getJobTitle() : "");
            holder.textCompanyName.setText(internship.getCompanyName() != null ? internship.getCompanyName() : "");
            holder.textLocation.setText(internship.getLocationAddress() != null ? internship.getLocationAddress() : "");
            holder.textDuration.setText(internship.getDuration() != null ? internship.getDuration() : "");
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(internship);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return internships != null ? internships.size() : 0;
    }

    public void updateData(List<Internship> newInternships) {
        this.internships = newInternships != null ? newInternships : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addInternship(Internship newInternship) {
        if (newInternship == null) {
            return; // Thoát nếu đối tượng null
        }
        if (internships == null) {
            internships = new ArrayList<>(); // Khởi tạo danh sách nếu null
        }
        internships.add(newInternship);
        notifyItemInserted(internships.size() - 1); // Cập nhật chỉ mục mới được thêm
    }

    static class InternshipViewHolder extends RecyclerView.ViewHolder {
        TextView textJobTitle, textCompanyName, textLocation, textDuration;

        public InternshipViewHolder(@NonNull View itemView) {
            super(itemView);
            textJobTitle = itemView.findViewById(R.id.text_job_title);
            textCompanyName = itemView.findViewById(R.id.text_company_name);
            textLocation = itemView.findViewById(R.id.text_location);
            textDuration = itemView.findViewById(R.id.text_duration);
        }
    }
}
