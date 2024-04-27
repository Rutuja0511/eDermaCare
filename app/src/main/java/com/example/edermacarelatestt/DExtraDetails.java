package com.example.edermacarelatestt;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class DExtraDetails extends AppCompatActivity {

    private EditText clinicAddr1, clinicAddr2, clinicAddr3;
    private TextView startTime, endTime;
    private Button saveButton, startTimeButton, endTimeButton;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private int mHour, mMinute;

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
        clinicAddr3 = findViewById(R.id.dermatologist_clinic_addr_3);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        startTimeButton = findViewById(R.id.start_time_button);
        endTimeButton = findViewById(R.id.end_time_button);
        saveButton = findViewById(R.id.search);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(clinicAddr1.getText()) || TextUtils.isEmpty(clinicAddr2.getText()) || TextUtils.isEmpty(clinicAddr3.getText()) || TextUtils.isEmpty(startTime.getText()) || TextUtils.isEmpty(endTime.getText())) {
                    // Display message if any EditText field is empty
                    Toast.makeText(DExtraDetails.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save data to Firestore
                    saveDataToFirestore(userId);
                }
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show time picker dialog for start time
                showTimePickerDialog(startTime);
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show time picker dialog for end time
                showTimePickerDialog(endTime);
            }
        });
    }

    private void saveDataToFirestore(String userId) {
        // Get entered data
        String addr1 = clinicAddr1.getText().toString().trim();
        String addr2 = clinicAddr2.getText().toString().trim();
        String addr3 = clinicAddr3.getText().toString().trim();
        String start = startTime.getText().toString().trim();
        String end = endTime.getText().toString().trim();

        // Create a new document in the "dermatologist" collection with the user's ID
        mFirestore.collection("dermatologist").document(userId).collection("address")
                .add(new ClinicDetails(addr1, addr2, addr3, start, end))
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                    // You can show a success message or do something else
                    Toast.makeText(DExtraDetails.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(DExtraDetails.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                });
    }

    private void showTimePickerDialog(final TextView timeTextView) {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        // Launch time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(DExtraDetails.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update TextView with selected time
                        timeTextView.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
