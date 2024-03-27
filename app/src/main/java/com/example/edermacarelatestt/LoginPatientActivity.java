package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPatientActivity extends AppCompatActivity {

    private static final String TAG = "PatientLoginActivity";
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupRedirectText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_patient);

        emailEditText = findViewById(R.id.patient_login_email);
        passwordEditText = findViewById(R.id.patient_login_password);
        loginButton = findViewById(R.id.patient_login_button);
        signupRedirectText = findViewById(R.id.signupredirect);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(v -> loginUser());
        signupRedirectText.setOnClickListener(v -> redirectToSignup());
    }

    private void redirectToSignup() {
        Intent intent = new Intent(LoginPatientActivity.this, SignUpPatientActivity.class);
        startActivity(intent);
    }


    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email is present in Firestore
        mFirestore.collection("patients")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Email is present in Firestore, now hash the password
                            String hashedPassword = hashPassword(password);
                            String storedPassword = document.getString("Password"); // Assuming "password" is the field storing hashed passwords

                            if (hashedPassword.equals(storedPassword)) {
                                // Password matches, login successful
                                Toast.makeText(LoginPatientActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                // Proceed with further actions (e.g., navigating to another activity)
                                Intent intent = new Intent(LoginPatientActivity.this, Activitydash.class);
                                startActivity(intent);
                                return;
                            }
                        }
                        // If loop completes without finding a matching email or password, login failed
                        Toast.makeText(LoginPatientActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(LoginPatientActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });


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
