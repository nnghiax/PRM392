package com.example.prm392app.uiRecruiters.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.InterviewSlot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class InterviewSlotAdapter extends RecyclerView.Adapter<InterviewSlotAdapter.SlotViewHolder> {
    private List<InterviewSlot> slotList;
    private Context context;
    private FirebaseFirestore db;
    private boolean isStudent; // true if the user is a student, false if recruiter

    public InterviewSlotAdapter(List<InterviewSlot> slotList, Context context, boolean isStudent) {
        this.slotList = slotList;
        this.context = context;
        this.isStudent = isStudent;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interview_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        InterviewSlot slot = slotList.get(position);

        holder.tvJobTitle.setText(slot.getJobTitle());
        holder.tvCompanyName.setText(slot.getCompanyName());
        holder.tvDateTime.setText(slot.getProposedDateTime());
        holder.tvStatus.setText("Status: " + slot.getStatus());

        // Show/hide buttons based on user type and interview status
        if (isStudent) {
            if (slot.getStatus().equals("PENDING")) {
                holder.buttonContainer.setVisibility(View.VISIBLE);
                holder.btnAccept.setOnClickListener(v -> updateInterviewStatus(slot, "ACCEPTED", position));
                holder.btnDecline.setOnClickListener(v -> updateInterviewStatus(slot, "DECLINED", position));
            } else {
                holder.buttonContainer.setVisibility(View.GONE);
            }
        } else {
            // For recruiters, hide the accept/decline buttons
            holder.buttonContainer.setVisibility(View.GONE);
        }
    }

    private void updateInterviewStatus(InterviewSlot slot, String newStatus, int position) {
        db.collection("interview_slots")
                .document(slot.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    slot.setStatus(newStatus);
                    notifyItemChanged(position);
                    Toast.makeText(context,
                            newStatus.equals("ACCEPTED") ? "Interview accepted" : "Interview declined",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update interview status", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvDateTime, tvStatus;
        Button btnAccept, btnDecline;
        LinearLayout buttonContainer;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            buttonContainer = itemView.findViewById(R.id.buttonContainer);
        }
    }
}
