package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
    private List<MessageItem> messageList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String chatId;
    private String userId;
    private ImageView backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get current user ID
        userId = auth.getCurrentUser().getUid();

        // Get chat ID from intent
        chatId = getIntent().getStringExtra("chatId");
        if (chatId == null) {
            Log.e("MessagesActivity", "chatId is null. Returning to main screen.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Initialize views
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);
        backButton = findViewById(R.id.arrowBack);

        // Set up RecyclerView
        messagesAdapter = new MessageAdapter(this, messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        // Load existing messages
        loadMessages(chatId);

        // Button Listeners
        sendButton.setOnClickListener(v -> sendMessage());
        acceptButton.setOnClickListener(v -> acceptUser());
        declineButton.setOnClickListener(v -> declineUser());
        backButton.setOnClickListener(v -> startActivity(new Intent(MessagesActivity.this, ChatActivity.class)));

        populateSwapItemDetails(chatId);

    }

    private void populateSwapItemDetails(String chatId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chats").document(chatId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageBlob = documentSnapshot.getString("swapItemImageBlob");
                        String description = documentSnapshot.getString("swapItemDescription");
                        String address = documentSnapshot.getString("swapItemAddress");

                        ImageView swapItemImage = findViewById(R.id.swapItemImage);
                        TextView swapItemDescription = findViewById(R.id.swapItemDescription);
                        TextView swapItemAddress = findViewById(R.id.swapItemAddress);

                        if (description != null) swapItemDescription.setText(description);
                        if (address != null) swapItemAddress.setText(address);

                        if (imageBlob != null && !imageBlob.isEmpty()) {
                            byte[] decodedString = android.util.Base64.decode(imageBlob, android.util.Base64.DEFAULT);
                            android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            swapItemImage.setImageBitmap(decodedByte);
                        }
                    } else {
                        Log.e("MessagesActivity", "No such chat document: " + chatId);
                    }
                })
                .addOnFailureListener(e -> Log.e("MessagesActivity", "Failed to fetch chat data: ", e));
    }

    private void loadMessages(String chatId) {
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("MessagesActivity", "Error loading messages", error);
                        return;
                    }

                    messageList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            MessageItem msg = doc.toObject(MessageItem.class);
                            if (msg != null) {
                                messageList.add(msg);
                            }
                        }
                        messagesAdapter.notifyDataSetChanged();
                        messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", userId);
        message.put("text", text);
        message.put("timestamp", com.google.firebase.Timestamp.now());

        db.collection("chats").document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> messageInput.setText(""))
                .addOnFailureListener(e -> Log.e("MessagesActivity", "Failed to send message", e));
    }

    private void acceptUser() {
        db.collection("chats").document(chatId)
                .update("status", "accepted")
                .addOnSuccessListener(aVoid -> Log.d("MessagesActivity", "User accepted"))
                .addOnFailureListener(e -> Log.e("MessagesActivity", "Failed to accept", e));
    }

    private void declineUser() {
        db.collection("chats").document(chatId)
                .update("status", "declined")
                .addOnSuccessListener(aVoid -> Log.d("MessagesActivity", "User declined"))
                .addOnFailureListener(e -> Log.e("MessagesActivity", "Failed to decline", e));
    }
}
