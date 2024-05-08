package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DermatologistProfile extends AppCompatActivity {

    Button mybutton, mybutton1;
    String userEmail; // Variable to store user email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dermatologist_profile);

        // Retrieve user email from intent
        userEmail = getIntent().getStringExtra("user_email");

        mybutton = findViewById(R.id.myButton);
        mybutton1 = findViewById(R.id.clinicbutton);

        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if userEmail is not null before proceeding
                if (userEmail != null) {
                    // Create an Intent to navigate to the profileActivity2
                    Intent intent = new Intent(DermatologistProfile.this, ProfileFragment2.class);

                    // Pass the user email as an extra to the intent
                    intent.putExtra("user_email", userEmail);

                    // Start the profileActivity2
                    startActivity(intent);
                } else {
                    // Handle the case where userEmail is null
                    // Show a toast message
                    Toast.makeText(DermatologistProfile.this, "User email not found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mybutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Activitydash2 activity with clinicProfile fragment specified
                Intent intent = new Intent(DermatologistProfile.this, Activitydash2.class);
                intent.putExtra("fragment", "clinicProfile");
                startActivity(intent);
            }
        });
    }
}