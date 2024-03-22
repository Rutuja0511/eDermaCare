package com.example.edermacarelatestt;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SignUpPatientActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextDOB, editTextMobile;
    TextView loginRedirect  ;
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
    }

    private void redirectToLogin() {
        Intent intent = new Intent(SignUpPatientActivity.this, LoginPatientActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity to prevent user from coming back to it using back button
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
//        System.out.println(name);
//        System.out.println(email);
//        System.out.println(password);
//        System.out.println(dob);
//        System.out.println(mobile);
//        System.out.println(hashedPassword);


        // Check if any field is empty, if yes, display an alert
        if (name.isEmpty()) {
            showAlert("Please enter your name");
            return;
        }

        // Check if email is empty or invalid, display an alert
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlert("Please enter a valid email address");
            return;
        }

        // Check if password is empty or less than 6 characters, display an alert
        if (password.isEmpty() || password.length() < 6) {
            showAlert("Please enter a password with at least 6 characters");
            return;
        }

        // Check if dob is empty, display an alert
        if (dob.isEmpty()) {
            showAlert("Please enter your date of birth");
            return;
        }

        // Check if mobile is empty or not valid, display an alert
        if (mobile.isEmpty() || !android.util.Patterns.PHONE.matcher(mobile).matches()) {
            showAlert("Please enter a valid mobile number");
            return;
        }

        // If all fields are valid, proceed with signup process
        // signUpManager.signUp(MainActivity.this, name, email, dob, mobile, password, username);
        signUpManager.signUp(SignUpPatientActivity.this, name, email, dob, mobile, hashedPassword);

    }

    // Method to display an alert






//        signUpManager.signUp(SignUpPatientActivity.this, name, email, dob, mobile, password, username);

}
