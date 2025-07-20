
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
        holder.btnWithdraw.setOnClickListener(v -> {
            updateApplicationStatus(application.getApplicationId(), "Withdraw", position);
        });
        holder.btnInterview.setOnClickListener(v -> {
            updateApplicationStatus(application.getApplicationId(), "Under Review", position);
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textCompany, textStatus;
        Button btnWithdraw, btnInterview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_application_title);
            textCompany = itemView.findViewById(R.id.text_company_name);
            textStatus = itemView.findViewById(R.id.text_status);
            btnWithdraw = itemView.findViewById(R.id.btn_withdraw);
            btnInterview = itemView.findViewById(R.id.btn_interview);
        }
    }

    private void updateApplicationStatus(String applicationId, String newStatus, int position) {
        db.collection("applications").document(applicationId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    applicationList.get(position).setStatus(newStatus);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                });
    }
}
