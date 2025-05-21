package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yuyakaido.android.cardstackview.*;

import java.util.*;

public class MainActivity extends AppCompatActivity implements CardStackListener {
    private ImageView plus, chat, user;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private List<ItemCard> itemList;
    private List<ItemCard> allItems = new ArrayList<>();

    private SwipeItemAdapter adapter;
    private CardStackView cardStackView;
    private CardStackLayoutManager layoutManager;
    private EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        plus = findViewById(R.id.plus);
        chat = findViewById(R.id.chat);
        user = findViewById(R.id.user);
        cardStackView = findViewById(R.id.card_stack_view);

        itemList = new ArrayList<>();
        adapter = new SwipeItemAdapter(this, itemList);
        layoutManager = new CardStackLayoutManager(this, this);
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);

        searchBox = findViewById(R.id.searchBox);


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        fetchSwapItems();

        plus.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, CreateActivity.class)));

        chat.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, ChatActivity.class)));

        user.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, UserActivity.class)));
    }
    private void filterItems(String query) {
       if(allItems.isEmpty()){
           allItems.addAll(itemList);
       }


        if (query.isEmpty()) {
            itemList.clear();
            itemList.addAll(allItems);
        } else {
            List<ItemCard> filtered = new ArrayList<>();
            String lowerQuery = query.toLowerCase();

            for (ItemCard item : allItems) {
                boolean matchesDescription = item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery);
                boolean matchesAddress = item.getAddress() != null && item.getAddress().toLowerCase().contains(lowerQuery);
                boolean matchesCategory = item.getCategories() != null && item.getCategories().stream().anyMatch(cat -> cat.toLowerCase().contains(lowerQuery));

                if (matchesDescription || matchesAddress || matchesCategory) {
                    filtered.add(item);
                }
            }

            itemList.clear();
            itemList.addAll(filtered);
        }

        adapter.notifyDataSetChanged();
    }

    private void fetchSwapItems() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || currentUser.getUid() == null) {
            Log.e("FirestoreDebug", "User not authenticated.");
            return;
        }

        String currentUserId = currentUser.getUid();
//
        db.collection("swap_items")
                .whereNotEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    itemList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        ItemCard item = doc.toObject(ItemCard.class);
                        if (!item.getStatus().equals("accepted") && !item.getStatus().equals("given") &&
                                (item.getChosenByUserIds() == null || !item.getChosenByUserIds().contains(currentUserId))) {
                            itemList.add(item);
                        }
                    }

                    Collections.sort(itemList, (i1, i2) -> i2.getPublishedDate().compareTo(i1.getPublishedDate()));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e("FirestoreError", "Failed to fetch swap items", e));

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (itemList.isEmpty()) return;

        ItemCard swipedItem = itemList.get(0);
        FirebaseUser currentUser = auth.getCurrentUser();

        if (direction == Direction.Right && currentUser != null) {
            recordUserSelection(swipedItem, currentUser);
            Toast.makeText(this, "Item Chosen & Chat Created!", Toast.LENGTH_SHORT).show();
        } else if (direction == Direction.Left) {
            Toast.makeText(this, "Item Skipped", Toast.LENGTH_SHORT).show();
        }

        itemList.remove(0);
        adapter.notifyDataSetChanged();
    }

    private void recordUserSelection(ItemCard swipedItem, FirebaseUser currentUser) {
        String currentUserId = currentUser.getUid();
        String userEmail = currentUser.getEmail();

        Map<String, Object> selectionData = new HashMap<>();
        selectionData.put("swapId", swipedItem.getSwapId());
        selectionData.put("userId", currentUserId);
        selectionData.put("userEmail", userEmail);
        selectionData.put("selectedDate", new Date());
        selectionData.put("status", "chosen");

        db.collection("user_selections").add(selectionData)
                .addOnSuccessListener(docRef -> Log.d("Firestore", "Selection recorded"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Selection recording failed", e));

        updateSwapItemChosenBy(swipedItem, currentUserId);
        createChat(swipedItem, currentUser);
        allItems.remove(swipedItem);
    }

    private void updateSwapItemChosenBy(ItemCard item, String userId) {
        List<String> chosenBy = item.getChosenByUserIds() == null ?
                new ArrayList<>() : item.getChosenByUserIds();
        chosenBy.add(userId);

        db.collection("swap_items")
                .whereEqualTo("swapId", item.getSwapId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        doc.getReference()
                                .update("chosenByUserIds", chosenBy, "status", "chosen")
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "SwapItem updated"))
                                .addOnFailureListener(e -> Log.e("FirestoreError", "SwapItem update failed", e));
                    } else {
                        Log.e("Firestore", "No swap_item found with swapId = " + item.getSwapId());
                    }
                });
    }
    private void createChat(ItemCard swipedItem, FirebaseUser currentUser) {
        String chatId = swipedItem.getSwapId() + "_" + currentUser.getUid();

        Map<String, Object> chatData = new HashMap<>();
        chatData.put("chatId", chatId);
        chatData.put("swapId", swipedItem.getSwapId());
        chatData.put("swapOwnerId", swipedItem.getUserId());
        chatData.put("swapOwnerEmail", swipedItem.getEmail());
        chatData.put("clientUserId", currentUser.getUid());
        chatData.put("clientUserEmail", currentUser.getEmail());
        chatData.put("creationDate", FieldValue.serverTimestamp());
        chatData.put("lastMessage", "");
        chatData.put("lastMessageTimestamp", FieldValue.serverTimestamp());
        chatData.put("status", "chosen");
        chatData.put("unreadCounts", 0);

        chatData.put("swapItemImageBlob", swipedItem.getImageBlob());
        chatData.put("swapItemDescription", swipedItem.getDescription());
        chatData.put("swapItemAddress", swipedItem.getAddress());

        db.collection("chats").document(chatId).set(chatData)
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "Chat created successfully"))
                .addOnFailureListener(e ->
                        Log.e("FirestoreError", "Chat creation failed", e));
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) { }

    @Override
    public void onCardRewound() {
        Toast.makeText(this, "Card Rewound", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardCanceled() {
        Toast.makeText(this, "Swipe Canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardAppeared(View view, int position) { }

    @Override
    public void onCardDisappeared(View view, int position) { }
}
