package com.example.edermacarelatestt;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class clinicProfile extends AppCompatActivity {

    private static final String TAG = "clinicProfile";

    // Declare EditText fields
    private TextView editClinic1;
    private TextView editClinic2;
    private TextView editClinic3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_profile);
        Log.d(TAG, "in clinicProfile onCreate");

        // Initialize EditText fields
        editClinic1 = findViewById(R.id.editClinic1);
        editClinic2 = findViewById(R.id.editClinic2);
        editClinic3 = findViewById(R.id.editClinic3);

        String userID = getIntent().getStringExtra("user_id");
        Log.d(TAG, "User ID: " + userID);

        displayClinicDetails(userID);
    }

    private void displayClinicDetails(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "dermatologist" collection
        db.collection("dermatologist")
                .document(userID)
                .collection("address")
                .limit(1) // Limit to only retrieve the first document
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming there's only one document due to the limit
                        DocumentSnapshot addressDoc = queryDocumentSnapshots.getDocuments().get(0);
                        // Get clinic address fields from the document
                        String clinicAddr1 = addressDoc.getString("clinicAddr1");
                        String clinicAddr2 = addressDoc.getString("clinicAddr2");
                        String clinicAddr3 = addressDoc.getString("clinicAddr3");

                        // Set the retrieved values to EditText fields
                        editClinic1.setText(clinicAddr1);
                        editClinic2.setText(clinicAddr2);
                        editClinic3.setText(clinicAddr3);

                        // Additional log statement after finding the first document in the "address" collection
                        Log.d(TAG, "Found clinic address document: " + addressDoc.getId());
                    } else {
                        Log.d(TAG, "No address found for this dermatologist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting address:", e);
                });

        // Additional log statement after querying the "address" collection
        Log.d(TAG, "Querying address collection for clinic details...");
    }
}
