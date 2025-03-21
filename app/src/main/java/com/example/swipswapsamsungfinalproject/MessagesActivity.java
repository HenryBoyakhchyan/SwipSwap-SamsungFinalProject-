package com.example.swipswapsamsungfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private Button sendButton, acceptButton, declineButton;
    private MessageAdapter messagesAdapter;
    private String chatId, userId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<MessageItem> messageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        chatId = getIntent().getStringExtra("chatId");

        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new MessageAdapter(this, new ArrayList<>());
        messagesRecyclerView.setAdapter(messagesAdapter);

        loadMessages(chatId);

        sendButton.setOnClickListener(v -> sendMessage());

        acceptButton.setOnClickListener(v -> acceptUser());
        declineButton.setOnClickListener(v -> declineUser());
    }

    private void loadMessages(String chatId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ChatActivity", "Listen failed.", error);
                        return;
                    }

                    messageList.clear();
                    if (value != null) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            MessageItem message = snapshot.toObject(MessageItem.class);
                            messageList.add(message);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            Map<String, Object> message = new HashMap<>();
            message.put("senderId", userId);
            message.put("text", text);
            message.put("timestamp", System.currentTimeMillis());

            db.collection("chats").document(chatId)
                    .collection("messages").add(message);
            messageInput.setText("");
        }
    }

    private void acceptUser() {
        DocumentReference chatRef = db.collection("chats").document(chatId);
        chatRef.update("status", "accepted");
    }

    private void declineUser() {
        DocumentReference chatRef = db.collection("chats").document(chatId);
        chatRef.update("status", "declined");
    }
}
