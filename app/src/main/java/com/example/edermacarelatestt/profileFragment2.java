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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class profileFragment2 extends AppCompatActivity {

    private TextView editDocname, editMembershipNo, editPhoneNo, editEmail, editCity, editState, editExperience;
    private FloatingActionButton changeImageButton;
    private ImageView doctorImage;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile2);

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

                    // Load image using Glide
                    if (image != null && !image.isEmpty()) {
                        Glide.with(profileFragment2.this).load(image).into(doctorImage);
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
