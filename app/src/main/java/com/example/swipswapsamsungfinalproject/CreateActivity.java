package com.example.swipswapsamsungfinalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView itemImage,btnUploadImage, home, user, chat;
    private EditText etTag, etDescription, etAddress;
    private Button  publishButton;
    private Uri imageUri;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("swap_items");

        // UI References
        itemImage = findViewById(R.id.upload_image_button);
        etTag = findViewById(R.id.tag);
        etDescription = findViewById(R.id.description);
        etAddress = findViewById(R.id.address);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        publishButton = findViewById(R.id.publish);

        // Bottom Navigation Buttons
        home = findViewById(R.id.home);
        chat = findViewById(R.id.chat);
      //  user = findViewById(R.id.user);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Upload Image Button Click
        btnUploadImage.setOnClickListener(v -> openFileChooser()); // Set click listener

        // Publish Button Click
        publishButton.setOnClickListener(v -> saveSwapItem());

        // Navigation Buttons Click
        home.setOnClickListener(view -> startActivity(new Intent(CreateActivity.this, MainActivity.class)));
        chat.setOnClickListener(view -> startActivity(new Intent(CreateActivity.this, OverallChatActivity.class)));
    }

    // Open File Chooser to Pick Image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            btnUploadImage.setImageURI(imageUri);
        }
    }

    // Save Swap Item to Firestore
    private void saveSwapItem() {
        String tag = etTag.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
       // String status = statusSpinner.getSelectedItem().toString();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tag.isEmpty() || description.isEmpty() || address.isEmpty() || imageUri == null) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        try {
            // Convert Image URI to Base64 String
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            String base64Image = encodeToBase64(bitmap);

            // Save Swap Item Data to Firestore
            Map<String, Object> swapItem = new HashMap<>();
            swapItem.put("imageBlob", base64Image);
            swapItem.put("tag", tag);
            swapItem.put("email", user.getEmail());
            swapItem.put("description", description);
            swapItem.put("address", address);
            swapItem.put("status", "published");

            db.collection("swap_items").add(swapItem)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Image Processing Failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Convert Bitmap to Base64 String
    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
