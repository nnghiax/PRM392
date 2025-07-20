package com.example.prm392app.ui.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messageList;
    private final String currentUserId;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        boolean isSentByMe = message.getSenderId().equals(currentUserId);

        holder.messageTextView.setText(message.getContent());

        // ? ??t background phù h?p
        holder.messageTextView.setBackgroundResource(
                isSentByMe ? R.drawable.bg_message_sent : R.drawable.bg_message_received
        );

        // ? C?n ch?nh trái/ph?i
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.messageTextView.getLayoutParams();
        params.gravity = isSentByMe ? Gravity.END : Gravity.START;
        holder.messageTextView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
