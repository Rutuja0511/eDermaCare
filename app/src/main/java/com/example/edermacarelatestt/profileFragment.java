package com.example.edermacarelatestt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileFragment extends Fragment {

    private TextView textViewName;
    private TextView textViewDOB;
    private TextView textViewEmail;
    private TextView textViewGender;
    private TextView textViewMobile;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews
        textViewName = view.findViewById(R.id.editTextTextPersonName);
        textViewDOB = view.findViewById(R.id.editTextTextPersonName2);
        textViewEmail = view.findViewById(R.id.editTextTextPersonName3);
        textViewGender = view.findViewById(R.id.editTextTextPersonName4);
        textViewMobile = view.findViewById(R.id.editTextTextPersonName5);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("patients").child(currentUser.getUid());

        // Attach ValueEventListener to retrieve data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    // Retrieve data from dataSnapshot
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String dob = dataSnapshot.child("DOB").getValue(String.class);
                    String email = dataSnapshot.child("Email").getValue(String.class);
                    String gender = dataSnapshot.child("Gender").getValue(String.class);
                    String mobile = dataSnapshot.child("Mobile").getValue(String.class);

                    // Set data to TextViews
                    textViewName.setText(name);
                    textViewDOB.setText(dob);
                    textViewEmail.setText(email);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);
                } else {
                    // Display a toast message indicating no data is available
                    Toast.makeText(getContext(), "No data available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancelled event
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
