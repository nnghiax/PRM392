package com.example.prm392app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import com.example.prm392app.model.InterviewSchedule;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;
    private Context context;
    private FirebaseFirestore db;

    public ApplicationAdapter(List<Application> applicationList, Context context, FirebaseFirestore db) {
        this.applicationList = applicationList;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.textTitle.setText(application.getApplicationTitle());
        holder.textCompany.setText(application.getCompanyName());
        holder.textStatus.setText(application.getStatus());

        // Only show withdraw and interview buttons for Pending status
        if ("Pending".equals(application.getStatus())) {
            holder.btnWithdraw.setVisibility(View.VISIBLE);
            holder.btnInterview.setVisibility(View.VISIBLE);
            holder.btnWithdraw.setOnClickListener(v -> {
                updateApplicationStatus(application.getApplicationId(), "Withdraw", position);
            });
            holder.btnInterview.setOnClickListener(v -> {
                updateApplicationStatus(application.getApplicationId(), "Under Review", position);
            });
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnDecline.setVisibility(View.GONE);
        } else if ("Scheduled".equals(application.getStatus())) {
            holder.btnWithdraw.setVisibility(View.GONE);
            holder.btnInterview.setVisibility(View.GONE);
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnDecline.setVisibility(View.VISIBLE);

            // Load interview schedule
            db.collection("interview_schedules")
                .whereEqualTo("applicationId", application.getApplicationId())
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        InterviewSchedule schedule = queryDocumentSnapshots.getDocuments().get(0).toObject(InterviewSchedule.class);
                        holder.textInterviewTime.setText("Lịch phỏng vấn: " + schedule.getProposedDateTime());
                        holder.textInterviewTime.setVisibility(View.VISIBLE);

                        holder.btnAccept.setOnClickListener(v -> {
                            updateInterviewStatus(schedule.getScheduleId(), "ACCEPTED", application.getApplicationId());
                        });

                        holder.btnDecline.setOnClickListener(v -> {
                            updateInterviewStatus(schedule.getScheduleId(), "DECLINED", application.getApplicationId());
                        });
                    }
                });
        } else {
            holder.btnWithdraw.setVisibility(View.GONE);
            holder.btnInterview.setVisibility(View.GONE);
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnDecline.setVisibility(View.GONE);
            holder.textInterviewTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    private void updateApplicationStatus(String applicationId, String newStatus, int position) {
        db.collection("applications").document(applicationId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Chỉ cập nhật list và notify nếu position hợp lệ
                    if (position >= 0 && position < applicationList.size()) {
                        applicationList.get(position).setStatus(newStatus);
                        notifyItemChanged(position);
                    } else {
                        // Nếu position không hợp lệ, refresh toàn bộ list
                        notifyDataSetChanged();
                    }
                    Toast.makeText(context, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateInterviewStatus(String scheduleId, String status, String applicationId) {
        // Tìm position của application trong list
        int position = -1;
        for (int i = 0; i < applicationList.size(); i++) {
            if (applicationList.get(i).getApplicationId().equals(applicationId)) {
                position = i;
                break;
            }
        }

        final int finalPosition = position;
        db.collection("interview_schedules")
            .document(scheduleId)
            .update("status", status)
            .addOnSuccessListener(aVoid -> {
                String newAppStatus = status.equals("ACCEPTED") ? "Interview Accepted" : "Interview Declined";
                updateApplicationStatus(applicationId, newAppStatus, finalPosition);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi khi cập nhật trạng thái phỏng vấn", Toast.LENGTH_SHORT).show();
            });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textCompany, textStatus, textInterviewTime;
        Button btnWithdraw, btnInterview, btnAccept, btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_application_title);
            textCompany = itemView.findViewById(R.id.text_company_name);
            textStatus = itemView.findViewById(R.id.text_status);
            textInterviewTime = itemView.findViewById(R.id.text_interview_time);
            btnWithdraw = itemView.findViewById(R.id.btn_withdraw);
            btnInterview = itemView.findViewById(R.id.btn_interview);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnDecline = itemView.findViewById(R.id.btn_decline);
        }
    }
}
