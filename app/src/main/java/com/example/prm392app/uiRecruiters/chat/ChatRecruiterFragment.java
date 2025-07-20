package com.example.prm392app.uiRecruiters.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392app.R;
import com.example.prm392app.model.ChatMessage;
import com.example.prm392app.ui.adapter.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ChatRecruiterFragment extends Fragment {

    private static final String TAG = "ChatRecruiterFragment";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList = new ArrayList<>();

    private String currentUserId;
    private String otherUserId;
    private String otherUserName;
    private String chatId;

    private TextView chatWithTextView;
    private EditText messageEditText;
    private ImageButton sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ✅ Đã chỉnh sửa để dùng đúng layout
        View view = inflater.inflate(R.layout.fragment_chat_recruiter, container, false);

        requireActivity().setTitle("Chat");
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.chatRecyclerView);
        chatWithTextView = view.findViewById(R.id.chatWithTextView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);

        adapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle args = getArguments();
        if (args != null) {
            otherUserId = args.getString("otherUserId");
            otherUserName = args.getString("otherUserName");

            chatWithTextView.setText("Đang trò chuyện với: " + otherUserName);

            chatId = generateChatId(currentUserId, otherUserId);
            loadMessages();
        }

        sendButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
                messageEditText.setText("");
            }
        });

        return view;
    }

    private String generateChatId(String id1, String id2) {
        return (id1.compareTo(id2) < 0 ? id1 + "_" + id2 : id2 + "_" + id1)
                .replace(".", "_")
                .replace("$", "_")
                .replace("#", "_")
                .replace("[", "_")
                .replace("]", "_")
                .replace("/", "_");
    }

    private void loadMessages() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(chatId)
                .child("messages");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot msgSnap : snapshot.getChildren()) {
                    ChatMessage message = msgSnap.getValue(ChatMessage.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadMessages: " + error.getMessage());
            }
        });
    }

    private void sendMessage(String text) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(chatId)
                .child("messages");

        String messageId = ref.push().getKey();
        if (messageId != null) {
            ChatMessage message = new ChatMessage(currentUserId, text, System.currentTimeMillis());
            ref.child(messageId).setValue(message)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Gửi thành công"))
                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi gửi: " + e.getMessage()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
