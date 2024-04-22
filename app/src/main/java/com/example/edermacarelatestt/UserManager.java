package com.example.edermacarelatestt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {
    // Firebase authentication instance
    private FirebaseAuth mAuth;

    public UserManager() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Method to get the current user ID
    public String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null; // No user logged in
        }
    }
}
