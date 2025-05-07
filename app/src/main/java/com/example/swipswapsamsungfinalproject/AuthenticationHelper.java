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
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            callback.onSuccess("Verification email sent. Please check your inbox.");
                                        } else {
                                            callback.onFailure("Failed to send verification email.");
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure("Registration failed: " + task.getException().getMessage());
                    }
                });
    }

     // Sign In User
     public void signIn(String email, String password, Activity activity, AuthCallback callback) {
         mAuth.signInWithEmailAndPassword(email, password)
                 .addOnCompleteListener(activity, task -> {
                     if (task.isSuccessful()) {
                         FirebaseUser user = mAuth.getCurrentUser();
                         if (user != null && user.isEmailVerified()) {
                             callback.onSuccess("Login successful.");
                         } else {
                             mAuth.signOut();
                             callback.onFailure("Please verify your email before signing in.");
                         }
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
