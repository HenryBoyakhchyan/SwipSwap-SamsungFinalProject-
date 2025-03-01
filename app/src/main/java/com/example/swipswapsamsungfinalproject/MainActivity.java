package com.example.swipswapsamsungfinalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//    private EditText searchBox;
//    private RecyclerView cardRecyclerView;
//    private CardAdapter cardAdapter;
//    private List<ItemCard> cardList;
//    private List<ItemCard> allCards;
//    private SharedPreferences sharedPreferences;
//    private DatabaseHelper databaseHelper;
//
//    @SuppressLint("WrongViewCast")

    private ImageView plus, chat, user;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();


//        startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));

        plus = findViewById(R.id.plus);
        chat = findViewById(R.id.chat);
        user = findViewById(R.id.user);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OverallChatActivity.class));
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });


//
//        searchBox = findViewById(R.id.searchBar);
//        cardRecyclerView = findViewById(R.id.cardContainer);
//        sharedPreferences = getSharedPreferences("SwipedCards", MODE_PRIVATE);
//        databaseHelper = new DatabaseHelper(this);
//
//        loadCards();
//        setupRecyclerView();
//
//        searchBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filterCards(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
    }
//
//    private void loadCards() {
//        allCards = databaseHelper.getAllItems();
//        cardList = new ArrayList<>(allCards);
//    }
//
//    private void setupRecyclerView() {
//        cardAdapter = new CardAdapter(cardList, this::onSwipeLeft, this::onSwipeRight);
//        cardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        cardRecyclerView.setAdapter(cardAdapter);
//    }
//
//    private void filterCards(String query) {
//        List<ItemCard> filteredList = new ArrayList<>();
//        for (ItemCard card : allCards) {
//            if (String.valueOf(card.getId()).contains(query)) {
//                filteredList.add(card);
//            }
//        }
//        cardAdapter.updateList(filteredList);
//    }
//
//    private void onSwipeLeft(ItemCard card) {
//        sharedPreferences.edit().putBoolean("swiped_" + card.getId(), true).apply();
//        cardList.remove(card);
//        cardAdapter.notifyDataSetChanged();
//    }
//
//    private void onSwipeRight(ItemCard card) {
//        CartManager.getInstance().addToCart(card);
//        cardList.remove(card);
//        cardAdapter.notifyDataSetChanged();
//    }
}
