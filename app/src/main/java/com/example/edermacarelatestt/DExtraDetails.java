package com.example.edermacarelatestt;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DExtraDetails extends AppCompatActivity {

    private EditText clinicAddr1, clinicAddr2;
    private TextView startTime, endTime, startTime2, endTime2;
    private Button saveButton, startTimeButton, endTimeButton, startTimeButton2, endTimeButton2;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dermatologist_extra_details);

        // Initialize Firebase Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Retrieve user ID from intent extras
        String userId = getIntent().getStringExtra("user_id");

        clinicAddr1 = findViewById(R.id.dermatologist_clinic_addr_1);
        clinicAddr2 = findViewById(R.id.dermatologist_clinic_addr_2);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        startTime2 = findViewById(R.id.start_time2);
        endTime2 = findViewById(R.id.end_time2);
        startTimeButton = findViewById(R.id.start_time_button);
        endTimeButton = findViewById(R.id.end_time_button);
        startTimeButton2 = findViewById(R.id.start_time_button2);
        endTimeButton2 = findViewById(R.id.end_time_button2);
        saveButton = findViewById(R.id.search);

        // Load data from Firestore and display in EditText fields
        loadClinicData(userId);

        // Set click listeners for time picker buttons
        setOnClickListenersForTimePickerButtons();

        // Set click listener for save button
        saveButton.setOnClickListener(v -> {
            // Get edited data from EditText fields
            String editedAddr1 = clinicAddr1.getText().toString().trim();
            String editedAddr2 = clinicAddr2.getText().toString().trim();
            String editedStart = startTime.getText().toString().trim();
            String editedEnd = endTime.getText().toString().trim();
            String editedStart2 = startTime2.getText().toString().trim();
            String editedEnd2 = endTime2.getText().toString().trim();

            // Update data in Firestore
            updateClinicData(userId, editedAddr1, editedAddr2, editedStart, editedEnd, editedStart2, editedEnd2);
        });
    }

    private void loadClinicData(String userId) {
        // Retrieve clinic data from Firestore for clinicAddress1
        DocumentReference docRef1 = mFirestore.collection("dermatologist").document(userId).collection("address").document("clinicAddress1");
        docRef1.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Populate EditText fields with clinic data
                clinicAddr1.setText(documentSnapshot.getString("name"));
                startTime.setText(documentSnapshot.getString("start"));
                endTime.setText(documentSnapshot.getString("end"));
            } else {
                // Display message to the user if clinic details don't exist
                Toast.makeText(DExtraDetails.this, "Clinic details for Address 1 don't exist. Please fill these and click on save.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(DExtraDetails.this, "Failed to load clinic data", Toast.LENGTH_SHORT).show();
        });

        // Retrieve clinic data from Firestore for clinicAddress2
        DocumentReference docRef2 = mFirestore.collection("dermatologist").document(userId).collection("address").document("clinicAddress2");
        docRef2.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Populate EditText fields with clinic data
                clinicAddr2.setText(documentSnapshot.getString("name"));
                startTime2.setText(documentSnapshot.getString("start"));
                endTime2.setText(documentSnapshot.getString("end"));
            } else {
                // Display message to the user if clinic details don't exist
                Toast.makeText(DExtraDetails.this, "Clinic details for Address 2 don't exist. Please fill these and click on save.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(DExtraDetails.this, "Failed to load clinic data", Toast.LENGTH_SHORT).show();
        });
    }


    private void setOnClickListenersForTimePickerButtons() {
        startTimeButton.setOnClickListener(v -> showTimePickerDialog(startTime));
        endTimeButton.setOnClickListener(v -> showTimePickerDialog(endTime));
        startTimeButton2.setOnClickListener(v -> showTimePickerDialog(startTime2));
        endTimeButton2.setOnClickListener(v -> showTimePickerDialog(endTime2));
    }

    private void showTimePickerDialog(final TextView timeTextView) {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        // Launch time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(DExtraDetails.this,
                (view, hourOfDay, minute) -> {
                    // Update TextView with selected time
                    timeTextView.setText(String.format("%02d:%02d", hourOfDay, minute));
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void updateClinicData(String userId, String editedAddr1, String editedAddr2, String editedStart, String editedEnd, String editedStart2, String editedEnd2) {
        // Update clinic data in Firestore for clinicAddress1
        DocumentReference docRef1 = mFirestore.collection("dermatologist").document(userId).collection("address").document("clinicAddress1");
        Map<String, Object> clinic1Data = new HashMap<>();
        clinic1Data.put("name", editedAddr1);
        clinic1Data.put("start", editedStart);
        clinic1Data.put("end", editedEnd);
        docRef1.set(clinic1Data)
                .addOnSuccessListener(aVoid -> {
                    // Data updated successfully for Address 1
                    Toast.makeText(DExtraDetails.this, "Data updated successfully for Clinic Address 1", Toast.LENGTH_SHORT).show();
                    // Check if Address 2 is empty
                    if (TextUtils.isEmpty(editedAddr2)) {
                        // Finish the activity since Address 2 is empty
                        finish();
                    } else {
                        // Update clinic data in Firestore for clinicAddress2
                        updateAddress2(userId, editedAddr2, editedStart2, editedEnd2);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(DExtraDetails.this, "Failed to update data for Clinic Address 1", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAddress2(String userId, String editedAddr2, String editedStart2, String editedEnd2) {
        // Update clinic data in Firestore for clinicAddress2
        DocumentReference docRef2 = mFirestore.collection("dermatologist").document(userId).collection("address").document("clinicAddress2");
        Map<String, Object> clinic2Data = new HashMap<>();
        clinic2Data.put("name", editedAddr2);
        clinic2Data.put("start", editedStart2);
        clinic2Data.put("end", editedEnd2);
        docRef2.set(clinic2Data)
                .addOnSuccessListener(aVoid -> {
                    // Data updated successfully for Address 2
                    Toast.makeText(DExtraDetails.this, "Data updated successfully for Clinic Address 2", Toast.LENGTH_SHORT).show();
                    // Finish the activity since both Address 1 and Address 2 are saved
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(DExtraDetails.this, "Failed to update data for Clinic Address 2", Toast.LENGTH_SHORT).show();
                });
    }

}
