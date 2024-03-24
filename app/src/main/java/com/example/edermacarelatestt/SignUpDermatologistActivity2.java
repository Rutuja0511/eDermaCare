package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpDermatologistActivity2 extends AppCompatActivity {

    EditText editTextPasswordD,editTextExperience, editTextMobileNo, editTextCity, editTextDistrict, editTextState;

    Button buttonSignUpD;
    private FirebaseFirestore db;
    private String licenseUrl;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity2_signup_dermatologist);
        editTextPasswordD= findViewById(R.id.dermatologist_signup_password);
        editTextCity=findViewById(R.id.dermatologist_city);
        editTextDistrict=findViewById(R.id.dermatologist_district);
        editTextState=findViewById(R.id.dermatologist_state);
        editTextMobileNo=findViewById(R.id.dermatologist_mobileNo);
        editTextExperience=findViewById(R.id.dermatologist_experience);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("licenseUrl")) {
            licenseUrl = intent.getStringExtra("licenseUrl"); // Get the download URL
        }

        buttonSignUpD = findViewById(R.id.dermatologist_signup_button);


        buttonSignUpD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpDermatologist();
            }
        });
    }


    private void signUpDermatologist() {
        // Retrieve values from EditText fields
        String password = editTextPasswordD.getText().toString();
        String experience = editTextExperience.getText().toString();
        String mobileNo = editTextMobileNo.getText().toString();
        String city = editTextCity.getText().toString();
        String district = editTextDistrict.getText().toString();
        String state = editTextState.getText().toString();

        // Perform validation checks
        if (TextUtils.isEmpty(experience) || TextUtils.isEmpty(mobileNo) ||
                TextUtils.isEmpty(city) || TextUtils.isEmpty(district) || TextUtils.isEmpty(state)) {
            // Show error message indicating required fields
            Toast.makeText(SignUpDermatologistActivity2.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            showAlert("Please enter a password with at least 6 characters");
            return;
        }

        // Hash the password
        String hashedPassword = hashPassword(password);

        if (!TextUtils.isEmpty(licenseUrl)) {
            Map<String, Object> dermatologist = new HashMap<>();
            dermatologist.put("Name", getIntent().getStringExtra("name"));
            dermatologist.put("Email", getIntent().getStringExtra("email"));
            dermatologist.put("RegID", getIntent().getStringExtra("registrationNo"));
            dermatologist.put("RegYear", getIntent().getStringExtra("registrationYear"));
            dermatologist.put("StateMedicalCouncil", getIntent().getStringExtra("stateMedicalCouncil"));
            dermatologist.put("Exp", experience);
            dermatologist.put("Mobile", mobileNo);
            dermatologist.put("City", city);
            dermatologist.put("District", district);
            dermatologist.put("State", state);
            dermatologist.put("Password", hashedPassword); // Storing hashed password for security
            dermatologist.put("LicenseURL", licenseUrl);

            // Add a new document with a generated ID
            db.collection("dermatologist")
                    .add(dermatologist)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(SignUpDermatologistActivity2.this, "Dermatologist added successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful signup
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpDermatologistActivity2.this, "Error adding dermatologist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Show an error message if license URL is empty
            Toast.makeText(SignUpDermatologistActivity2.this, "License URL is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Add password bytes to digest
            md.update(password.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}



