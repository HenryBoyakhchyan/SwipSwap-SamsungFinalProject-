package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnResetPassword;
    private TextView tvSignInLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvSignInLink = findViewById(R.id.tvSignInLink);

        mAuth = FirebaseAuth.getInstance();

        tvSignInLink.setOnClickListener(v ->
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class))
        );

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Enter your email!", Toast.LENGTH_SHORT).show();

                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Reset link sent to " + email,
                                    Toast.LENGTH_LONG).show();
                            new android.os.Handler().postDelayed(() -> {
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }, 1000);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Failed to send reset email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
