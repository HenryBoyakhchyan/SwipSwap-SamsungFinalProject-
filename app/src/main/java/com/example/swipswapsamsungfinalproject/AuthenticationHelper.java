package com.example.swipswapsamsungfinalproject;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationHelper {
    private final FirebaseAuth auth;
    private final DatabaseReference database;

    public AuthenticationHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
    }

    public interface AuthCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
    // Sign Up User
    public void signUp(String email, String password, String fullName, Activity activity, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullName", fullName);
                            userData.put("email", email);

                            database.child(userId).setValue(userData)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            callback.onSuccess("Registration successful!");
                                        } else {
                                            callback.onFailure("Database error: " + dbTask.getException().getMessage());
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure("Auth error: " + task.getException().getMessage());
                    }
                });
    }
    // Sign In User
    public void signIn(String email, String password, Context context, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Login successful!");
                    } else {
                        callback.onFailure("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    // Reset Password
    public static void resetPassword(String email, Context context, AuthCallback callback) {
//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        callback.onSuccess("Password reset email sent!");
//                    } else {
//                        callback.onFailure("Failed to send reset email: " + task.getException().getMessage());
//                    }
//                });
    }

    // Sign Out
    public void signOut() {
        auth.signOut();
    }


}
