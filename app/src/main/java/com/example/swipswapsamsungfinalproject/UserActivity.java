package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView userName, userAddress, userEmail;
    private RecyclerView recyclerViewUserItems;
    private ItemAdapter itemAdapter;
    private List<ItemCard> itemList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageView plus, chat, home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseFirestore.setLoggingEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userAddress = findViewById(R.id.userAddress);
        recyclerViewUserItems = findViewById(R.id.recyclerViewUserItems);
        plus = findViewById(R.id.plus);
        chat = findViewById(R.id.chat);
        home = findViewById(R.id.home);

        recyclerViewUserItems.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList);
        recyclerViewUserItems.setAdapter(itemAdapter);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            loadUserProfile();
            loadUserItems();
        } else {
            Log.e("UserActivity", "User not logged in!");
            userName.setText("Guest");
            userAddress.setText("Unknown");
            userEmail.setText("No Email");
        }

        // Navigation Click Listeners
        plus.setOnClickListener(view -> startActivity(new Intent(UserActivity.this, CreateActivity.class)));
        chat.setOnClickListener(view -> startActivity(new Intent(UserActivity.this, ChatActivity.class)));
        home.setOnClickListener(view -> startActivity(new Intent(UserActivity.this, MainActivity.class)));
    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        userEmail.setText(user.getEmail());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImage").getValue(String.class);

                    userName.setText(name != null ? name : "Unknown");
                    userAddress.setText(address != null ? address : "No Address");

                    Glide.with(UserActivity.this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseDatabase", "Error loading profile", error.toException());
            }
        });
    }

    private void loadUserItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Log.e("FirestoreDebug", "User email is null, cannot fetch items.");
            return;
        }

        Log.d("FirestoreDebug", "Fetching items for email: " + userEmail);

        db.collection("swap_items")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                ItemCard item = document.toObject(ItemCard.class);
                                itemList.add(item);
                            } catch (Exception e) {
                                Log.e("FirestoreError", "Failed to parse item", e);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreDebug", "Query failed", task.getException());
                    }
                });
    }
}
