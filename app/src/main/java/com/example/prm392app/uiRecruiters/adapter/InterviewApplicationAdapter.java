package com.example.prm392app.uiRecruiters.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392app.R;
import com.example.prm392app.model.Application;
import java.util.List;

public class InterviewApplicationAdapter extends RecyclerView.Adapter<InterviewApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;
    private Context context;
    private OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Application application);
    }

    public InterviewApplicationAdapter(List<Application> applicationList, Context context, OnScheduleClickListener listener) {
        this.applicationList = applicationList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interview_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.textTitle.setText(application.getApplicationTitle());
        holder.textStudentName.setText("Ứng viên: " + application.getFullName());
        holder.textStatus.setText("Trạng thái: " + application.getStatus());

        holder.btnSchedule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onScheduleClick(application);
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textStudentName, textStatus;
        Button btnSchedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_application_title);
            textStudentName = itemView.findViewById(R.id.text_student_name);
            textStatus = itemView.findViewById(R.id.text_status);
            btnSchedule = itemView.findViewById(R.id.btn_schedule);
        }
    }
}
