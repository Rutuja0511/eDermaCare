package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginDermatologistActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dermatologist);

        editTextEmail = findViewById(R.id.dermatologist_login_email);
        editTextPassword = findViewById(R.id.dermatologist_login_password);
        buttonLogin = findViewById(R.id.dermatologist_login_button);
        textViewSignUp = findViewById(R.id.signupredirect);
        mFirestore = FirebaseFirestore.getInstance();

        // Handle login button click
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDermatologist();
            }
        });

        // Handle sign up text click
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to sign up activity
                Intent intent = new Intent(LoginDermatologistActivity.this, SignUpDermatologistActivity1.class);
                startActivity(intent);
            }
        });
    }

    private void loginDermatologist() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email is present in Firestore
        mFirestore.collection("dermatologist")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String hashedPassword = hashPassword(password);
                            String storedPassword = document.getString("Password");
                            String userId = document.getId();
                            if (hashedPassword.equals(storedPassword)) {
                                Toast.makeText(LoginDermatologistActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                redirectToDermatologist(email, userId); // Pass both email and userId
                                return;
                            }
                        }
                        Toast.makeText(LoginDermatologistActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(LoginDermatologistActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

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

    private void redirectToDermatologist(String userEmail, String userId) {
        Intent intent = new Intent(LoginDermatologistActivity.this, Activitydash2.class);
//        Intent intent = new Intent(LoginDermatologistActivity.this, DExtraDetails.class);
        intent.putExtra("user_email", userEmail);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}
