
package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button sendButton, acceptButton, declineButton, takenButton;
    private ImageView backButton, swapItemImage;
    private TextView swapItemDescription, swapItemAddress, swapItemStatus;

    private MessageAdapter messagesAdapter;
    private List<MessageItem> messageList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String chatId, userId, currentStatus;
    private boolean isOwner = false;
    private String swapOwnerId = "", clientUserId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        chatId = getIntent().getStringExtra("chatId");

        if (chatId == null) {
            Toast.makeText(this, "Chat ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);
        takenButton = findViewById(R.id.takenButton);
        backButton = findViewById(R.id.arrowBack);
        swapItemImage = findViewById(R.id.swapItemImage);
        swapItemDescription = findViewById(R.id.swapItemDescription);
        swapItemAddress = findViewById(R.id.swapItemAddress);
        swapItemStatus = findViewById(R.id.swapItemStatus);

        messagesAdapter = new MessageAdapter(this, messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        loadChatInfo();

        sendButton.setOnClickListener(v -> sendMessage());
        acceptButton.setOnClickListener(v -> acceptUser());
        declineButton.setOnClickListener(v -> declineUser());
        takenButton.setOnClickListener(v -> markAsTaken());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadChatInfo() {
        db.collection("chats").document(chatId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    currentStatus = snapshot.getString("status");
                    swapOwnerId = snapshot.getString("swapOwnerId");
                    clientUserId = snapshot.getString("clientUserId");

                    isOwner = userId.equals(swapOwnerId);

                    String imgBlob = snapshot.getString("swapItemImageBlob");
                    if (imgBlob != null && !imgBlob.isEmpty()) {
                        byte[] imageBytes = Base64.decode(imgBlob, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        swapItemImage.setImageBitmap(bitmap);
                    }

                    swapItemDescription.setText(snapshot.getString("swapItemDescription"));
                    swapItemAddress.setText(snapshot.getString("swapItemAddress"));
                    swapItemStatus.setText(currentStatus);

                    updateUIBasedOnStatus();
                    loadMessages();
                });
    }

    private void updateUIBasedOnStatus() {
        messageInput.setEnabled(true);
        sendButton.setEnabled(true);

        acceptButton.setVisibility(View.GONE);
        declineButton.setVisibility(View.GONE);
        takenButton.setVisibility(View.GONE);

        switch (currentStatus) {
            case "chosen":
                if (isOwner) {
                    acceptButton.setVisibility(View.VISIBLE);
                    declineButton.setVisibility(View.VISIBLE);
                } else if (userId.equals(clientUserId)) {
                    declineButton.setVisibility(View.VISIBLE);
                }
                break;
            case "declined":
                messageInput.setEnabled(false);
                sendButton.setEnabled(false);
                break;
            case "accepted":
                if (!isOwner && userId.equals(clientUserId)) {
                    takenButton.setVisibility(View.VISIBLE);
                }
                break;
            case "given":
                // No buttons visible, messaging allowed
                break;
        }
    }

    private void loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("MessagesActivity", "Message load error", error);
                        return;
                    }

                    messageList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        MessageItem message = doc.toObject(MessageItem.class);
                        messageList.add(message);
                    }
                    messagesAdapter.notifyDataSetChanged();
                    messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", userId);
        message.put("text", text);
        message.put("timestamp", Timestamp.now());

        db.collection("chats").document(chatId).collection("messages")
                .add(message)
                .addOnSuccessListener(aVoid -> messageInput.setText(""));
    }

    private void acceptUser() {
        db.collection("chats").document(chatId).update("status", "accepted")
                .addOnSuccessListener(unused -> {
                    declineOtherClients();
                    Toast.makeText(this, "Accepted", Toast.LENGTH_SHORT).show();
                    currentStatus = "accepted";
                    updateUIBasedOnStatus();
                });
    }

    private void declineOtherClients() {
        db.collection("chats")
                .whereEqualTo("swapOwnerId", swapOwnerId)
                .whereEqualTo("status", "chosen")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (!doc.getId().equals(chatId)) {
                            doc.getReference().update("status", "declined");
                        }
                    }
                });
    }

    private void declineUser() {
        db.collection("chats").document(chatId).update("status", "declined")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Declined", Toast.LENGTH_SHORT).show();
                    currentStatus = "declined";
                    updateUIBasedOnStatus();
                });
    }

    private void markAsTaken() {
        db.collection("chats").document(chatId).update("status", "given")
                .addOnSuccessListener(unused -> {
                    incrementField("Users", swapOwnerId, "given");
                    incrementField("Users", clientUserId, "taken");
                    Toast.makeText(this, "Marked as Given", Toast.LENGTH_SHORT).show();
                    currentStatus = "given";
                    updateUIBasedOnStatus();
                });
    }

    private void incrementField(String collection, String userId, String field) {
        db.collection(collection).document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    long current = snapshot.contains(field) ? snapshot.getLong(field) : 0;
                    db.collection(collection).document(userId).update(field, current + 1);
                });
    }

}
