package com.example.prm392app.uiRecruiters.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ApplicationRecruiterAdapter extends RecyclerView.Adapter<ApplicationRecruiterAdapter.ViewHolder> {

    private List<Application> applicationList;
    private Context context;
    private FirebaseFirestore db;

    public ApplicationRecruiterAdapter(List<Application> applicationList, Context context, FirebaseFirestore db) {
        this.applicationList = applicationList;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_recruiter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.tvInternshipId.setText("Internship ID: " + (application.getInternshipId() != null ? application.getInternshipId() : "N/A"));
        holder.tvStatus.setText("Status: " + (application.getStatus() != null ? application.getStatus() : "N/A"));
        holder.tvFullName.setText("Full Name: " + (application.getFullName() != null ? application.getFullName() : "Unknown"));
        holder.tvApplicationTitle.setText("Application Title: " + (application.getApplicationTitle() != null ? application.getApplicationTitle() : "N/A"));
        holder.tvIntroduction.setText("Introduction: " + (application.getIntroduction() != null ? application.getIntroduction() : "N/A"));
        holder.tvCommitment.setText("Commitment: " + (application.getCommitment() != null ? application.getCommitment() : "N/A"));
    }

    @Override
    public int getItemCount() {
        return applicationList != null ? applicationList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInternshipId, tvStatus, tvFullName, tvApplicationTitle, tvIntroduction, tvCommitment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInternshipId = itemView.findViewById(R.id.tv_internship_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvApplicationTitle = itemView.findViewById(R.id.tv_application_title);
            tvIntroduction = itemView.findViewById(R.id.tv_introduction);
            tvCommitment = itemView.findViewById(R.id.tv_commitment); // Thay tv_job_title bằng tv_commitment
        }
    }
}