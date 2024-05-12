package com.example.edermacarelatestt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class profileFragment2 extends AppCompatActivity {

    private TextView  editMembershipNo ;
    private EditText editDocname,  editPhoneNo, editEmail, editCity, editState, editExperience;
    private FloatingActionButton changeImageButton;
    private ImageView doctorImage;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile2);
        Button saveButton = findViewById(R.id.submitButton);
        saveButton.setOnClickListener(v -> saveUserData());
        changeImageButton = findViewById(R.id.changeImageButton);
        doctorImage = findViewById(R.id.doctorImage);

        // Initialize the ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    // Handle the returned Uri here
                    if (uri != null) {
                        // Resize and set the image
                        resizeAndSetImage(uri);
                    }
                });

        // Set click listener for changeImageButton
        changeImageButton.setOnClickListener(v -> {
            // Launch image picker activity using ActivityResultLauncher
            imagePickerLauncher.launch("image/*");
        });

        // Initialize UI elements
        editDocname = findViewById(R.id.editName);
        editMembershipNo = findViewById(R.id.editMembershipNo);
        editEmail = findViewById(R.id.editEmail);
        editPhoneNo = findViewById(R.id.editPhoneNo);
        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);
        editExperience = findViewById(R.id.editExperience);

        // Retrieve user email from Intent extras
        String user_email = getIntent().getStringExtra("user_email");
        fetchUserData(user_email);
    }
    // Inside onCreate() method


    // Add the saveUserData() method
    private void saveUserData() {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve user email from Intent extras
        String userEmail = getIntent().getStringExtra("user_email");

        // Initialize the map for user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", editDocname.getText().toString());
        userData.put("RegID", editMembershipNo.getText().toString());
        userData.put("Email", editEmail.getText().toString());
        userData.put("Mobile", editPhoneNo.getText().toString());
        userData.put("City", editCity.getText().toString());
        userData.put("State", editState.getText().toString());
        userData.put("Exp", editExperience.getText().toString());

        // Query to get the document reference
        db.collection("dermatologist")
                .whereEqualTo("Email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if query returned any documents
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the document reference
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();

                        // Update the document in Firestore
                        db.collection("dermatologist").document(documentId).update(userData)
                                .addOnSuccessListener(aVoid -> {
                                    // Document updated successfully
                                    Toast.makeText(profileFragment2.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Failed to update document
                                    Toast.makeText(profileFragment2.this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                                    Log.e("TAG", "Error updating document", e);
                                });
                    } else {
                        // Document not found
                        Toast.makeText(profileFragment2.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to retrieve document
                    Toast.makeText(profileFragment2.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Error retrieving document", e);
                });
    }



    private void fetchUserData(String userEmail) {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to get user data based on email
        Query query = db.collection("dermatologist").whereEqualTo("Email", userEmail);

        // Perform the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve data from Firestore document
                    String name = document.getString("Name");
                    String email = document.getString("Email");
                    String memID = document.getString("RegID");
                    String mobile = document.getString("Mobile");
                    String city = document.getString("City");
                    String state = document.getString("State");
                    String exp = document.getString("Exp");
                    String image = document.getString("ImageURL");

                    // Update UI with retrieved data
                    editDocname.setText(name);
                    editMembershipNo.setText(memID);
                    editEmail.setText(email);
                    editPhoneNo.setText(mobile);
                    editCity.setText(city);
                    editState.setText(state);
                    editExperience.setText(exp);

                    String imageURL = document.getString("ImageURL");
                    System.out.println("drkimage"+imageURL);
                    if (imageURL != null && !imageURL.isEmpty()) {
                        // Clear any existing image before loading the new one
                        Glide.with(profileFragment2.this).clear(doctorImage);

                        // Load image using Glide with specified dimensions
                        Glide.with(profileFragment2.this)
                                .load(imageURL)
                                .transform(new CircleCrop())
                                .override(doctorImage.getWidth(), doctorImage.getHeight())
                                .into(doctorImage);
                    } else {
                        doctorImage.setImageResource(R.drawable.doctor_image);
                    }
                }
            } else {
                // Handle errors
                Toast.makeText(profileFragment2.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resizeAndSetImage(Uri uri) {
            try {
                // Decode the URI into a Bitmap
                Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                // Resize the originalBitmap to match the target dimensions
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true);

                // Create a circular bitmap
                Bitmap circularBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(circularBitmap);
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, 100, 100);
                RectF rectF = new RectF(rect);
                float radius = Math.min(rectF.width(), rectF.height()) / 2f;

                // Draw a circle on the canvas
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(0xFF000000);
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, paint);

                // Apply the circular mask
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(resizedBitmap, rect, rect, paint);

                // Set the circularBitmap to doctorImage
                doctorImage.setImageBitmap(circularBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
