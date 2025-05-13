package com.example.swipswapsamsungfinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private ImageView profileImage, home, plus, chat, user, editProfileButton;
    private TextView userName, userEmail, userAddress;
    private RecyclerView recyclerViewUserItems;
    private ItemAdapter itemAdapter;
    private List<ItemCard> itemList = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseFirestore firestoreDb;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static final String USERS_COLLECTION = "users";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();

        // Initialize Views
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userAddress = findViewById(R.id.userAddress);
        recyclerViewUserItems = findViewById(R.id.recyclerViewUserItems);
        plus = findViewById(R.id.plus);
        chat = findViewById(R.id.chat);
        home = findViewById(R.id.home);
        user = findViewById(R.id.user);
        editProfileButton = findViewById(R.id.edit_profile_button);

        // Setup RecyclerView
        itemAdapter = new ItemAdapter(this, itemList);
        recyclerViewUserItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserItems.setAdapter(itemAdapter);

        // Image Picker Launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(UserActivity.this)
                                .load(selectedImageUri) // or use Base64 converted to bitmap
                                .placeholder(R.drawable.profile_placeholder)
                                .circleCrop() // <- This makes it circular
                                .into(profileImage);
                    }
                });

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            loadUserProfile(currentUser.getUid());
            loadUserItems(currentUser.getEmail());
        }

        // Navigation
        home.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        plus.setOnClickListener(v -> startActivity(new Intent(this, CreateActivity.class)));
        chat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        user.setOnClickListener(v -> recreate());

        // Edit Button
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        profileImage.setOnClickListener(v -> showEditProfileDialog());

        TextView signOutLink = findViewById(R.id.signOutLink);
        signOutLink.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
    private void loadUserProfile(String userId) {
        firestoreDb.collection(USERS_COLLECTION).document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            userName.setText(user.getName());
                            userAddress.setText(user.getAddress());
                            userEmail.setText(user.getEmail());

                            String blob = user.getProfileImageBlob();
                            if (blob != null && !blob.isEmpty()) {
                                byte[] imageBytes = Base64.decode(blob, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                Glide.with(UserActivity.this)
                                        .asBitmap()
                                        .load(bitmap)
                                        .placeholder(R.drawable.profile_placeholder)
                                        .circleCrop()
                                        .into(profileImage);
                            } else {
                                Glide.with(UserActivity.this)
                                        .load(R.drawable.profile_placeholder)
                                        .circleCrop()
                                        .into(profileImage);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("UserActivity", "Failed to load user", e));
    }


    private void loadUserItems(String email) {
        firestoreDb.collection("swap_items")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    itemList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ItemCard item = doc.toObject(ItemCard.class);
                        itemList.add(item);
                    }
                    itemAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("UserActivity", "Failed to load items", e));
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 10);

        ImageView imagePreview = new ImageView(this);
        imagePreview.setImageDrawable(profileImage.getDrawable());
        imagePreview.setAdjustViewBounds(true);
        imagePreview.setMaxHeight(300);
        imagePreview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        EditText nameInput = new EditText(this);
        nameInput.setHint("Enter name");
        nameInput.setText(userName.getText().toString());

        EditText addressInput = new EditText(this);
        addressInput.setHint("Enter address");
        addressInput.setText(userAddress.getText().toString());

        layout.addView(imagePreview);
        layout.addView(nameInput);
        layout.addView(addressInput);
        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            FirebaseUser firebaseUser = auth.getCurrentUser();
            if (firebaseUser == null) return;

            String uid = firebaseUser.getUid();
            String name = nameInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String email = firebaseUser.getEmail();

            User user = new User();
            user.setUserId(uid);
            user.setName(name);
            user.setAddress(address);
            user.setEmail(email);
            user.setJoinedDate(Timestamp.now());

            if (selectedImageUri != null) {
                try {
                    Glide.with(UserActivity.this)
                            .load(selectedImageUri) // or use Base64 converted to bitmap
                            .placeholder(R.drawable.profile_placeholder)
                            .circleCrop() // <- This makes it circular
                            .into(profileImage);

                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    user.setProfileImageBlob(encodeToBase64(bitmap));
                } catch (Exception e) {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            firestoreDb.collection(USERS_COLLECTION).document(uid)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        loadUserProfile(uid);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }
}
