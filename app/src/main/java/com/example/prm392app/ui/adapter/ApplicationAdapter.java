package com.example.prm392app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {
    private List<Application> applicationList;
    private Context context;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onWithdrawClick(Application app);
        void onScheduleClick(Application app);
    }

    public ApplicationAdapter(List<Application> list, Context ctx, OnItemActionListener listener) {
        this.applicationList = list;
        this.context = ctx;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtStatus;
        Button btnWithdraw, btnSchedule;

        public ViewHolder(View view) {
            super(view);
            txtTitle = view.findViewById(R.id.txtTitleApplication);
            txtStatus = view.findViewById(R.id.txtStatusApplication);
            btnWithdraw = view.findViewById(R.id.btnWithdrawApplication);
            btnSchedule = view.findViewById(R.id.btnScheduleApplication);
        }

        public void bind(Application app) {
            txtTitle.setText("Internship ID: " + app.getInternshipId());
            txtStatus.setText("Status: " + app.getStatus());

            btnWithdraw.setOnClickListener(v -> {
                if (listener != null) listener.onWithdrawClick(app);
            });

            btnSchedule.setOnClickListener(v -> {
                if (listener != null) listener.onScheduleClick(app);
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(applicationList.get(position));
    }

    @Override
    public int getItemCount() {
        return applicationList != null ? applicationList.size() : 0;
    }
}
