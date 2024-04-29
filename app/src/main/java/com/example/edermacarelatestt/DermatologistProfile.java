package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
public class DermatologistProfile extends AppCompatActivity {

    Button mybutton, mybutton1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dermatologist_profile);

        mybutton = findViewById(R.id.myButton);
        mybutton1 = findViewById(R.id.clinicbutton);

        mybutton.setOnClickListener(v -> {
            // Navigate to FragmentProfile2 activity
            Intent intent = new Intent(DermatologistProfile.this, profileFragment2.class);
            startActivity(intent);
        });
        mybutton1.setOnClickListener(v -> {
            // Navigate to FragmentProfile2 activity
            Intent intent = new Intent(DermatologistProfile.this, clinicProfile.class);
            startActivity(intent);
        });
    }
}
