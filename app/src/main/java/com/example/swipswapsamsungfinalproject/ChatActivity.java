package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    private ImageView home, plus, user;
    private RecyclerView chatRecyclerView;
    private ChatListAdapter chatListAdapter;
    private List<ChatItem> chatList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String currentUserEmail;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        home = findViewById(R.id.home);
        plus = findViewById(R.id.plus);
        user = findViewById(R.id.user);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(this, chatList);
        chatRecyclerView.setAdapter(chatListAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        currentUserEmail = currentUser.getEmail();
        currentUserId = currentUser.getUid();

        loadChatList();

        plus.setOnClickListener(view -> startActivity(new Intent(this, CreateActivity.class)));
        home.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        user.setOnClickListener(view -> startActivity(new Intent(this, UserActivity.class)));
    }

    private void loadChatList() {
        if (currentUserEmail == null) return;

        Set<String> loadedChatIds = new HashSet<>();
        chatList.clear();

        // Fetch chats where user is the client
        db.collection("chats")
                .whereEqualTo("clientUserId", currentUserId)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(clientChats -> {
                    for (DocumentSnapshot doc : clientChats.getDocuments()) {
                        ChatItem chatItem = doc.toObject(ChatItem.class);
                        if (chatItem != null && loadedChatIds.add(doc.getId())) {
                            chatItem.setChatId(doc.getId());
                            chatList.add(chatItem);
                        }
                    }
                    // Fetch chats where user is the swap owner
                    db.collection("chats")
                            .whereEqualTo("swapOwnerId", currentUserId)
                            .orderBy("creationDate", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(ownerChats -> {
                                for (DocumentSnapshot doc : ownerChats.getDocuments()) {
                                    ChatItem chatItem = doc.toObject(ChatItem.class);
                                    if (chatItem != null && loadedChatIds.add(doc.getId())) {
                                        chatItem.setChatId(doc.getId());
                                        chatList.add(chatItem);
                                    }
                                }
                                // Notify adapter after both queries have completed
                                chatListAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                // Handle any errors retrieving owner chats
                                Log.e("FirestoreError", "Error fetching owner chats", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors retrieving client chats
                    Log.e("FirestoreError", "Error fetching client chats", e);
                });
    }
}
