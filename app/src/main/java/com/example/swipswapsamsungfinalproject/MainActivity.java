package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yuyakaido.android.cardstackview.*;

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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStackListener {
    private ImageView plus, chat, user;
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private List<ItemCard> itemList;
    private SwipeItemAdapter adapter;
    private CardStackView cardStackView;
    private CardStackLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("SwapItems");

        plus = findViewById(R.id.plus);
        chat = findViewById(R.id.chat);
        user = findViewById(R.id.user);
        cardStackView = findViewById(R.id.card_stack_view);

        fetchSwapItems();

        itemList = new ArrayList<>();
        adapter = new SwipeItemAdapter(this, itemList);

        layoutManager = new CardStackLayoutManager(this, this);
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);


        plus.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CreateActivity.class)));
        chat.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, OverallChatActivity.class)));
        user.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UserActivity.class)));
    }

    private void fetchSwapItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("FirestoreDebug", "User not logged in.");
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Log.e("FirestoreDebug", "User email is null, cannot fetch items.");
            return;
        }

        Log.d("FirestoreDebug", "Fetching items excluding email: " + userEmail);

        db.collection("swap_items")
                .whereNotEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                ItemCard item = document.toObject(ItemCard.class);

                                // Ensure item is not trashed or given
                                if (!"trashed".equals(item.getStatus()) && !"given".equals(item.getStatus())) {
                                    itemList.add(item);
                                    Log.d("FirestoreDebug", "Added item: " + item.getDescription());
                                }
                            } catch (Exception e) {
                                Log.e("FirestoreError", "Failed to parse item", e);
                            }
                        }
                        Collections.reverse(itemList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreDebug", "Query failed", task.getException());
                    }
                });
    }



    private void markAsChosen(ItemCard item) {
        DatabaseReference itemRef = databaseRef.child(item.getDescription());
        itemRef.child("status").setValue("chosen");
    }

    // ✅ Implementing required CardStackListener methods
    @Override
    public void onCardDragging(Direction direction, float ratio) {
        // Optional: Handle dragging feedback (e.g., change UI)
    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Right) {
            Toast.makeText(this, "Item Liked!", Toast.LENGTH_SHORT).show();
        } else if (direction == Direction.Left) {
            Toast.makeText(this, "Item Skipped!", Toast.LENGTH_SHORT).show();
        }

        // Remove swiped card
        if (!itemList.isEmpty()) {
            itemList.remove(0);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCardRewound() {
        Toast.makeText(this, "Card Rewound", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardCanceled() {
        Toast.makeText(this, "Swipe Canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardAppeared(View view, int position) {}

    @Override
    public void onCardDisappeared(View view, int position) {}
}
