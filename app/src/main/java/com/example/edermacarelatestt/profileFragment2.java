package com.example.edermacarelatestt;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class profileFragment2 extends AppCompatActivity {

    private TextView editDocname, editMembershipNo, editPhoneNo, editEmail, editCity, editState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile2);

        // Initialize UI elements
        editDocname = findViewById(R.id.editName);
        editMembershipNo = findViewById(R.id.editMembershipNo);
        editEmail = findViewById(R.id.editEmail);
        editPhoneNo = findViewById(R.id.editPhoneNo);
        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);

        // Retrieve user email from Intent extras
        String userEmail = getIntent().getStringExtra("user_email");

        if (userEmail != null) {
            // Fetch user data from Firestore
            fetchUserData(userEmail);
        } else {
            // Handle the case where user_email is not found in Intent extras
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
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
                    System.out.println(state);

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
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
