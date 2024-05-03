package com.example.edermacarelatestt;

import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class profileFragment2 extends Fragment {

    private TextView editDocname,editMembershipNo,editPhoneNo,editEmail,editCity,editState;
    FloatingActionButton changeImageButton;
    ImageView doctorImage; // Make sure doctorImage is declared as ImageView

    ActivityResultLauncher<String> imagePickerLauncher;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile2, container, false);

        //Initialize ui elements
        editDocname = view.findViewById(R.id.editName);
        editMembershipNo = view.findViewById(R.id.editMembershipNo);
        editEmail = view.findViewById(R.id.editEmail);
        editPhoneNo = view.findViewById(R.id.editPhoneNo);
        editCity = view.findViewById(R.id.editCity);
        editState = view.findViewById(R.id.editState);
        String userEmail = null;
        Bundle args = getArguments();
        System.out.println(args+"profilefragment2");
        if (args != null) {
            userEmail = args.getString("user_email");
            System.out.println("profilefragment"+userEmail);
        }

        if (userEmail != null) {
            // Fetch user data from Firestore
            fetchUserData(userEmail);
        } else {
            // Handle the case where user_email is not set in fragment arguments
            Toast.makeText(getContext(), "User email not found", Toast.LENGTH_SHORT).show();
        }





        // Initialize views
        changeImageButton = view.findViewById(R.id.changeImageButton);
        doctorImage = view.findViewById(R.id.doctorImage); // Make sure doctorImage is initialized as ImageView

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

        return view;
    }

    private void resizeAndSetImage(Uri uri) {
        try {
            // Decode the URI into a Bitmap
            Bitmap originalBitmap = BitmapFactory.decodeStream(
                    requireActivity().getContentResolver().openInputStream(uri));

            // Calculate the target width and height based on doctorImage dimensions
            int targetWidth = doctorImage.getWidth();
            int targetHeight = doctorImage.getHeight();

            // Resize the originalBitmap to match the target dimensions
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

            // Create a circular bitmap
            Bitmap circularBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circularBitmap);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, targetWidth, targetHeight);
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

                    // Update UI with retrieved data

                    editDocname.setText(name);
                    editMembershipNo.setText(memID);
                    editEmail.setText(email);
                    editPhoneNo.setText(mobile);
                    editCity.setText(city);
                    editState.setText(state);
                }
            } else {
                // Handle errors
                Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}