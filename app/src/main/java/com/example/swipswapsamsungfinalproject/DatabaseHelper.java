package com.example.swipswapsamsungfinalproject;

import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public DatabaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    // Register User
    public void registerUser(String username, String email, String password, OnUserRegisterListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get user ID from Firebase Authentication
                        String userId = mAuth.getCurrentUser().getUid();

                        // Create user object
                        User newUser = new User(userId, username, email);

                        // Store user in Firebase Realtime Database
                        mDatabase.child(userId).setValue(newUser)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        listener.onSuccess("User registered successfully!");
                                    } else {
                                        listener.onFailure(dbTask.getException().getMessage());
                                    }
                                });
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    // User Login
    public void loginUser(String email, String password, OnUserLoginListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess("Login successful!");
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Reset Password
    public void resetPassword(String email, OnPasswordResetListener listener) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess("Password reset email sent.");
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Logout
    public void logoutUser() {
        mAuth.signOut();
    }

    // Callback Interfaces
    public interface OnUserRegisterListener {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public interface OnUserLoginListener {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public interface OnPasswordResetListener {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }
}
