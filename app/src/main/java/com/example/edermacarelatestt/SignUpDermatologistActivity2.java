package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignUpDermatologistActivity2 extends AppCompatActivity {

    EditText editTextPasswordD, editTextExperience, editTextMobileNo, editTextCity, editTextDistrict, editTextState;

    Button buttonSignUpD;
    private FirebaseFirestore db;
    private String licenseUrl;
    private String verified;
    private String qualification;
    private String qualificationYear;
    private String universityName;
    private String permanent_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity2_signup_dermatologist);
        editTextPasswordD = findViewById(R.id.dermatologist_signup_password);
        editTextCity = findViewById(R.id.dermatologist_city);
        editTextDistrict = findViewById(R.id.dermatologist_district);
        editTextState = findViewById(R.id.dermatologist_state);
        editTextMobileNo = findViewById(R.id.dermatologist_mobileNo);
        editTextExperience = findViewById(R.id.dermatologist_experience);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("licenseUrl")) {
            licenseUrl = intent.getStringExtra("licenseUrl"); // Get the download URL
        }

        buttonSignUpD = findViewById(R.id.dermatologist_signup_button);

        buttonSignUpD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpDermatologistActivity2.this, "We will Verify your details and contact you within 24 hours", Toast.LENGTH_SHORT).show();
                signUpDermatologist();
            }
        });

    }

    private void showVerificationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpDermatologistActivity2.this);
        builder.setMessage("Thank you for signing up. We will verify your details and contact you within 24 hours.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void signUpDermatologist() {
        String password = editTextPasswordD.getText().toString();
        String experience = editTextExperience.getText().toString();
        String mobileNo = editTextMobileNo.getText().toString();
        String city = editTextCity.getText().toString();
        String district = editTextDistrict.getText().toString();
        String state = editTextState.getText().toString();

        if (TextUtils.isEmpty(experience) || TextUtils.isEmpty(mobileNo) ||
                TextUtils.isEmpty(city) || TextUtils.isEmpty(district) || TextUtils.isEmpty(state)) {
            Toast.makeText(SignUpDermatologistActivity2.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            showAlert("Please enter a password with at least 6 characters");
            return;
        }

        String hashedPassword = hashPassword(password);

        String registrationNo = getIntent().getStringExtra("registrationNo");
        String yearOfRegistration = getIntent().getStringExtra("registrationYear");
        String stateMedicalCouncil = getIntent().getStringExtra("stateMedicalCouncil");

        DermatologistVerification.performVerification(registrationNo, yearOfRegistration, stateMedicalCouncil, new DermatologistVerification.VerificationCallback() {
            @Override
            public void onVerificationComplete(JSONObject result) {
                try {
                    JSONObject resultObj = result.getJSONObject("result");
                    System.out.println(resultObj);
                    JSONObject sourceOutput = resultObj.getJSONObject("source_output");
                    String status = sourceOutput.getString("status");
                    verified = status.equals("id_found") ? "true" : "false";
                    if (status.equals("id_found")) {
                        JSONObject imrDetails = sourceOutput.getJSONObject("imr_details");
                        qualification = imrDetails.getString("qualification");
                        qualificationYear = imrDetails.getString("qualification_year");
                        universityName = imrDetails.getString("university_name");
                        permanent_address=imrDetails.getString("permanent_address");
                    }
                    Log.d(TAG, "Verification Status: " + verified);
                    System.out.println(verified);
                    Log.d(TAG, "Qualification: " + qualification);
                    Log.d(TAG, "Qualification Year: " + qualificationYear);
                    Log.d(TAG, "University Name: " + universityName);
                    Log.d(TAG, "add: " + permanent_address);

                    // Post data to Firebase only after verification is complete
                    postDataToFirebase();
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException occurred: " + e.getMessage());
                }
            }

            @Override
            public void onVerificationFailed(String errorMessage) {
                Log.e(TAG, "Verification failed: " + errorMessage);
            }
        });
    }

    private void postDataToFirebase() {
        // Check if necessary values are available and post data to Firebase
        if (!TextUtils.isEmpty(licenseUrl) && verified.equals("true")) {
            Map<String, Object> dermatologist = new HashMap<>();
            dermatologist.put("Name", getIntent().getStringExtra("name"));
            dermatologist.put("Email", getIntent().getStringExtra("email"));
            dermatologist.put("RegID", getIntent().getStringExtra("registrationNo"));
            dermatologist.put("RegYear", getIntent().getStringExtra("registrationYear"));
            dermatologist.put("StateMedicalCouncil", getIntent().getStringExtra("stateMedicalCouncil"));
            dermatologist.put("Exp", editTextExperience.getText().toString());
            dermatologist.put("Mobile", editTextMobileNo.getText().toString());
            dermatologist.put("City", editTextCity.getText().toString());
            dermatologist.put("District", editTextDistrict.getText().toString());
            dermatologist.put("State", editTextState.getText().toString());
            dermatologist.put("Password", hashPassword(editTextPasswordD.getText().toString()));
            dermatologist.put("verified", verified);
            dermatologist.put("qualification", qualification);
            dermatologist.put("qualificationYear", qualificationYear);
            dermatologist.put("universityName", universityName);
            dermatologist.put("LicenseURL", licenseUrl);
            dermatologist.put("permanent_address",permanent_address);

            db.collection("dermatologist")
                    .add(dermatologist)
                    .addOnSuccessListener(documentReference -> {
                        showVerificationAlertDialog();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpDermatologistActivity2.this, "Error adding dermatologist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SignUpDermatologistActivity2.this, "License URL is empty or verification failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
