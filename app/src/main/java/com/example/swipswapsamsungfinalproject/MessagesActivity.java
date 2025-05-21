
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    private TextView swapItemDescription, swapItemAddress, swapItemStatus
            , itemContact, ownerLabel, tvGivenCount, tvTakenCount;
    private boolean youOwner;
    private String currentUserEmail;

    private MessageAdapter messagesAdapter;
    private List<MessageItem> messageList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String chatId, userId, currentStatus;
    private boolean isOwner = false;
    private FirebaseFirestore firestoreDb;

    private long swapId;
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
        itemContact  = findViewById(R.id.chat_item_contact);
        ownerLabel = findViewById(R.id.owner_label_my);
        tvGivenCount = findViewById(R.id.tvGivenCount);
        tvTakenCount = findViewById(R.id.tvTakenCount);

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
        currentUserEmail = auth.getCurrentUser().getEmail();

        db.collection("chats").document(chatId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    currentStatus = snapshot.getString("status");
                    swapId = snapshot.getLong("swapId");
                    swapOwnerId = snapshot.getString("swapOwnerId");
                    clientUserId = snapshot.getString("clientUserId");


                    isOwner = userId.equals(swapOwnerId);

                    if (isOwner) {
                        this.ownerLabel.setVisibility(View.VISIBLE);
                    } else {
                        this.ownerLabel.setVisibility(View.GONE);
                    }

                    String imgBlob = snapshot.getString("swapItemImageBlob");
                    if (imgBlob != null && !imgBlob.isEmpty()) {
                        byte[] imageBytes = Base64.decode(imgBlob, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        swapItemImage.setImageBitmap(bitmap);
                    }

                    swapItemDescription.setText(snapshot.getString("swapItemDescription"));
                    swapItemAddress.setText(snapshot.getString("swapItemAddress"));

                    if( isOwner ) {
                        loadUserProfile(snapshot.getString("clientUserId"));
                        itemContact.setText(snapshot.getString("clientUserEmail") != null ? "contact: " + snapshot.getString("clientUserEmail") : "");
                    } else{
                        loadUserProfile(snapshot.getString("swapOwnerId"));
                        itemContact.setText(snapshot.getString("swapOwnerEmail") != null ? "contact: " + snapshot.getString("swapOwnerEmail") : "");
                    }
                    updateUIBasedOnStatus();
                    loadMessages();
                });
    }
    private void loadUserProfile(String userId) {

        db.collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;
                    User user = snapshot.toObject(User.class);
                    if (user != null) {
                        int givenCount = user.getGiven();
                        int takenCount = user.getTaken();

                        tvGivenCount.setText("given: " + givenCount);
                        tvTakenCount.setText("taken: " + takenCount);
                    }
        });
       }
    private void updateSwapItemStatus(){

        db.collection("swap_items")
                .whereEqualTo("swapId", swapId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        doc.getReference()
                                .update("status", currentStatus)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "SwapItem updated"))
                                .addOnFailureListener(e -> Log.e("FirestoreError", "SwapItem update failed", e));
                    } else {
                        Log.e("Firestore", "No swap_item found with swapId = " + swapId);
                    }
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
                break;
        }

        swapItemStatus.setText(currentStatus);

        // Set status icon
        switch (currentStatus) {
            case "chosen":
                swapItemStatus.setTextColor(this.getColor(R.color.statusYellow));
                break;
            case "accepted":
                swapItemStatus.setTextColor(this.getColor(R.color.statusRed));
                break;
            case "published":
                swapItemStatus.setTextColor(this.getColor(R.color.statusGrey));
                break;
            case "declined":
                swapItemStatus.setTextColor(this.getColor(R.color.statusBlack));
                break;
            default:
                swapItemStatus.setTextColor(this.getColor(R.color.statusGreen));
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
        message.put("timestamp", FieldValue.serverTimestamp());

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
                    updateSwapItemStatus();
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
                    updateSwapItemStatus();
                });
    }

    private void markAsTaken() {
        db.collection("chats").document(chatId).update("status", "given")
                .addOnSuccessListener(unused -> {
                    incrementGiven(swapOwnerId);
                    incrementTaken(clientUserId);

                    Toast.makeText(this, "Marked as Given", Toast.LENGTH_SHORT).show();
                    currentStatus = "given";
                    updateSwapItemStatus();
                    updateUIBasedOnStatus();
                });

    }

    private void incrementGiven(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    Long current = snapshot.getLong("given");
                    long takenCount = current != null ? current : 0;
                    db.collection("users").document(userId)
                            .update("given", takenCount + 1);
                });
    }

    private void incrementTaken(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    Long current = snapshot.getLong("taken");
                    long takenCount = current != null ? current : 0;
                    db.collection("users").document(userId)
                            .update("taken", takenCount + 1);
                });
    }

}
