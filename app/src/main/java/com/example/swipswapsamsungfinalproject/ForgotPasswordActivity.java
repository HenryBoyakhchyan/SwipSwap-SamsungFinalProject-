package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ForgotPasswordActivity extends AppCompatActivity {
    private AuthenticationHelper authHelper;
    private EditText etEmail;
    private Button btnResetPassword;
    private TextView btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authHelper = new AuthenticationHelper();
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnSignIn = findViewById(R.id.tvSignInLink);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Enter your email!", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthenticationHelper.resetPassword(email, ForgotPasswordActivity.this, new AuthenticationHelper.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
