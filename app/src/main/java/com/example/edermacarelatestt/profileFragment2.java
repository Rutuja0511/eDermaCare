package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class profileFragment2 extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);


        return view;

    }
//    private void fetchUserData(String userEmail) {
//        // Access Firestore instance
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Query to get user data based on email
//        Query query = db.collection("patients").whereEqualTo("Email", userEmail);
//
//        // Perform the query
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    // Retrieve data from Firestore document
//                    String name = document.getString("Name");
//                    String email = document.getString("Email");
//                    String dob = document.getString("DOB");
//                    String mobile = document.getString("Mobile");
//
//                    // Update UI with retrieved data
//                    nameTextView.setText(name);
//                    emailTextView.setText(email);
//                    dobTextView.setText(dob);
//                    mobileTextView.setText(mobile);
//                }
//            } else {
//                // Handle errors
//                Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}