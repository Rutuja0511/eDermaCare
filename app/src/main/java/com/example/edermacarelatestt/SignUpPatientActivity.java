package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpPatientActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextDOB, editTextMobile;
    TextView loginRedirect;
    Button buttonSignUp;
    PatientSignUpManager signUpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_patient);
        signUpManager = new PatientSignUpManager();
        editTextName = findViewById(R.id.patient_signup_name);
        editTextEmail = findViewById(R.id.patient_signup_email);
        editTextPassword = findViewById(R.id.patient_signup_password);
        editTextDOB = findViewById(R.id.patient_signup_dob);
        editTextMobile = findViewById(R.id.patient_signup_mobile);
        buttonSignUp = findViewById(R.id.patient_signup_button);
        loginRedirect = findViewById(R.id.loginredirect);

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpManager.showDatePickerDialog(SignUpPatientActivity.this, editTextDOB);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToLogin();
            }
        });

        Spinner spinnerGender = findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(SignUpPatientActivity.this, LoginPatientActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity to prevent user from coming back to it using back button
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

    private void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String dob = editTextDOB.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();
        String hashedPassword = hashPassword(password);

        Spinner spinnerGender = findViewById(R.id.spinner_gender);
        String gender = spinnerGender.getSelectedItem().toString();

        if (name.isEmpty()) {
            showAlert("Please enter your name");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlert("Please enter a valid email address");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            showAlert("Please enter a password with at least 6 characters");
            return;
        }

        if (dob.isEmpty()) {
            showAlert("Please enter your date of birth");
            return;
        }

        if (mobile.isEmpty() || !android.util.Patterns.PHONE.matcher(mobile).matches()) {
            showAlert("Please enter a valid mobile number");
            return;
        }

        if (gender.equals("Select Gender")) {
            showAlert("Please select your gender");
            return;
        }
//        signUpManager.signUp(SignUpPatientActivity.this, name, email, dob, mobile, hashedPassword, gender);

        // Call signUp method
        boolean signUpSuccessful = signUpManager.signUp(SignUpPatientActivity.this, name, email, dob, mobile, hashedPassword, gender);

        if (signUpSuccessful) {
            // If signup is successful, start LoginPatientActivity
            Intent intent = new Intent(SignUpPatientActivity.this, LoginPatientActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish the current activity to prevent user from coming back to it using back button
        }else {
            // Handle unsuccessful signup
            showAlert("Signup failed. Please try again.");
        }
    }


}
