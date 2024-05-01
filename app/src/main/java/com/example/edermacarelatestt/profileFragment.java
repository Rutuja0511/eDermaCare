package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class profileFragment extends Fragment {

    private TextView nameTextView, emailTextView, dobTextView, mobileTextView;

    Button goBackButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        nameTextView = view.findViewById(R.id.editTextTextPersonName);
        emailTextView = view.findViewById(R.id.editTextTextPersonName4);
        dobTextView = view.findViewById(R.id.editTextTextPersonName2);
        mobileTextView = view.findViewById(R.id.editTextTextPersonName3);
        // Retrieve user's email ID from arguments
        String userEmail = null;
        Bundle args = getArguments();
        if (args != null) {
            userEmail = args.getString("user_email");
        }

        if (userEmail != null) {
            // Fetch user data from Firestore
            fetchUserData(userEmail);
        } else {
            // Handle the case where user_email is not set in fragment arguments
            Toast.makeText(getContext(), "User email not found", Toast.LENGTH_SHORT).show();
        }

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


}
