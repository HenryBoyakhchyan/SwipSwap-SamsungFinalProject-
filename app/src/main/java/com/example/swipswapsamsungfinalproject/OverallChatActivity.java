package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class OverallChatActivity extends AppCompatActivity {

    private ImageView home, plus, user, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        plus = findViewById(R.id.plus);
        home = findViewById(R.id.home);
        user = findViewById(R.id.user);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OverallChatActivity.this, CreateActivity.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OverallChatActivity.this, MainActivity.class));
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OverallChatActivity.this, UserActivity.class));
            }
        });

    }
}
