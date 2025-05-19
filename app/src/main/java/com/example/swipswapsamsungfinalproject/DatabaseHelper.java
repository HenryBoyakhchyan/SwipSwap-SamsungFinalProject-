package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public DatabaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Register User
    public void registerUser(String name, String email,
                             String address, String password, String profileImageBlob,
                             OnUserRegisterListener listener) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("userId", userId);
                        newUser.put("name", name);
                        newUser.put("email", email);
                        newUser.put("address", address);
                        newUser.put("profileImageBlob", profileImageBlob);
                        newUser.put("joinedDate", FieldValue.serverTimestamp());

                        db.collection("users").document(userId).set(newUser)
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
