package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class profileFragment extends Fragment {

    private EditText nameTextView, emailTextView, dobTextView, mobileTextView;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        nameTextView = view.findViewById(R.id.editTextTextPersonName);
        emailTextView = view.findViewById(R.id.editTextTextPersonName4);
        dobTextView = view.findViewById(R.id.editTextTextPersonName2);
        mobileTextView = view.findViewById(R.id.editTextTextPersonName3);
        saveButton = view.findViewById(R.id.saveButton);

        // Retrieve user's email ID from arguments
        String userEmail,userID;
        Bundle args = getArguments();
        if (args != null) {
            userEmail = args.getString("user_email");
            userID = args.getString("user_id");
        } else {
            userID = null;
            userEmail = null;
        }

        if (userEmail != null) {
            // Fetch user data from Firestore
            fetchUserData(userEmail);
        } else {
            // Handle the case where user_email is not set in fragment arguments
            Toast.makeText(getContext(), "User email not found", Toast.LENGTH_SHORT).show();
        }

        // Set OnClickListener to the save button
        saveButton.setOnClickListener(v -> saveUserData(userID));

        return view;
    }

    private void fetchUserData(String userEmail) {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to get user data based on email
        Query query = db.collection("patients").whereEqualTo("Email", userEmail);

        // Perform the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve data from Firestore document
                    String name = document.getString("Name");
                    String email = document.getString("Email");
                    String dob = document.getString("DOB");
                    String mobile = document.getString("Mobile");

                    // Update UI with retrieved data
                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    dobTextView.setText(dob);
                    mobileTextView.setText(mobile);
                }
            } else {
                // Handle errors
                Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String userID) {

        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get data from EditText fields
        String name = nameTextView.getText().toString().trim();
        String email = emailTextView.getText().toString().trim();
        String dob = dobTextView.getText().toString().trim();
        String mobile = mobileTextView.getText().toString().trim();

        // Create a map to update the document in Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", name);
        userData.put("Email", email);
        userData.put("DOB", dob);
        userData.put("Mobile", mobile);

        // Update the document in Firestore
        db.collection("patients").document(userID)
                .update(userData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User data updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update user data", Toast.LENGTH_SHORT).show());
    }
}
