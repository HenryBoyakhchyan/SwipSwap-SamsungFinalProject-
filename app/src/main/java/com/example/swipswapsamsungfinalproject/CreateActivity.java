package com.example.swipswapsamsungfinalproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView btnUploadImage, home, chat, user;
    private EditText etDescription, etAddress;
    private Button publishButton;
    private Uri imageUri;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    private List<String> categoryNames;
    private TextView categorySelector;
    private List<String> allCategories = new ArrayList<>();
    private boolean[] selectedItems;
    private List<String> selectedCategories = new ArrayList<>();
    private boolean editMode = false;
    private long editingSwapId = -1;
    private String existingImageBlob = null;
    private String editingDocumentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnUploadImage = findViewById(R.id.btnUploadImage);
        etDescription = findViewById(R.id.description);
        etAddress = findViewById(R.id.address);
        publishButton = findViewById(R.id.publish);
        categorySelector = findViewById(R.id.categorySelector);
        home = findViewById(R.id.home);
        chat = findViewById(R.id.chat);
        user = findViewById(R.id.user);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        btnUploadImage.setOnClickListener(v -> openFileChooser());
        publishButton.setOnClickListener(v -> saveSwapItem());

        home.setOnClickListener(view -> startActivity(new Intent(CreateActivity.this, MainActivity.class)));
        chat.setOnClickListener(view -> startActivity(new Intent(CreateActivity.this, ChatActivity.class)));
        user.setOnClickListener(view -> startActivity(new Intent(CreateActivity.this, UserActivity.class)));

        fetchCategoriesFromDB();
        categorySelector.setOnClickListener(v -> showCategoryDialog());

        // Edit Mode Handling
        editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {
            editingSwapId = getIntent().getLongExtra("swapId", -1);
            if (editingSwapId != -1) {
                loadSwapItemData(editingSwapId);
            }
        }

    }

    private void loadSwapItemData(long swapId) {
        db.collection("swap_items")
                .whereEqualTo("swapId", swapId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    if (!querySnapshots.isEmpty()) {
                        DocumentSnapshot doc = querySnapshots.getDocuments().get(0);
                        editingDocumentId = doc.getId(); // Save document ID
                        etDescription.setText(doc.getString("description"));
                        etAddress.setText(doc.getString("address"));

                        List<String> categories = (List<String>) doc.get("categories");
                        if (categories != null) {
                            selectedCategories.clear();
                            selectedCategories.addAll(categories);
                            categorySelector.setText(String.join(", ", selectedCategories));
                        }

                        existingImageBlob = doc.getString("imageBlob"); // Save base64 image
                        if (existingImageBlob != null && !existingImageBlob.isEmpty()) {
                            byte[] decodedBytes = Base64.decode(existingImageBlob, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            btnUploadImage.setImageBitmap(bitmap);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load item data", Toast.LENGTH_SHORT).show());
    }


    private void fetchCategoriesFromDB() {
        db.collection("swap_category")
                .orderBy("displayOrder", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    allCategories.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        if (name != null) allCategories.add(name);
                    }
                    selectedItems = new boolean[allCategories.size()];
                });
    }
    private void showCategoryDialog() {
        String[] categoriesArray = allCategories.toArray(new String[0]);

        // Update selectedItems based on selectedCategories before showing the dialog
        for (int i = 0; i < allCategories.size(); i++) {
            selectedItems[i] = selectedCategories.contains(allCategories.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Categories")
                .setMultiChoiceItems(categoriesArray, selectedItems, (dialog, indexSelected, isChecked) -> {
                    String selectedCategory = allCategories.get(indexSelected);
                    if (isChecked) {
                        if (!selectedCategories.contains(selectedCategory)) {
                            selectedCategories.add(selectedCategory);
                        }
                    } else {
                        selectedCategories.remove(selectedCategory);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    categorySelector.setText(String.join(", ", selectedCategories));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            btnUploadImage.setImageURI(imageUri);
        }
    }

    private void saveSwapItem() {
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (description.isEmpty() || address.isEmpty() || (!editMode && imageUri == null)) {
            Toast.makeText(this, "All fields and image are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Select at least one category.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Runnable saveAction = () -> {
            try {
                String imageToSave;
                if (imageUri != null) {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageToSave = encodeToBase64(bitmap);
                } else {
                    imageToSave = existingImageBlob; // Reuse old image
                }

                Map<String, Object> swapItem = new HashMap<>();
                swapItem.put("imageBlob", imageToSave);
                swapItem.put("categories", selectedCategories);
                swapItem.put("email", auth.getCurrentUser().getEmail());
                swapItem.put("userId", auth.getCurrentUser().getUid());
                swapItem.put("description", description);
                swapItem.put("address", address);
                swapItem.put("status", "published");
                swapItem.put("publishedDate", new Date());
                swapItem.put("chosenByUserIds", new ArrayList<>());

                if (editMode && editingDocumentId != null) {
                    // Only update fields (don't change swapId)
                    db.collection("swap_items").document(editingDocumentId)
                            .update(swapItem)
                            .addOnSuccessListener(unused -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Item updated!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Generate new swapId and create document
                    db.collection("swap_items")
                            .orderBy("swapId", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                int nextSwapId = 1;
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Long lastSwapId = queryDocumentSnapshots.getDocuments().get(0).getLong("swapId");
                                    if (lastSwapId != null) nextSwapId = lastSwapId.intValue() + 1;
                                }

                                swapItem.put("swapId", nextSwapId);

                                db.collection("swap_items").add(swapItem)
                                        .addOnSuccessListener(documentReference -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(this, "Add failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            });
                }
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Image processing failed", Toast.LENGTH_SHORT).show();
            }
        };

        new Thread(saveAction).start();
    }

    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
