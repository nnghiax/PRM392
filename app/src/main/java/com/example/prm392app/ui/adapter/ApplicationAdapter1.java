package com.example.prm392app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter1 extends RecyclerView.Adapter<ApplicationAdapter1.ViewHolder> {

    private List<Application> applications;
    private Context context;

    public ApplicationAdapter1() {
        this.applications = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applications.get(position);
        holder.textTitle.setText(application.getApplicationTitle());
        holder.textStatus.setText("Status: " + application.getStatus());
        holder.textDate.setText("Applied on: " + application.getApplicationDate());
        holder.textCompanyName.setText("Company: " + application.getCompanyName()); // Thêm dòng này
    }

    @Override
    public int getItemCount() {
        return applications != null ? applications.size() : 0;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textStatus, textDate, textCompanyName; // Thêm textCompanyName

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_application_title);
            textStatus = itemView.findViewById(R.id.text_application_status);
            textDate = itemView.findViewById(R.id.text_application_date);
            textCompanyName = itemView.findViewById(R.id.text_company_name); // Thêm ID này
        }
    }
}